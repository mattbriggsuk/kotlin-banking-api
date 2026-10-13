package com.example.banking.business

import com.example.banking.domain.enums.TransactionType
import com.example.banking.domain.model.Account
import com.example.banking.domain.model.Transaction
import com.example.banking.domain.model.TransferResult
import com.example.banking.domain.repository.AccountRepository
import com.example.banking.domain.repository.CustomerRepository
import com.example.banking.domain.service.AccountService
import com.example.banking.utils.AccountUtils
import com.example.banking.utils.TransactionUtils
import com.example.banking.utils.ValidationUtils
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.Instant

/** Implements account lifecycle and balance operations. */
@Service
class AccountServiceImpl(
    private val accountRepository: AccountRepository,
    private val customerRepository: CustomerRepository,
) : AccountService {

    /** Opens an account for an existing customer with a zero balance. */
    @Synchronized
    override fun create(customerNumber: String): Account {
        require(ValidationUtils.isValidCustomerNumber(customerNumber)) {
            "Customer number must be a valid 16-digit number"
        }

        // An account can only be opened for a registered customer.
        customerRepository.getCustomer(customerNumber)
            ?: throw NoSuchElementException("Customer not found")

        val account = Account(
            AccountUtils.generateAccountNumber(),
            customerNumber,
            BigDecimal("0.00"),
            false,
        )
        return accountRepository.save(account)
    }

    /** Closes an open account after confirming that its balance is zero. */
    @Synchronized
    override fun close(accountNumber: String): Account {
        val account = openAccount(accountNumber)
        check(account.balance.signum() == 0) {
            "Account balance must be zero before closing"
        }

        account.closed = true
        return accountRepository.save(account)
    }

    @Synchronized
    override fun deposit(transaction: Transaction): Account =
        changeBalance(transaction, TransactionType.DEPOSIT, add = true)

    @Synchronized
    override fun withdraw(transaction: Transaction): Account =
        changeBalance(transaction, TransactionType.WITHDRAWAL, add = false)

    private fun changeBalance(
        transaction: Transaction,
        type: TransactionType,
        add: Boolean,
    ): Account {
        TransactionUtils.validateTransaction(transaction, type)
        transaction.transactionDate = Instant.now()

        val amount = requireNotNull(transaction.amount)
        val account = openAccount(requireNotNull(transaction.targetAccountNumber))
        if (!add) {
            TransactionUtils.requireFunds(account, amount)
        }

        account.balance = if (add) account.balance + amount else account.balance - amount
        return accountRepository.save(account)
    }

    /**
     * Transfers funds between two open accounts.
     *
     * The instance lock prevents this service from overlapping balance updates. The repository
     * then stores both updated accounts under one repository lock. This is not a database transaction.
     */
    @Synchronized
    override fun transfer(transaction: Transaction): TransferResult {
        TransactionUtils.validateTransaction(transaction, TransactionType.TRANSFER)
        transaction.transactionDate = Instant.now()

        val amount = requireNotNull(transaction.amount)
        val from = openAccount(requireNotNull(transaction.sourceAccountNumber))
        val to = openAccount(requireNotNull(transaction.targetAccountNumber))
        TransactionUtils.requireFunds(from, amount)

        // Change both objects only after every validation check has passed.
        from.balance -= amount
        to.balance += amount
        accountRepository.transferBalance(from, to)

        return TransferResult(from, to)
    }

    /** Returns an existing account and rejects closed accounts. */
    private fun openAccount(accountNumber: String): Account {
        require(ValidationUtils.isValidAccountNumber(accountNumber)) {
            "Account number must be valid"
        }

        val account = accountRepository.getAccount(accountNumber)
            ?: throw NoSuchElementException("Account not found")
        check(!account.closed) { "Account is closed" }
        return account
    }
}

package com.example.banking.business

import com.example.banking.domain.enums.AccountType
import com.example.banking.domain.enums.TransactionType
import com.example.banking.domain.exceptions.AccessDeniedException
import com.example.banking.domain.model.Account
import com.example.banking.domain.model.Transaction
import com.example.banking.domain.model.TransferResult
import com.example.banking.domain.model.UserSession
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

    @Synchronized
    override fun create(userSession: UserSession, accountType: AccountType): Account {
        val customerNumber = userSession.customerNumber
        customerRepository.getCustomer(customerNumber)
            ?: throw NoSuchElementException("Customer not found")

        val account = Account(
            AccountUtils.generateAccountNumber(),
            customerNumber,
            BigDecimal("0.00"),
            false,
            accountType,
        )
        return accountRepository.save(account)
    }

    @Synchronized
    override fun close(userSession: UserSession, accountNumber: String): Account {
        val account = getOwnedAccount(userSession, accountNumber)
        check(account.balance.signum() == 0) {
            "Account balance must be zero before closing"
        }

        account.closed = true
        return accountRepository.save(account)
    }

    @Synchronized
    override fun deposit(userSession: UserSession, transaction: Transaction): Account {
        TransactionUtils.validateTransaction(transaction, TransactionType.DEPOSIT)
        transaction.transactionDate = Instant.now()
        val amount = requireNotNull(transaction.amount)
        val account = getOwnedAccount(userSession, requireNotNull(transaction.targetAccountNumber))
        account.balance += amount
        return accountRepository.save(account)
    }

    @Synchronized
    override fun withdraw(userSession: UserSession, transaction: Transaction): Account {
        TransactionUtils.validateTransaction(transaction, TransactionType.WITHDRAWAL)
        transaction.transactionDate = Instant.now()
        val amount = requireNotNull(transaction.amount)
        val account = getOwnedAccount(userSession, requireNotNull(transaction.targetAccountNumber))
        TransactionUtils.requireFunds(account, amount)
        account.balance -= amount
        return accountRepository.save(account)
    }

    @Synchronized
    override fun transfer(userSession: UserSession, transaction: Transaction): TransferResult {
        TransactionUtils.validateTransaction(transaction, TransactionType.TRANSFER)
        transaction.transactionDate = Instant.now()
        val amount = requireNotNull(transaction.amount)

        val from = getOwnedAccount(userSession, requireNotNull(transaction.sourceAccountNumber))
        val to = getAccount(requireNotNull(transaction.targetAccountNumber))
        TransactionUtils.requireFunds(from, amount)

        from.balance -= amount
        to.balance += amount
        accountRepository.transferBalance(from, to)
        return TransferResult(from, to)
    }

    private fun getOwnedAccount(userSession: UserSession?, accountNumber: String): Account {
        val account = getAccount(accountNumber)
        if (userSession?.customerNumber != account.customerNumber) {
            throw AccessDeniedException()
        }
        return account
    }

    private fun getAccount(accountNumber: String): Account {
        require(ValidationUtils.isValidAccountNumber(accountNumber)) {
            "Account number must be valid"
        }
        val account = accountRepository.getAccount(accountNumber)
            ?: throw NoSuchElementException("Account not found")
        check(!account.closed) { "Account is closed" }
        return account
    }
}

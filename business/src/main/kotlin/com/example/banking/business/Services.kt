package com.example.banking.business

import com.example.banking.domain.enums.TransactionType
import com.example.banking.domain.model.*
import com.example.banking.domain.repository.*
import com.example.banking.domain.service.*
import com.example.banking.utils.*
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.Instant

@Service
class AccountServiceImpl(private val accountRepository: AccountRepository, private val customerRepository: CustomerRepository) : AccountService {
    @Synchronized override fun create(customerNumber: String): Account {
        require(ValidationUtils.isValidCustomerNumber(customerNumber)) { "Customer number must be a valid 16-digit number" }
        customerRepository.getCustomer(customerNumber) ?: throw NoSuchElementException("Customer not found")
        return accountRepository.save(Account(AccountUtils.generateAccountNumber(), customerNumber, BigDecimal("0.00"), false))
    }
    @Synchronized override fun close(accountNumber: String): Account {
        val account = openAccount(accountNumber)
        check(account.balance.signum() == 0) { "Account balance must be zero before closing" }
        account.closed = true
        return accountRepository.save(account)
    }
    @Synchronized override fun deposit(transaction: Transaction): Account = changeBalance(transaction, TransactionType.DEPOSIT, true)
    @Synchronized override fun withdraw(transaction: Transaction): Account = changeBalance(transaction, TransactionType.WITHDRAWAL, false)
    private fun changeBalance(transaction: Transaction, type: TransactionType, add: Boolean): Account {
        TransactionUtils.validateTransaction(transaction, type); transaction.transactionDate = Instant.now()
        val amount = requireNotNull(transaction.amount); val account = openAccount(requireNotNull(transaction.targetAccountNumber))
        if (!add) TransactionUtils.requireFunds(account, amount)
        account.balance = if (add) account.balance + amount else account.balance - amount
        return accountRepository.save(account)
    }
    @Synchronized override fun transfer(transaction: Transaction): TransferResult {
        TransactionUtils.validateTransaction(transaction, TransactionType.TRANSFER); transaction.transactionDate = Instant.now()
        val amount = requireNotNull(transaction.amount)
        val from = openAccount(requireNotNull(transaction.sourceAccountNumber)); val to = openAccount(requireNotNull(transaction.targetAccountNumber))
        TransactionUtils.requireFunds(from, amount); from.balance -= amount; to.balance += amount
        accountRepository.transferBalance(from, to); return TransferResult(from, to)
    }
    private fun openAccount(number: String): Account {
        require(ValidationUtils.isValidAccountNumber(number)) { "Account number must be valid" }
        val account = accountRepository.getAccount(number) ?: throw NoSuchElementException("Account not found")
        check(!account.closed) { "Account is closed" }; return account
    }
}

@Service
class CustomerServiceImpl(private val customerRepository: CustomerRepository, private val accountRepository: AccountRepository) : CustomerService {
    override fun getCustomer(customerNumber: String): Customer { validateNumber(customerNumber); return customerRepository.getCustomer(customerNumber) ?: throw NoSuchElementException("Customer not found") }
    override fun getCustomerAccounts(customerNumber: String): List<Account> { getCustomer(customerNumber); return accountRepository.getCustomerAccounts(customerNumber) }
    @Synchronized override fun register(customer: Customer): Customer {
        require(ValidationUtils.isValidCustomerObject(customer)) { "Customer and all required details must be provided" }
        return customerRepository.save(Customer(AccountUtils.generateAccountNumber(), customer.firstName, customer.lastName, customer.email))
    }
    private fun validateNumber(number: String) = require(ValidationUtils.isValidCustomerNumber(number)) { "Customer number must be a valid 16-digit number" }
}

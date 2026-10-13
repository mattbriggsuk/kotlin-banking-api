package com.example.banking.repository

import com.example.banking.domain.model.*
import com.example.banking.domain.repository.*
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap

@Repository
class AccountRepositoryImpl : AccountRepository {
    private val accounts = mutableMapOf<String, Account>()
    @Synchronized override fun getAccount(accountNumber: String) = accounts[accountNumber]
    @Synchronized override fun getCustomerAccounts(customerNumber: String) = accounts.values.filter { it.customerNumber == customerNumber }
    @Synchronized override fun save(account: Account) = account.also { accounts[it.accountNumber] = it }
    @Synchronized override fun transferBalance(fromAccount: Account, toAccount: Account) { accounts[fromAccount.accountNumber] = fromAccount; accounts[toAccount.accountNumber] = toAccount }
}

@Repository
class CustomerRepositoryImpl : CustomerRepository {
    private val customers = ConcurrentHashMap<String, Customer>()
    override fun getCustomer(customerNumber: String) = customers[customerNumber]
    override fun save(customer: Customer): Customer = customer.also { customers[requireNotNull(it.customerNumber)] = it }
}

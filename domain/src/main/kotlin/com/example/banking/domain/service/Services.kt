package com.example.banking.domain.service
import com.example.banking.domain.model.*
interface AccountService { fun create(customerNumber: String): Account; fun close(accountNumber: String): Account; fun deposit(transaction: Transaction): Account; fun withdraw(transaction: Transaction): Account; fun transfer(transaction: Transaction): TransferResult }
interface CustomerService { fun register(customer: Customer): Customer; fun getCustomer(customerNumber: String): Customer; fun getCustomerAccounts(customerNumber: String): List<Account> }

package com.example.banking.domain.service

import com.example.banking.domain.model.Account
import com.example.banking.domain.model.Customer

interface CustomerService {
    fun register(customer: Customer): Customer
    fun getCustomer(customerNumber: String): Customer
    fun getCustomerAccounts(customerNumber: String): List<Account>
}

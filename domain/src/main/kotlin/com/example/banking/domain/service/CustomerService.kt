package com.example.banking.domain.service

import com.example.banking.domain.model.Account
import com.example.banking.domain.model.Customer
import com.example.banking.domain.model.CustomerRegistration
import com.example.banking.domain.model.UserSession

interface CustomerService {
    fun register(customer: Customer): CustomerRegistration
    fun getCustomer(userSession: UserSession, customerNumber: String): Customer
    fun getCustomerAccounts(userSession: UserSession, customerNumber: String): List<Account>
}

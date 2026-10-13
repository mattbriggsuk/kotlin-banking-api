package com.example.banking.domain.repository

import com.example.banking.domain.model.Customer

interface CustomerRepository {
    fun save(customer: Customer, pinHash: String): Customer
    fun getCustomer(customerNumber: String): Customer?
    fun getCustomerByEmail(email: String): Customer?
    fun getPinHash(customerNumber: String): String?
}

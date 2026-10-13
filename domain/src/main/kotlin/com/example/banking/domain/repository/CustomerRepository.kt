package com.example.banking.domain.repository

import com.example.banking.domain.model.Customer

interface CustomerRepository {
    fun save(customer: Customer): Customer
    fun getCustomer(customerNumber: String): Customer?
}

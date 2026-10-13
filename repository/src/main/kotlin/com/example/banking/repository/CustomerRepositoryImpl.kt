package com.example.banking.repository

import com.example.banking.domain.model.Customer
import com.example.banking.domain.repository.CustomerRepository
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap

/** In-memory customer storage. Customers are lost when the application stops. */
@Repository
class CustomerRepositoryImpl : CustomerRepository {

    // Customer numbers provide direct lookup keys, and ConcurrentHashMap supports request threads safely.
    private val customers = ConcurrentHashMap<String, Customer>()

    override fun getCustomer(customerNumber: String): Customer? = customers[customerNumber]

    override fun save(customer: Customer): Customer =
        customer.also { customers[requireNotNull(it.customerNumber)] = it }
}

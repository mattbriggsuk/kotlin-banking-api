package com.example.banking.repository

import com.example.banking.domain.model.Customer
import com.example.banking.domain.repository.CustomerRepository
import org.springframework.stereotype.Repository
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

/** In-memory customer storage. Credentials never form part of public customer DTOs. */
@Repository
class CustomerRepositoryImpl : CustomerRepository {
    private class StoredCustomer(val customer: Customer, val pinHash: String)
    private val customers = ConcurrentHashMap<String, StoredCustomer>()
    private val emails = ConcurrentHashMap<String, String>()

    override fun getCustomer(customerNumber: String): Customer? = customers[customerNumber]?.customer
    override fun getCustomerByEmail(email: String): Customer? =
        emails[email.trim().lowercase(Locale.ROOT)]?.let(::getCustomer)
    override fun getPinHash(customerNumber: String): String? = customers[customerNumber]?.pinHash

    @Synchronized
    override fun save(customer: Customer, pinHash: String): Customer {
        val number = customer.customerNumber
            ?: throw IllegalArgumentException("Customer number is required")
        val email = customer.email.trim().lowercase(Locale.ROOT)
        check(!emails.containsKey(email)) { "A customer with this email already exists. Log in instead." }
        check(!customers.containsKey(number)) { "Customer number already exists" }
        customers[number] = StoredCustomer(customer, pinHash)
        emails[email] = number
        return customer
    }
}

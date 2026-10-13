package com.example.banking.domain.dto

import com.example.banking.domain.model.Customer

/** API representation of a customer, kept separate from the domain model. */
data class CustomerDto(
    val customerNumber: String?,
    val firstName: String,
    val lastName: String,
    val email: String,
) {
    companion object {
        fun fromDomain(customer: Customer) = CustomerDto(
            customer.customerNumber,
            customer.firstName,
            customer.lastName,
            customer.email,
        )
    }
}

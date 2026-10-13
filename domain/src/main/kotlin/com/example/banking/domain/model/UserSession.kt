package com.example.banking.domain.model

/** The customer and opaque token represented by an authenticated session. */
data class UserSession(
    val customer: Customer,
    val sessionToken: String,
) {
    val customerNumber: String
        get() = requireNotNull(customer.customerNumber)
}

package com.example.banking.domain.dto

import com.example.banking.domain.model.CustomerRegistration

data class CustomerRegisterResponse(
    val customer: CustomerDto,
    val pin: String,
    val sessionToken: String,
) : ResponseBase() {
    override fun toString() = "CustomerRegisterResponse(credentials=redacted)"

    companion object {
        fun fromDomain(registration: CustomerRegistration, sessionToken: String) = CustomerRegisterResponse(
            CustomerDto.fromDomain(registration.customer), registration.pin, sessionToken,
        )
    }
}

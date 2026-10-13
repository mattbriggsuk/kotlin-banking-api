package com.example.banking.domain.dto

data class CustomerLoginResponse(val customer: CustomerDto, val sessionToken: String) : ResponseBase() {
    override fun toString() = "CustomerLoginResponse(credentials=redacted)"
}

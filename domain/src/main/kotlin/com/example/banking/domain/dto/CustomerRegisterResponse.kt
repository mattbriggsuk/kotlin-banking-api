package com.example.banking.domain.dto

import com.example.banking.domain.model.Customer

data class CustomerRegisterResponse(var customer: CustomerDto) : ResponseBase() {
    companion object {
        /** Maps the registered domain customer into an API response. */
        fun fromDomain(customer: Customer) = CustomerRegisterResponse(CustomerDto.fromDomain(customer))
    }
}

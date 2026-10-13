package com.example.banking.domain.dto

import com.example.banking.domain.model.Customer

data class CustomerResponse(var customer: CustomerDto) : ResponseBase() {
    companion object {
        /** Maps the domain customer into an API response. */
        fun fromDomain(customer: Customer) = CustomerResponse(CustomerDto.fromDomain(customer))
    }
}

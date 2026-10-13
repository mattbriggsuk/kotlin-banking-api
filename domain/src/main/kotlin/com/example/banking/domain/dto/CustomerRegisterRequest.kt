package com.example.banking.domain.dto

import com.example.banking.domain.model.Customer
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull

data class CustomerRegisterRequest(
    @field:NotNull
    @field:Valid
    var customer: Customer? = null,
) : RequestBase()

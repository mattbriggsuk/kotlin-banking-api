package com.example.banking.domain.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class AccountCreateRequest(
    @field:NotBlank
    @field:Pattern(regexp = "[0-9]{16}", message = "must contain exactly 16 digits")
    var customerNumber: String = "",
) : RequestBase()

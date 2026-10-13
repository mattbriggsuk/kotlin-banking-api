package com.example.banking.domain.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class AccountCloseRequest(
    @field:NotBlank
    @field:Pattern(regexp = "[0-9]{16}", message = "must contain exactly 16 digits")
    var accountNumber: String = "",
) : RequestBase()

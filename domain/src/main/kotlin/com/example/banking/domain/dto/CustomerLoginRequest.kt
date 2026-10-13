package com.example.banking.domain.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

/** Identifier can be a customer number or email. Never include credentials in logs. */
data class CustomerLoginRequest(
    @field:NotBlank @field:Size(max = 254) val identifier: String = "",
    @field:Pattern(regexp = "[0-9]{6}", message = "must contain exactly six digits") val pin: String = "",
) : RequestBase() {
    override fun toString() = "CustomerLoginRequest(credentials=redacted)"
}

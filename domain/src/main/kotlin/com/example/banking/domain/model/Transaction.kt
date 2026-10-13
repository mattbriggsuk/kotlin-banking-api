package com.example.banking.domain.model

import com.example.banking.domain.enums.TransactionType
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Digits
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import java.math.BigDecimal
import java.time.Instant

/** A requested money movement; [sourceAccountNumber] is required only for transfers. */
data class Transaction(
    @field:NotNull
    var transactionType: TransactionType? = null,
    @field:NotNull
    @field:DecimalMin(value = "0", inclusive = false)
    @field:Digits(integer = 16, fraction = 2)
    var amount: BigDecimal? = null,
    @field:Pattern(regexp = "[0-9]{16}", message = "must contain exactly 16 digits")
    var sourceAccountNumber: String? = null,
    @field:NotBlank
    @field:Pattern(regexp = "[0-9]{16}", message = "must contain exactly 16 digits")
    var targetAccountNumber: String? = null,
    var transactionDate: Instant? = null,
)

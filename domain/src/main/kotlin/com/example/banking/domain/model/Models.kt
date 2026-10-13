package com.example.banking.domain.model

import com.example.banking.domain.enums.TransactionType
import jakarta.validation.constraints.*
import java.math.BigDecimal
import java.time.Instant

data class Account(val accountNumber: String, val customerNumber: String, var balance: BigDecimal, var closed: Boolean)
data class Customer(val customerNumber: String? = null, @field:NotBlank val firstName: String = "", @field:NotBlank val lastName: String = "", @field:NotBlank @field:Email val email: String = "")
data class Transaction(
    @field:NotNull var transactionType: TransactionType? = null,
    @field:NotNull @field:DecimalMin(value = "0", inclusive = false) @field:Digits(integer = 16, fraction = 2) var amount: BigDecimal? = null,
    @field:Pattern(regexp = "[0-9]{16}", message = "must contain exactly 16 digits") var sourceAccountNumber: String? = null,
    @field:NotBlank @field:Pattern(regexp = "[0-9]{16}", message = "must contain exactly 16 digits") var targetAccountNumber: String? = null,
    var transactionDate: Instant? = null
)
data class TransferResult(val fromAccount: Account, val toAccount: Account)

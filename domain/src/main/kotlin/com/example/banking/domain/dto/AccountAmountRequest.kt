package com.example.banking.domain.dto

import com.example.banking.domain.model.Transaction
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull

/** HTTP request wrapper shared by deposits and withdrawals. */
data class AccountAmountRequest(
    @field:NotNull
    @field:Valid
    var transaction: Transaction? = null,
) : RequestBase()

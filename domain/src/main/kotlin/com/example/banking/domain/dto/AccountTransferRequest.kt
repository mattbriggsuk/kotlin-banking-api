package com.example.banking.domain.dto

import com.example.banking.domain.model.Transaction
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull

data class AccountTransferRequest(
    @field:NotNull
    @field:Valid
    var transaction: Transaction? = null,
) : RequestBase()

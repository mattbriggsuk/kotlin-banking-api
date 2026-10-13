package com.example.banking.domain.dto

import com.example.banking.domain.enums.AccountType
import jakarta.validation.constraints.NotNull

data class AccountCreateRequest(
    @field:NotNull
    var accountType: AccountType = AccountType.CURRENT,
) : RequestBase()

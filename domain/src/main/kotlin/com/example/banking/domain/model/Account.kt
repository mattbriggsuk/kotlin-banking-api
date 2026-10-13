package com.example.banking.domain.model

import com.example.banking.domain.enums.AccountType
import java.math.BigDecimal

data class Account(
    val accountNumber: String,
    val customerNumber: String,
    var balance: BigDecimal,
    var closed: Boolean,
    val accountType: AccountType = AccountType.CURRENT,
)

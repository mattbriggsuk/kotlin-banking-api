package com.example.banking.domain.model

import java.math.BigDecimal

data class Account(
    val accountNumber: String,
    val customerNumber: String,
    var balance: BigDecimal,
    var closed: Boolean,
)

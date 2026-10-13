package com.example.banking.domain.model

/** The account states after a successful transfer. */
data class TransferResult(
    val fromAccount: Account,
    val toAccount: Account,
)

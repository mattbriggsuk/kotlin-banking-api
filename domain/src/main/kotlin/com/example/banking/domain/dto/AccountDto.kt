package com.example.banking.domain.dto

import com.example.banking.domain.model.Account
import java.math.BigDecimal

/** API representation of an account, kept separate from the mutable domain model. */
data class AccountDto(
    val accountNumber: String,
    val customerNumber: String,
    val balance: BigDecimal,
    val closed: Boolean,
) {
    companion object {
        fun fromDomain(account: Account) = AccountDto(
            account.accountNumber,
            account.customerNumber,
            account.balance,
            account.closed,
        )
    }
}

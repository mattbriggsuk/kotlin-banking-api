package com.example.banking.domain.dto

import com.example.banking.domain.model.Account

data class CustomerAccountResponse(
    var customerNumber: String,
    var accounts: List<AccountDto>,
) : ResponseBase() {
    companion object {
        /** Maps a customer's domain accounts into API DTOs. */
        fun fromDomain(customerNumber: String, accounts: List<Account>) = CustomerAccountResponse(
            customerNumber,
            accounts.map(AccountDto::fromDomain),
        )
    }
}

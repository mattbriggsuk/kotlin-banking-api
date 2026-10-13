package com.example.banking.domain.dto

import com.example.banking.domain.model.Account

data class AccountAmountResponse(var account: AccountDto) : ResponseBase() {
    companion object {
        fun fromDomain(account: Account) = AccountAmountResponse(AccountDto.fromDomain(account))
    }
}

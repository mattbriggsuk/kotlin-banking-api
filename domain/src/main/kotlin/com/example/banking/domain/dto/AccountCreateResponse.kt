package com.example.banking.domain.dto

import com.example.banking.domain.model.Account

data class AccountCreateResponse(var account: AccountDto) : ResponseBase() {
    companion object {
        fun fromDomain(account: Account) = AccountCreateResponse(AccountDto.fromDomain(account))
    }
}

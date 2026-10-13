package com.example.banking.domain.dto

import com.example.banking.domain.model.Account

data class AccountCloseResponse(var account: AccountDto) : ResponseBase() {
    companion object {
        fun fromDomain(account: Account) = AccountCloseResponse(AccountDto.fromDomain(account))
    }
}

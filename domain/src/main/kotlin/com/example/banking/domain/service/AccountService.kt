package com.example.banking.domain.service

import com.example.banking.domain.enums.AccountType
import com.example.banking.domain.model.Account
import com.example.banking.domain.model.Transaction
import com.example.banking.domain.model.TransferResult
import com.example.banking.domain.model.UserSession

interface AccountService {
    fun create(userSession: UserSession, accountType: AccountType = AccountType.CURRENT): Account
    fun close(userSession: UserSession, accountNumber: String): Account
    fun deposit(userSession: UserSession, transaction: Transaction): Account
    fun withdraw(userSession: UserSession, transaction: Transaction): Account
    fun transfer(userSession: UserSession, transaction: Transaction): TransferResult
}

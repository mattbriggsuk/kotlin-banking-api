package com.example.banking.domain.service

import com.example.banking.domain.model.Account
import com.example.banking.domain.model.Transaction
import com.example.banking.domain.model.TransferResult

interface AccountService {
    fun create(customerNumber: String): Account
    fun close(accountNumber: String): Account
    fun deposit(transaction: Transaction): Account
    fun withdraw(transaction: Transaction): Account
    fun transfer(transaction: Transaction): TransferResult
}

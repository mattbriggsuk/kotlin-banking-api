package com.example.banking.domain.repository

import com.example.banking.domain.model.Account

interface AccountRepository {
    fun getAccount(accountNumber: String): Account?
    fun getCustomerAccounts(customerNumber: String): List<Account>
    fun save(account: Account): Account
    fun transferBalance(fromAccount: Account, toAccount: Account)
}

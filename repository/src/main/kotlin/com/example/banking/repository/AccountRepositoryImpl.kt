package com.example.banking.repository

import com.example.banking.domain.model.Account
import com.example.banking.domain.repository.AccountRepository
import org.springframework.stereotype.Repository

/**
 * In-memory account storage. Accounts are lost when the application stops.
 *
 * Each synchronized method locks this repository instance. This protects the shared map and keeps
 * the two writes in [transferBalance] together so another operation cannot observe half a transfer.
 */
@Repository
class AccountRepositoryImpl : AccountRepository {

    // The account number is the map key, allowing direct lookup without an internal ID.
    private val accounts = mutableMapOf<String, Account>()

    @Synchronized
    override fun getAccount(accountNumber: String): Account? = accounts[accountNumber]

    @Synchronized
    override fun getCustomerAccounts(customerNumber: String): List<Account> =
        accounts.values.filter { it.customerNumber == customerNumber }

    @Synchronized
    override fun save(account: Account): Account =
        account.also { accounts[it.accountNumber] = it }

    @Synchronized
    override fun transferBalance(fromAccount: Account, toAccount: Account) {
        accounts[fromAccount.accountNumber] = fromAccount
        accounts[toAccount.accountNumber] = toAccount
    }
}

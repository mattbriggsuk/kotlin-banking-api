package com.example.banking.domain.dto

import com.example.banking.domain.model.TransferResult

data class AccountTransferResponse(
    var fromAccount: AccountDto,
    var toAccount: AccountDto,
) : ResponseBase() {
    companion object {
        /** Maps both account states returned by a successful transfer. */
        fun fromDomain(result: TransferResult) = AccountTransferResponse(
            AccountDto.fromDomain(result.fromAccount),
            AccountDto.fromDomain(result.toAccount),
        )
    }
}

package com.example.banking.utils

import com.example.banking.domain.enums.TransactionType
import com.example.banking.domain.model.Account
import com.example.banking.domain.model.Transaction
import java.math.BigDecimal

/** Shared validation rules for banking transactions. */
object TransactionUtils {

    /** Validates common transaction fields and the rules for the expected transaction type. */
    fun validateTransaction(transaction: Transaction?, expectedType: TransactionType) {
        requireNotNull(transaction) { "Transaction is required" }
        require(transaction.transactionType == expectedType) {
            "Transaction type must be $expectedType"
        }

        val amount = transaction.amount
        require(
            amount != null &&
                amount.signum() > 0 &&
                amount.scale() <= 2 &&
                amount.precision() - amount.scale() <= 16,
        ) {
            "Amount must be positive with at most two decimal places and 16 integer digits"
        }

        require(ValidationUtils.isValidAccountNumber(transaction.targetAccountNumber)) {
            "Target account number must be valid"
        }

        if (expectedType == TransactionType.TRANSFER) {
            require(ValidationUtils.isValidAccountNumber(transaction.sourceAccountNumber)) {
                "Source account number must be valid for transfers"
            }
            require(transaction.sourceAccountNumber != transaction.targetAccountNumber) {
                "Source and target accounts must differ"
            }
        } else {
            require(transaction.sourceAccountNumber.isNullOrBlank()) {
                "Source account number is only valid for transfers"
            }
        }
    }

    /** Prevents a withdrawal or transfer from taking an account balance below zero. */
    fun requireFunds(account: Account, amount: BigDecimal) {
        check(account.balance >= amount) { "Insufficient funds" }
    }
}

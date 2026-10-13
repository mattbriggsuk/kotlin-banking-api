package com.example.banking.utils

import com.example.banking.domain.enums.TransactionType
import com.example.banking.domain.model.*
import java.math.BigDecimal
import java.security.SecureRandom

object AccountUtils {
    private val random = SecureRandom()
    fun generateAccountNumber(): String = buildString(16) {
        append(random.nextInt(9) + 1)
        repeat(14) { append(random.nextInt(10)) }
        append(calculateLuhnCheckDigit(this))
    }
    private fun calculateLuhnCheckDigit(digits: CharSequence): Int = (10 - digits.mapIndexed { i, c ->
        var digit = c.digitToInt(); if (i % 2 == 0) { digit *= 2; if (digit > 9) digit -= 9 }; digit
    }.sum() % 10) % 10
}

object ValidationUtils {
    fun isValidAccountNumber(value: String?): Boolean {
        if (value == null || !value.matches(Regex("[0-9]{16}"))) return false
        return value.mapIndexed { i, c -> var d = c.digitToInt(); if (i % 2 == 0) { d *= 2; if (d > 9) d -= 9 }; d }.sum() % 10 == 0
    }
    fun isValidCustomerNumber(value: String?) = isValidAccountNumber(value)
    fun isValidCustomerObject(customer: Customer?) = customer != null && customer.firstName.isNotBlank() && customer.lastName.isNotBlank() && customer.email.isNotBlank()
}

object TransactionUtils {
    fun validateTransaction(transaction: Transaction?, expectedType: TransactionType) {
        requireNotNull(transaction) { "Transaction is required" }
        require(transaction.transactionType == expectedType) { "Transaction type must be $expectedType" }
        val amount = transaction.amount
        require(amount != null && amount.signum() > 0 && amount.scale() <= 2 && amount.precision() - amount.scale() <= 16) {
            "Amount must be positive with at most two decimal places and 16 integer digits"
        }
        require(ValidationUtils.isValidAccountNumber(transaction.targetAccountNumber)) { "Target account number must be valid" }
        if (expectedType == TransactionType.TRANSFER) {
            require(ValidationUtils.isValidAccountNumber(transaction.sourceAccountNumber)) { "Source account number must be valid for transfers" }
            require(transaction.sourceAccountNumber != transaction.targetAccountNumber) { "Source and target accounts must differ" }
        } else require(transaction.sourceAccountNumber.isNullOrBlank()) { "Source account number is only valid for transfers" }
    }
    fun requireFunds(account: Account, amount: BigDecimal) { check(account.balance >= amount) { "Insufficient funds" } }
}

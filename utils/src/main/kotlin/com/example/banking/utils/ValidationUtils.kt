package com.example.banking.utils

import com.example.banking.domain.model.Customer

/** Shared validation helpers with no dependency on Spring. */
object ValidationUtils {

    /**
     * Validates the 16-digit format and Luhn checksum shared by customer and account numbers.
     * The checksum detects common typing mistakes; it does not prove that an account exists.
     */
    fun isValidAccountNumber(value: String?): Boolean {
        if (value == null || !value.matches(Regex("[0-9]{16}"))) return false

        val sum = value.mapIndexed { index, character ->
            var digit = character.digitToInt()
            // Subtracting nine reduces a doubled value above nine to its digit sum.
            if (index % 2 == 0) {
                digit *= 2
                if (digit > 9) digit -= 9
            }
            digit
        }.sum()

        // A valid number has a total, including its check digit, divisible by ten.
        return sum % 10 == 0
    }

    /** Customer numbers currently use the same format as account numbers. */
    fun isValidCustomerNumber(value: String?): Boolean = isValidAccountNumber(value)

    /** Ensures the customer and every required registration field contain a value. */
    fun isValidCustomerObject(customer: Customer?): Boolean =
        customer != null &&
            customer.firstName.isNotBlank() &&
            customer.lastName.isNotBlank() &&
            customer.email.isNotBlank()
}

package com.example.banking.utils

import java.security.SecureRandom

/** Shared helpers for customer and account number generation. */
object AccountUtils {
    private val random = SecureRandom()

    /** Generates 15 random digits followed by a Luhn check digit. */
    fun generateAccountNumber(): String = buildString(16) {
        // Prevent a leading zero so the generated value is visibly 16 digits.
        append(random.nextInt(9) + 1)
        repeat(14) { append(random.nextInt(10)) }
        append(calculateLuhnCheckDigit(this))
    }

    private fun calculateLuhnCheckDigit(digits: CharSequence): Int =
        (10 - digits.mapIndexed { index, character ->
            var digit = character.digitToInt()
            // For a 16-digit number, digits at even zero-based positions are doubled.
            if (index % 2 == 0) {
                digit *= 2
                if (digit > 9) digit -= 9
            }
            digit
        }.sum() % 10) % 10
}

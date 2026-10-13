package com.example.banking.utils

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object PinUtils {
    private val random = SecureRandom()
    private const val iterations = 600_000
    fun generate(): String = random.nextInt(1_000_000).toString().padStart(6, '0')

    fun hash(pin: String): String {
        val salt = ByteArray(16).also(random::nextBytes)
        val encoder = Base64.getEncoder()
        return encoder.encodeToString(salt) + ":" + encoder.encodeToString(derive(pin, salt))
    }

    fun matches(pin: String, encoded: String): Boolean {
        val parts = encoded.split(':', limit = 3)
        if (parts.size != 2) return false
        return try {
            val decoder = Base64.getDecoder()
            MessageDigest.isEqual(decoder.decode(parts[1]), derive(pin, decoder.decode(parts[0])))
        } catch (_: IllegalArgumentException) {
            false
        }
    }

    private fun derive(pin: String, salt: ByteArray): ByteArray {
        val spec = PBEKeySpec(pin.toCharArray(), salt, iterations, 256)
        return try { SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded }
        finally { spec.clearPassword() }
    }
}

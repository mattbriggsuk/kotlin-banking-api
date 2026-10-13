package com.example.banking.business

import com.example.banking.domain.exceptions.AuthenticationException
import com.example.banking.domain.model.Customer
import com.example.banking.domain.model.UserSession
import com.example.banking.domain.repository.CustomerRepository
import com.example.banking.domain.service.AccessService
import com.example.banking.utils.PinUtils
import org.springframework.stereotype.Service
import java.security.SecureRandom
import java.time.Instant
import java.util.Base64
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

/** Authenticates customers and manages opaque, server-side sessions. */
@Service
class AccessServiceImpl(
    private val customerRepository: CustomerRepository,
) : AccessService {
    private data class Session(val customerNumber: String, val expiresAt: Instant)

    private val sessions = ConcurrentHashMap<String, Session>()
    private val random = SecureRandom()
    private val dummyHash = PinUtils.hash(PinUtils.generate())

    override fun login(identifier: String, pin: String): UserSession {
        val normalised = identifier.trim().lowercase(Locale.ROOT)
        val customer = if (normalised.contains('@')) {
            customerRepository.getCustomerByEmail(normalised)
        } else {
            customerRepository.getCustomer(normalised)
        }

        val hash = customer?.customerNumber?.let(customerRepository::getPinHash)
        val matches = PinUtils.matches(pin, hash ?: dummyHash)
        if (customer == null || hash == null || !matches) {
            throw AuthenticationException("Customer details or PIN are incorrect.")
        }

        return UserSession(customer, startSession(requireNotNull(customer.customerNumber)))
    }

    override fun startSession(customerNumber: String): String {
        val now = Instant.now()
        sessions.entries.removeIf { it.value.expiresAt.isBefore(now) }

        val bytes = ByteArray(32).also(random::nextBytes)
        val token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
        sessions[token] = Session(customerNumber, now.plusSeconds(1800))
        return token
    }

    override fun authenticatedSession(authorization: String?): UserSession {
        val token = token(authorization)
        val session = sessions[token]
        if (session == null || !session.expiresAt.isAfter(Instant.now())) {
            sessions.remove(token)
            throw AuthenticationException("Your session has expired. Log in again.")
        }

        val customer: Customer = customerRepository.getCustomer(session.customerNumber)
            ?: throw AuthenticationException("Your session has expired. Log in again.")
        return UserSession(customer, token)
    }

    override fun logout(userSession: UserSession) {
        sessions.remove(userSession.sessionToken)
    }

    private fun token(authorization: String?): String {
        if (authorization == null || !authorization.startsWith("Bearer ") || authorization.length <= 7) {
            throw AuthenticationException("Log in to continue.")
        }
        return authorization.substring(7)
    }
}

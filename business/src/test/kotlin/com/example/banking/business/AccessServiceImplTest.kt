package com.example.banking.business

import com.example.banking.domain.exceptions.AuthenticationException
import com.example.banking.domain.model.Customer
import com.example.banking.domain.repository.CustomerRepository
import com.example.banking.utils.PinUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

class AccessServiceImplTest {
    private val customerNumber = "1234567890123452"
    private val pin = "012345"
    private val customer = Customer(customerNumber, "Alex", "Morgan", "alex@example.com")
    private lateinit var customerRepository: CustomerRepository
    private lateinit var service: AccessServiceImpl

    @BeforeEach
    fun setUp() {
        customerRepository = mock {
            on { getCustomer(customerNumber) } doReturn customer
            on { getCustomerByEmail(customer.email) } doReturn customer
            on { getPinHash(customerNumber) } doReturn PinUtils.hash(pin)
        }
        service = AccessServiceImpl(customerRepository)
    }

    @Test
    fun `login accepts a customer number and preserves leading zeros in the PIN`() {
        val session = service.login(customerNumber, pin)
        assertSame(customer, session.customer)
        assertSame(
            customer,
            service.authenticatedSession("Bearer " + session.sessionToken).customer,
        )
    }

    @Test
    fun `email login trims and normalises case`() {
        assertSame(customer, service.login(" ALEX@EXAMPLE.COM ", pin).customer)
    }

    @Test
    fun `unknown customer and wrong PIN have the same error`() {
        val wrong = assertThrows(AuthenticationException::class.java) {
            service.login(customerNumber, "999999")
        }
        val missing = assertThrows(AuthenticationException::class.java) {
            service.login("missing@example.com", pin)
        }
        assertEquals(wrong.message, missing.message)
    }

    @Test
    fun `logout revokes the session`() {
        val session = service.login(customerNumber, pin)
        val authorization = "Bearer " + session.sessionToken
        service.logout(session)

        assertThrows(AuthenticationException::class.java) {
            service.authenticatedSession(authorization)
        }
    }

    @Test
    fun `missing and invalid bearer tokens are rejected`() {
        assertThrows(AuthenticationException::class.java) {
            service.authenticatedSession(null)
        }
        assertThrows(AuthenticationException::class.java) {
            service.authenticatedSession("not-a-bearer-token")
        }
    }
}

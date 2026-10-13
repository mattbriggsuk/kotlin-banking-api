package com.example.banking.api

import com.fasterxml.jackson.databind.JsonNode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import java.util.UUID

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AccessSecurityIntegrationTest {
    @Autowired
    private lateinit var http: TestRestTemplate

    @Test
    fun `require access authenticates and authorizes controller arguments`() {
        val alice = register("Alice")
        val bob = register("Bob")

        val aliceNumber = alice.at("/customer/customerNumber").asText()
        val bobNumber = bob.at("/customer/customerNumber").asText()
        val aliceToken = alice.get("sessionToken").asText()
        val bobToken = bob.get("sessionToken").asText()

        assertEquals(HttpStatus.UNAUTHORIZED, getCustomer(aliceNumber, null).statusCode)
        assertEquals(HttpStatus.OK, getCustomer(aliceNumber, aliceToken).statusCode)
        assertEquals(HttpStatus.FORBIDDEN, getCustomer(bobNumber, aliceToken).statusCode)

        val accountResponse = post(
            "/api/account/create",
            aliceToken,
            mapOf("accountType" to "CURRENT"),
        )
        assertEquals(HttpStatus.CREATED, accountResponse.statusCode)

        val accountNumber = requireNotNull(accountResponse.body)
            .at("/account/accountNumber")
            .asText()
        val deposit = mapOf(
            "transaction" to mapOf(
                "transactionType" to "DEPOSIT",
                "amount" to 10,
                "targetAccountNumber" to accountNumber,
            ),
        )

        assertEquals(HttpStatus.OK, post("/api/account/deposit", aliceToken, deposit).statusCode)
        assertEquals(HttpStatus.FORBIDDEN, post("/api/account/deposit", bobToken, deposit).statusCode)

        assertEquals(HttpStatus.NO_CONTENT, post("/api/access/logout", aliceToken, null).statusCode)
        assertEquals(HttpStatus.UNAUTHORIZED, getCustomer(aliceNumber, aliceToken).statusCode)
    }

    private fun register(firstName: String): JsonNode {
        val body = mapOf(
            "customer" to mapOf(
                "firstName" to firstName,
                "lastName" to "Security",
                "email" to "${firstName.lowercase()}.${UUID.randomUUID()}@example.com",
            ),
        )
        val response = post("/api/customer/register", null, body)
        assertEquals(HttpStatus.CREATED, response.statusCode)
        return requireNotNull(response.body)
    }

    private fun getCustomer(customerNumber: String, token: String?): ResponseEntity<JsonNode> =
        http.exchange(
            "/api/customer/get?customerNumber=$customerNumber",
            HttpMethod.GET,
            HttpEntity<Any>(headers(token)),
            JsonNode::class.java,
        )

    private fun post(path: String, token: String?, body: Any?): ResponseEntity<JsonNode> =
        http.exchange(
            path,
            HttpMethod.POST,
            HttpEntity(body, headers(token)),
            JsonNode::class.java,
        )

    private fun headers(token: String?): HttpHeaders =
        HttpHeaders().also {
            it.contentType = MediaType.APPLICATION_JSON
            if (token != null) {
                it.setBearerAuth(token)
            }
        }
}

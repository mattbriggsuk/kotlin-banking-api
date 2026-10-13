package com.example.banking.api.controllers

import com.example.banking.api.security.RequireAccess
import com.example.banking.domain.dto.CustomerAccountResponse
import com.example.banking.domain.dto.CustomerRegisterRequest
import com.example.banking.domain.dto.CustomerRegisterResponse
import com.example.banking.domain.dto.CustomerResponse
import com.example.banking.domain.model.UserSession
import com.example.banking.domain.service.AccessService
import com.example.banking.domain.service.CustomerService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

/**
 * Provides customer registration, profile, and account-list endpoints.
 *
 * Registration is public and starts the customer's first session. Profile and account
 * requests require the [UserSession] created by the bearer authentication filter.
 */
@RestController
@RequestMapping("/api/customer")
class CustomerController(
    // Handles customer registration, lookup, and customer-level ownership checks.
    private val customerService: CustomerService,
    // Starts the initial session returned after successful registration.
    private val accessService: AccessService,
) {
    /**
     * Registers a customer and immediately creates their first authenticated session.
     *
     * The response returns the generated PIN once, together with the new session token.
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@Valid @RequestBody request: CustomerRegisterRequest): CustomerRegisterResponse {
        // Bean validation checks the request shape; the service applies registration rules.
        val registration = customerService.register(requireNotNull(request.customer))

        // Start a session only after the customer has been registered successfully.
        val sessionToken = accessService.startSession(requireNotNull(registration.customer.customerNumber))
        return CustomerRegisterResponse.fromDomain(registration, sessionToken)
    }

    /** Returns the authenticated customer's profile after the service verifies ownership. */
    @RequireAccess
    @GetMapping("/get")
    fun get(
        @RequestParam customerNumber: String,
        @AuthenticationPrincipal userSession: UserSession,
    ): CustomerResponse =
        CustomerResponse.fromDomain(customerService.getCustomer(userSession, customerNumber))

    /** Returns every account belonging to the authenticated customer. */
    @RequireAccess
    @GetMapping("/accounts")
    fun accounts(
        @RequestParam customerNumber: String,
        @AuthenticationPrincipal userSession: UserSession,
    ): CustomerAccountResponse =
        CustomerAccountResponse.fromDomain(
            customerNumber,
            customerService.getCustomerAccounts(userSession, customerNumber),
        )
}

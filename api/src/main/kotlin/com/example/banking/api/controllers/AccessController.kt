package com.example.banking.api.controllers

import com.example.banking.api.security.RequireAccess
import com.example.banking.domain.dto.CustomerDto
import com.example.banking.domain.dto.CustomerLoginRequest
import com.example.banking.domain.dto.CustomerLoginResponse
import com.example.banking.domain.model.UserSession
import com.example.banking.domain.service.AccessService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

/**
 * Provides login and logout endpoints for customer sessions.
 *
 * Login is public because it creates the identity used by protected endpoints. Logout
 * requires that identity so the service can revoke the exact token used for the request.
 */
@RestController
@RequestMapping("/api/access")
class AccessController(
    // Handles credential validation and server-side session management.
    private val accessService: AccessService,
) {
    /** Authenticates customer credentials and returns a new opaque session token. */
    @PostMapping("/login")
    fun login(@Valid @RequestBody request: CustomerLoginRequest): CustomerLoginResponse {
        // The service accepts either an email address or customer number as the identifier.
        val userSession = accessService.login(request.identifier, request.pin)

        // Return public customer data and the token; credential hashes never leave the repository.
        return CustomerLoginResponse(
            CustomerDto.fromDomain(userSession.customer),
            userSession.sessionToken,
        )
    }

    /** Revokes the session represented by the authenticated principal. */
    @RequireAccess
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun logout(@AuthenticationPrincipal userSession: UserSession) {
        accessService.logout(userSession)
    }
}

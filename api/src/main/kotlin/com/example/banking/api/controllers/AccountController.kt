package com.example.banking.api.controllers

import com.example.banking.api.security.RequireAccess
import com.example.banking.domain.dto.AccountAmountRequest
import com.example.banking.domain.dto.AccountAmountResponse
import com.example.banking.domain.dto.AccountCloseRequest
import com.example.banking.domain.dto.AccountCloseResponse
import com.example.banking.domain.dto.AccountCreateRequest
import com.example.banking.domain.dto.AccountCreateResponse
import com.example.banking.domain.dto.AccountTransferRequest
import com.example.banking.domain.dto.AccountTransferResponse
import com.example.banking.domain.model.UserSession
import com.example.banking.domain.service.AccountService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

/**
 * Exposes account lifecycle and balance operations under `/api/account`.
 *
 * Every endpoint requires a valid session. The controller passes the trusted [UserSession]
 * to [AccountService], where account ownership and banking rules are enforced.
 */
@RestController
@RequestMapping("/api/account")
class AccountController(
    // Applies account rules and verifies ownership using the authenticated session.
    private val accountService: AccountService,
) {
    /**
     * Opens an account for the authenticated customer.
     *
     * The customer number comes from [userSession], so clients cannot choose another owner.
     */
    @RequireAccess
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @Valid @RequestBody request: AccountCreateRequest,
        @AuthenticationPrincipal userSession: UserSession,
    ): AccountCreateResponse =
        AccountCreateResponse.fromDomain(accountService.create(userSession, request.accountType))

    /** Closes an owned account after the service confirms that its balance is zero. */
    @RequireAccess
    @PostMapping("/close")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun close(
        @Valid @RequestBody request: AccountCloseRequest,
        @AuthenticationPrincipal userSession: UserSession,
    ): AccountCloseResponse =
        AccountCloseResponse.fromDomain(accountService.close(userSession, request.accountNumber))

    /** Deposits a positive amount into an open account owned by the authenticated customer. */
    @RequireAccess
    @PostMapping("/deposit")
    fun deposit(
        @Valid @RequestBody request: AccountAmountRequest,
        @AuthenticationPrincipal userSession: UserSession,
    ): AccountAmountResponse =
        AccountAmountResponse.fromDomain(
            accountService.deposit(userSession, requireNotNull(request.transaction)),
        )

    /** Withdraws funds from an owned account when sufficient funds are available. */
    @RequireAccess
    @PostMapping("/withdraw")
    fun withdraw(
        @Valid @RequestBody request: AccountAmountRequest,
        @AuthenticationPrincipal userSession: UserSession,
    ): AccountAmountResponse =
        AccountAmountResponse.fromDomain(
            accountService.withdraw(userSession, requireNotNull(request.transaction)),
        )

    /**
     * Transfers funds from an account owned by the authenticated customer.
     *
     * The destination may belong to another customer; only the source account requires ownership.
     */
    @RequireAccess
    @PostMapping("/transfer")
    fun transfer(
        @Valid @RequestBody request: AccountTransferRequest,
        @AuthenticationPrincipal userSession: UserSession,
    ): AccountTransferResponse =
        AccountTransferResponse.fromDomain(
            accountService.transfer(userSession, requireNotNull(request.transaction)),
        )
}

package com.example.banking.api.controllers

import com.example.banking.domain.dto.AccountAmountRequest
import com.example.banking.domain.dto.AccountAmountResponse
import com.example.banking.domain.dto.AccountCloseRequest
import com.example.banking.domain.dto.AccountCloseResponse
import com.example.banking.domain.dto.AccountCreateRequest
import com.example.banking.domain.dto.AccountCreateResponse
import com.example.banking.domain.dto.AccountTransferRequest
import com.example.banking.domain.dto.AccountTransferResponse
import com.example.banking.domain.service.AccountService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

/**
 * Exposes account lifecycle and balance operations under `/api/account`.
 *
 * The controller validates incoming request DTOs, delegates banking rules to [AccountService],
 * and maps domain results into public response DTOs. The shared API exception handler converts
 * service validation, lookup, and state failures into appropriate HTTP responses.
 */
@RestController
@RequestMapping("/api/account")
class AccountController(private val accountService: AccountService) {

    /** Opens an account for an existing customer with a zero balance. */
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody request: AccountCreateRequest) =
        AccountCreateResponse.fromDomain(accountService.create(request.customerNumber))

    /** Closes an open account once its balance is zero. */
    @PostMapping("/close")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun close(@Valid @RequestBody request: AccountCloseRequest) =
        AccountCloseResponse.fromDomain(accountService.close(request.accountNumber))

    /** Adds a positive amount to an open account and returns its updated state. */
    @PostMapping("/deposit")
    fun deposit(@Valid @RequestBody request: AccountAmountRequest) =
        AccountAmountResponse.fromDomain(accountService.deposit(requireNotNull(request.transaction)))

    /** Removes a positive amount from an open account when sufficient funds are available. */
    @PostMapping("/withdraw")
    fun withdraw(@Valid @RequestBody request: AccountAmountRequest) =
        AccountAmountResponse.fromDomain(accountService.withdraw(requireNotNull(request.transaction)))

    /** Moves funds between two open accounts and returns both updated account states. */
    @PostMapping("/transfer")
    fun transfer(@Valid @RequestBody request: AccountTransferRequest) =
        AccountTransferResponse.fromDomain(accountService.transfer(requireNotNull(request.transaction)))
}

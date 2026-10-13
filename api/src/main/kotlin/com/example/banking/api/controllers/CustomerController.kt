package com.example.banking.api.controllers

import com.example.banking.domain.dto.CustomerAccountResponse
import com.example.banking.domain.dto.CustomerRegisterRequest
import com.example.banking.domain.dto.CustomerRegisterResponse
import com.example.banking.domain.dto.CustomerResponse
import com.example.banking.domain.service.CustomerService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

/**
 * Exposes customer registration and lookup operations under `/api/customer`.
 *
 * The controller validates incoming request DTOs, delegates customer rules to [CustomerService],
 * and maps domain results into public response DTOs. The shared API exception handler converts
 * service validation and lookup failures into appropriate HTTP responses.
 */
@RestController
@RequestMapping("/api/customer")
class CustomerController(private val customerService: CustomerService) {

    /** Registers a customer and returns the generated customer number with their details. */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@Valid @RequestBody request: CustomerRegisterRequest) =
        CustomerRegisterResponse.fromDomain(customerService.register(requireNotNull(request.customer)))

    /** Returns the customer identified by the supplied 16-digit customer number. */
    @GetMapping("/get")
    fun get(@RequestParam customerNumber: String) =
        CustomerResponse.fromDomain(customerService.getCustomer(customerNumber))

    /** Returns all accounts owned by an existing customer. */
    @GetMapping("/accounts")
    fun accounts(@RequestParam customerNumber: String) =
        CustomerAccountResponse.fromDomain(
            customerNumber,
            customerService.getCustomerAccounts(customerNumber),
        )
}

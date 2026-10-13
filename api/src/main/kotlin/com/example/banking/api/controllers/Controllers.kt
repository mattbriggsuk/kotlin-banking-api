package com.example.banking.api.controllers

import com.example.banking.domain.dto.*
import com.example.banking.domain.service.*
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController @RequestMapping("/api/account")
class AccountController(private val accountService: AccountService) {
    @PostMapping("/create") @ResponseStatus(HttpStatus.CREATED)
    fun create(@Valid @RequestBody request: AccountCreateRequest) = AccountCreateResponse.fromDomain(accountService.create(request.customerNumber))
    @PostMapping("/close") @ResponseStatus(HttpStatus.ACCEPTED)
    fun close(@Valid @RequestBody request: AccountCloseRequest) = AccountCloseResponse.fromDomain(accountService.close(request.accountNumber))
    @PostMapping("/deposit")
    fun deposit(@Valid @RequestBody request: AccountAmountRequest) = AccountAmountResponse.fromDomain(accountService.deposit(requireNotNull(request.transaction)))
    @PostMapping("/withdraw")
    fun withdraw(@Valid @RequestBody request: AccountAmountRequest) = AccountAmountResponse.fromDomain(accountService.withdraw(requireNotNull(request.transaction)))
    @PostMapping("/transfer")
    fun transfer(@Valid @RequestBody request: AccountTransferRequest) = AccountTransferResponse.fromDomain(accountService.transfer(requireNotNull(request.transaction)))
}

@RestController @RequestMapping("/api/customer")
class CustomerController(private val customerService: CustomerService) {
    @PostMapping("/register") @ResponseStatus(HttpStatus.CREATED)
    fun register(@Valid @RequestBody request: CustomerRegisterRequest) = CustomerRegisterResponse.fromDomain(customerService.register(requireNotNull(request.customer)))
    @GetMapping("/get") fun get(@RequestParam customerNumber: String) = CustomerResponse.fromDomain(customerService.getCustomer(customerNumber))
    @GetMapping("/accounts") fun accounts(@RequestParam customerNumber: String) = CustomerAccountResponse.fromDomain(customerNumber, customerService.getCustomerAccounts(customerNumber))
}

package com.example.banking.domain.dto
import com.example.banking.domain.model.*
import jakarta.validation.Valid
import jakarta.validation.constraints.*
import java.math.BigDecimal

abstract class RequestBase
abstract class ResponseBase
data class AccountDto(val accountNumber: String, val customerNumber: String, val balance: BigDecimal, val closed: Boolean) { companion object { fun fromDomain(a: Account) = AccountDto(a.accountNumber, a.customerNumber, a.balance, a.closed) } }
data class CustomerDto(val customerNumber: String?, val firstName: String, val lastName: String, val email: String) { companion object { fun fromDomain(c: Customer) = CustomerDto(c.customerNumber, c.firstName, c.lastName, c.email) } }
data class AccountCreateRequest(@field:NotBlank @field:Pattern(regexp = "[0-9]{16}", message = "must contain exactly 16 digits") var customerNumber: String = "") : RequestBase()
data class AccountCloseRequest(@field:NotBlank @field:Pattern(regexp = "[0-9]{16}", message = "must contain exactly 16 digits") var accountNumber: String = "") : RequestBase()
data class AccountAmountRequest(@field:NotNull @field:Valid var transaction: Transaction? = null) : RequestBase()
data class AccountTransferRequest(@field:NotNull @field:Valid var transaction: Transaction? = null) : RequestBase()
data class CustomerRegisterRequest(@field:NotNull @field:Valid var customer: Customer? = null) : RequestBase()
data class AccountCreateResponse(var account: AccountDto) : ResponseBase() { companion object { fun fromDomain(a: Account) = AccountCreateResponse(AccountDto.fromDomain(a)) } }
data class AccountCloseResponse(var account: AccountDto) : ResponseBase() { companion object { fun fromDomain(a: Account) = AccountCloseResponse(AccountDto.fromDomain(a)) } }
data class AccountAmountResponse(var account: AccountDto) : ResponseBase() { companion object { fun fromDomain(a: Account) = AccountAmountResponse(AccountDto.fromDomain(a)) } }
data class AccountTransferResponse(var fromAccount: AccountDto, var toAccount: AccountDto) : ResponseBase() { companion object { fun fromDomain(r: TransferResult) = AccountTransferResponse(AccountDto.fromDomain(r.fromAccount), AccountDto.fromDomain(r.toAccount)) } }
data class CustomerRegisterResponse(var customer: CustomerDto) : ResponseBase() { companion object { fun fromDomain(c: Customer) = CustomerRegisterResponse(CustomerDto.fromDomain(c)) } }
data class CustomerResponse(var customer: CustomerDto) : ResponseBase() { companion object { fun fromDomain(c: Customer) = CustomerResponse(CustomerDto.fromDomain(c)) } }
data class CustomerAccountResponse(var customerNumber: String, var accounts: List<AccountDto>) : ResponseBase() { companion object { fun fromDomain(n: String, a: List<Account>) = CustomerAccountResponse(n, a.map(AccountDto::fromDomain)) } }

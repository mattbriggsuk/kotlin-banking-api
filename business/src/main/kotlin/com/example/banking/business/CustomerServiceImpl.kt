package com.example.banking.business

import com.example.banking.domain.exceptions.AccessDeniedException
import com.example.banking.domain.model.Account
import com.example.banking.domain.model.Customer
import com.example.banking.domain.model.CustomerRegistration
import com.example.banking.domain.model.UserSession
import com.example.banking.domain.repository.AccountRepository
import com.example.banking.domain.repository.CustomerRepository
import com.example.banking.domain.service.CustomerService
import com.example.banking.utils.AccountUtils
import com.example.banking.utils.PinUtils
import com.example.banking.utils.ValidationUtils
import org.springframework.stereotype.Service
import java.util.Locale

/** Implements customer registration and lookup operations. */
@Service
class CustomerServiceImpl(
    private val customerRepository: CustomerRepository,
    private val accountRepository: AccountRepository,
) : CustomerService {

    override fun getCustomer(userSession: UserSession, customerNumber: String): Customer {
        validateNumber(customerNumber)
        requireCustomer(userSession, customerNumber)
        return customerRepository.getCustomer(customerNumber)
            ?: throw NoSuchElementException("Customer not found")
    }

    override fun getCustomerAccounts(userSession: UserSession, customerNumber: String): List<Account> {
        getCustomer(userSession, customerNumber)
        return accountRepository.getCustomerAccounts(customerNumber)
    }

    @Synchronized
    override fun register(customer: Customer): CustomerRegistration {
        require(ValidationUtils.isValidCustomerObject(customer)) {
            "Customer and all required details must be provided"
        }

        check(customerRepository.getCustomerByEmail(customer.email) == null) {
            "A customer with this email already exists. Log in instead."
        }

        val pin = PinUtils.generate()
        val registeredCustomer = Customer(
            AccountUtils.generateAccountNumber(),
            customer.firstName.trim(),
            customer.lastName.trim(),
            customer.email.trim().lowercase(Locale.ROOT),
        )
        val saved = customerRepository.save(registeredCustomer, PinUtils.hash(pin))
        return CustomerRegistration(saved, pin)
    }

    private fun validateNumber(customerNumber: String) {
        require(ValidationUtils.isValidCustomerNumber(customerNumber)) {
            "Customer number must be a valid 16-digit number"
        }
    }

    private fun requireCustomer(userSession: UserSession?, customerNumber: String) {
        if (userSession?.customerNumber != customerNumber) {
            throw AccessDeniedException()
        }
    }
}

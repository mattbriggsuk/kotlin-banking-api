package com.example.banking.business

import com.example.banking.domain.model.Account
import com.example.banking.domain.model.Customer
import com.example.banking.domain.repository.AccountRepository
import com.example.banking.domain.repository.CustomerRepository
import com.example.banking.domain.service.CustomerService
import com.example.banking.utils.AccountUtils
import com.example.banking.utils.ValidationUtils
import org.springframework.stereotype.Service

/** Implements customer registration and lookup operations. */
@Service
class CustomerServiceImpl(
    private val customerRepository: CustomerRepository,
    private val accountRepository: AccountRepository,
) : CustomerService {

    /** Returns an existing customer after validating the supplied customer number. */
    override fun getCustomer(customerNumber: String): Customer {
        validateNumber(customerNumber)
        return customerRepository.getCustomer(customerNumber)
            ?: throw NoSuchElementException("Customer not found")
    }

    /** Confirms that the customer exists before returning their accounts. */
    override fun getCustomerAccounts(customerNumber: String): List<Account> {
        getCustomer(customerNumber)
        return accountRepository.getCustomerAccounts(customerNumber)
    }

    /** Registers a customer and generates their Luhn-valid customer number. */
    @Synchronized
    override fun register(customer: Customer): Customer {
        require(ValidationUtils.isValidCustomerObject(customer)) {
            "Customer and all required details must be provided"
        }

        val registeredCustomer = Customer(
            AccountUtils.generateAccountNumber(),
            customer.firstName,
            customer.lastName,
            customer.email,
        )
        return customerRepository.save(registeredCustomer)
    }

    private fun validateNumber(customerNumber: String) =
        require(ValidationUtils.isValidCustomerNumber(customerNumber)) {
            "Customer number must be a valid 16-digit number"
        }
}

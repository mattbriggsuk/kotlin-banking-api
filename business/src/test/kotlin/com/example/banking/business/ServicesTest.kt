package com.example.banking.business

import com.example.banking.domain.enums.TransactionType
import com.example.banking.domain.model.*
import com.example.banking.domain.repository.*
import com.example.banking.utils.ValidationUtils
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import java.math.BigDecimal

class ServicesTest {
    private val customerNumber = "1234567890123452"
    private val sourceNumber = "9876543210987658"
    private val targetNumber = "1111111111111117"

    @Test fun `register generates a valid customer number and saves details`() {
        val customers = mock<CustomerRepository> { on { save(any()) } doAnswer { it.arguments[0] as Customer } }
        val service = CustomerServiceImpl(customers, mock())
        val result = service.register(Customer(firstName = "Taylor", lastName = "Reed", email = "taylor@example.com"))
        assertTrue(ValidationUtils.isValidCustomerNumber(result.customerNumber)); assertEquals("Taylor", result.firstName); verify(customers).save(result)
    }
    @Test fun `get customer rejects invalid number before lookup`() {
        val customers = mock<CustomerRepository>(); val service = CustomerServiceImpl(customers, mock())
        assertThrows(IllegalArgumentException::class.java) { service.getCustomer("invalid") }; verifyNoInteractions(customers)
    }
    @Test fun `create opens and saves zero balance account`() {
        val accounts = mock<AccountRepository> { on { save(any()) } doAnswer { it.arguments[0] as Account } }
        val customers = mock<CustomerRepository> { on { getCustomer(customerNumber) } doReturn Customer(customerNumber, "T", "R", "t@e.com") }
        val result = AccountServiceImpl(accounts, customers).create(customerNumber)
        assertEquals(BigDecimal("0.00"), result.balance); assertFalse(result.closed); assertTrue(ValidationUtils.isValidAccountNumber(result.accountNumber)); verify(accounts).save(result)
    }
    @Test fun `deposit adds balance and timestamps transaction`() {
        val account = Account(targetNumber, customerNumber, BigDecimal("10.00"), false)
        val accounts = mock<AccountRepository> { on { getAccount(targetNumber) } doReturn account; on { save(account) } doReturn account }
        val tx = Transaction(TransactionType.DEPOSIT, BigDecimal("5.25"), targetAccountNumber = targetNumber)
        AccountServiceImpl(accounts, mock()).deposit(tx)
        assertEquals(BigDecimal("15.25"), account.balance); assertNotNull(tx.transactionDate)
    }
    @Test fun `withdraw rejects insufficient funds without saving`() {
        val account = Account(targetNumber, customerNumber, BigDecimal("3.00"), false)
        val accounts = mock<AccountRepository> { on { getAccount(targetNumber) } doReturn account }
        val tx = Transaction(TransactionType.WITHDRAWAL, BigDecimal("4.00"), targetAccountNumber = targetNumber)
        assertThrows(IllegalStateException::class.java) { AccountServiceImpl(accounts, mock()).withdraw(tx) }
        assertEquals(BigDecimal("3.00"), account.balance); verify(accounts, never()).save(any())
    }
    @Test fun `transfer moves funds and persists both accounts`() {
        val source = Account(sourceNumber, customerNumber, BigDecimal("20.00"), false); val target = Account(targetNumber, customerNumber, BigDecimal("7.50"), false)
        val accounts = mock<AccountRepository> { on { getAccount(sourceNumber) } doReturn source; on { getAccount(targetNumber) } doReturn target }
        val tx = Transaction(TransactionType.TRANSFER, BigDecimal("4.50"), sourceNumber, targetNumber)
        val result = AccountServiceImpl(accounts, mock()).transfer(tx)
        assertSame(source, result.fromAccount); assertEquals(BigDecimal("15.50"), source.balance); assertEquals(BigDecimal("12.00"), target.balance); verify(accounts).transferBalance(source, target)
    }
}

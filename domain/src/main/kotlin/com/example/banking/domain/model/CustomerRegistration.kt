package com.example.banking.domain.model

/** The clear PIN is returned once, at registration, and is never persisted. */
class CustomerRegistration(val customer: Customer, val pin: String)

package com.example.banking.domain.exceptions

class AuthenticationException(message: String) : RuntimeException(message)
class AccessDeniedException : RuntimeException("You cannot access another customer's accounts")

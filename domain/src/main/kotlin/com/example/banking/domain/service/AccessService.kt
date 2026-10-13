package com.example.banking.domain.service

import com.example.banking.domain.model.UserSession

interface AccessService {
    fun login(identifier: String, pin: String): UserSession
    fun logout(userSession: UserSession)
    fun startSession(customerNumber: String): String
    fun authenticatedSession(authorization: String?): UserSession
}

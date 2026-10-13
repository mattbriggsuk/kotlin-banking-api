package com.example.banking.api.security

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ProblemDetail
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component

/**
 * Writes security-chain failures using the API's [ProblemDetail] response format.
 *
 * Controller advice cannot handle failures raised before a controller is invoked,
 * so Spring Security delegates authentication and access-denied responses here.
 */
@Component
class SecurityProblemHandler(
    // Reuses the application's configured Jackson settings for ProblemDetail JSON.
    private val objectMapper: ObjectMapper,
) : AuthenticationEntryPoint, AccessDeniedHandler {

    /** Writes HTTP 401 when a request cannot establish an authenticated session. */
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException,
    ) {
        // Token validation supplies a safe public message through BadCredentialsException.
        // Other authentication failures receive the standard login instruction.
        val detail = if (exception is BadCredentialsException) {
            exception.message ?: "Log in to continue."
        } else {
            "Log in to continue."
        }
        write(response, HttpStatus.UNAUTHORIZED, detail)
    }

    /** Writes HTTP 403 when an authenticated identity lacks the required access. */
    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AccessDeniedException,
    ) {
        write(
            response,
            HttpStatus.FORBIDDEN,
            "You cannot access another customer's accounts",
        )
    }

    /** Serializes one consistent application/problem+json security response. */
    private fun write(response: HttpServletResponse, status: HttpStatus, detail: String) {
        // Set status and media type before Jackson commits the response body.
        response.status = status.value()
        response.contentType = MediaType.APPLICATION_PROBLEM_JSON_VALUE
        objectMapper.writeValue(
            response.outputStream,
            ProblemDetail.forStatusAndDetail(status, detail),
        )
    }
}

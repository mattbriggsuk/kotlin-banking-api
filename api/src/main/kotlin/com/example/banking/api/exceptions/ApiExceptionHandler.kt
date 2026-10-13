package com.example.banking.api.exceptions

import com.example.banking.domain.exceptions.AuthenticationException
import com.example.banking.domain.exceptions.AccessDeniedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * Converts exceptions from REST controllers into HTTP error responses.
 * Spring matches the exception type to an [ExceptionHandler] method and serializes its
 * [ProblemDetail] result as JSON.
 */
@RestControllerAdvice
class ApiExceptionHandler {
    /** Handle validation without logging rejected credential values. */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun invalidFields(): ProblemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Please check required fields and their formats.")

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun invalidJson(): ProblemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Request fields are missing or have invalid values.")

    @ExceptionHandler(AuthenticationException::class)
    fun unauthorised(exception: AuthenticationException): ProblemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, exception.message ?: "Log in to continue.")

    @ExceptionHandler(AccessDeniedException::class)
    fun forbidden(exception: AccessDeniedException): ProblemDetail =
        ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, exception.message ?: "Access denied")


    /** Converts a missing customer or account into HTTP 404 Not Found. */
    @ExceptionHandler(NoSuchElementException::class)
    fun notFound(exception: NoSuchElementException): ProblemDetail =
        ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND,
            exception.message ?: "Not found",
        )

    /** Converts a business-state conflict into HTTP 409 Conflict. */
    @ExceptionHandler(IllegalStateException::class)
    fun conflict(exception: IllegalStateException): ProblemDetail =
        ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT,
            exception.message ?: "Conflict",
        )

    /** Converts an invalid business argument into HTTP 400 Bad Request. */
    @ExceptionHandler(IllegalArgumentException::class)
    fun badRequest(exception: IllegalArgumentException): ProblemDetail =
        ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            exception.message ?: "Bad request",
        )
}

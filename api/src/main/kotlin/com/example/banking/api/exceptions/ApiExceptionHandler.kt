package com.example.banking.api.exceptions

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

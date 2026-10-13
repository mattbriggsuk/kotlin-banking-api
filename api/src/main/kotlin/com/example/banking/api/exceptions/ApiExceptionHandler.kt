package com.example.banking.api.exceptions
import org.springframework.http.*
import org.springframework.web.bind.annotation.*

@RestControllerAdvice
class ApiExceptionHandler {
    @ExceptionHandler(NoSuchElementException::class) fun notFound(e: NoSuchElementException) = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.message ?: "Not found")
    @ExceptionHandler(IllegalStateException::class) fun conflict(e: IllegalStateException) = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, e.message ?: "Conflict")
    @ExceptionHandler(IllegalArgumentException::class) fun badRequest(e: IllegalArgumentException) = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.message ?: "Bad request")
}

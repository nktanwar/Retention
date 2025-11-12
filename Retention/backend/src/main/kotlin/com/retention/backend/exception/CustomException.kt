package com.retention.backend.exception

import org.springframework.http.HttpStatus

// Make sure this is imported in your GlobalExceptionHandler
class CustomException(
    override val message: String,
    val status: HttpStatus = HttpStatus.BAD_REQUEST
) : RuntimeException(message)

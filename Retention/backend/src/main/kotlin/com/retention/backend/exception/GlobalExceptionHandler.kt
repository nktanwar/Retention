package com.retention.backend.exception


import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import java.time.LocalDateTime

data class ErrorResponse(
    val timestamp: LocalDateTime,
    val status: Int,
    val error: String,
    val message: String?,
    val path: String?
)

@ControllerAdvice
class GlobalExceptionHandler {

    // Handle generic exceptions
    @ExceptionHandler(Exception::class)
    fun handleAllExceptions(ex: Exception, request: WebRequest): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse(
            timestamp = LocalDateTime.now(),
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            error = "Internal Server Error",
            message = ex.message,
            path = request.getDescription(false)
        )
        return ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    // Handle validation errors (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException, request: WebRequest): ResponseEntity<ErrorResponse> {
        val errors = ex.bindingResult.allErrors
            .joinToString("; ") { error ->
                if (error is FieldError) "${error.field}: ${error.defaultMessage ?: "Invalid"}"
                else error.defaultMessage ?: "Invalid"
            }


        val response = ErrorResponse(
            timestamp = LocalDateTime.now(),
            status = HttpStatus.BAD_REQUEST.value(),
            error = "Validation Failed",
            message = errors,
            path = request.getDescription(false)
        )
        return ResponseEntity(response, HttpStatus.BAD_REQUEST)
    }

    // Handle custom exceptions (example: JWT expired)
    @ExceptionHandler(CustomException::class)
    fun handleCustomException(ex: CustomException, request: WebRequest): ResponseEntity<ErrorResponse> {
        val response = ErrorResponse(
            timestamp = LocalDateTime.now(),
            status = ex.status.value(),
            error = ex.status.reasonPhrase,
            message = ex.message,
            path = request.getDescription(false)
        )
        return ResponseEntity(response, ex.status)
    }
}

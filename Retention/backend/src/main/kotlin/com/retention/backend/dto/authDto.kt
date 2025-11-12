package com.retention.backend.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class SignUpDto(
    @field:NotBlank(message = "Name is required")
    val name: String,

    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email should be valid")
    val email: String,

    @field:NotBlank(message = "Password is required")
    val password: String
)

data class LoginDto(
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email should be valid")
    val email: String,

    @field:NotBlank(message = "Password is required")
    val password: String
)

data class AuthResponse(
    val token: String,
    val id: String,
    val name: String,
    val email: String
)

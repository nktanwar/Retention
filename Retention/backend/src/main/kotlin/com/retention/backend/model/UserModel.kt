package com.retention.backend.model

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.springframework.data.annotation.Id

data class UserModel(
    @Id
    val id : String ?= null,
    @field:NotBlank(message = "Name is required")
    val name: String,

    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email should be valid")
    val email: String,

    @field:NotBlank(message = "Password is required")
    val password: String,

    val level : Int = 0,

    val refreshToken: String? = null
)

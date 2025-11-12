package com.retention.backend.controller

import com.retention.backend.dto.SignUpDto
import com.retention.backend.dto.LoginDto
import com.retention.backend.dto.AuthResponse
import com.retention.backend.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import jakarta.validation.Valid

@RestController
@RequestMapping("/auth")
class AuthController(
    private val userService: UserService
) {

    @PostMapping("/signup")
    fun signUpHandler(@Valid @RequestBody request: SignUpDto): ResponseEntity<Any> {
        return try {
            print("signup request received: $request")
            userService.createUser(request)
            ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully")
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.CONFLICT).body(ex.message)
        }
    }

    @PostMapping("/login")
    fun loginHandler(@Valid @RequestBody request: LoginDto): ResponseEntity<Any> {
        return try {
            val (user, token) = userService.loginUser(request)
            val response = AuthResponse(
                token = token,
                id = user.id.toString(),
                name = user.name,
                email = user.email
            )
            ResponseEntity.ok(response)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.message)
        }
    }
}

package com.retention.backend.service

import com.retention.backend.dto.SignUpDto
import com.retention.backend.dto.LoginDto
import com.retention.backend.model.UserModel
import com.retention.backend.repository.UserRepository
import com.retention.backend.utils.JwtUtil
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtUtil: JwtUtil
) {

    // Create a new user (signup)
    fun createUser(signUpDto: SignUpDto): UserModel {
        if (userRepository.existsByEmail(signUpDto.email)) {
            throw IllegalArgumentException("Email already exists")
        }

        val user = UserModel(
            name = signUpDto.name,
            email = signUpDto.email,
            password = passwordEncoder.encode(signUpDto.password)
        )
        return userRepository.save(user)
    }

    // Login user and return JWT
    fun loginUser(loginDto: LoginDto): Pair<UserModel, String> {
        val user = userRepository.findByEmail(loginDto.email)
            ?: throw IllegalArgumentException("Invalid credentials")

        if (!passwordEncoder.matches(loginDto.password, user.password)) {
            throw IllegalArgumentException("Invalid credentials")
        }

        val token = jwtUtil.generateToken(user.email, user.id!!)
        return Pair(user, token)
    }

    fun getUserById(userId: String): UserModel {
        return userRepository.findById(userId).orElseThrow { IllegalArgumentException("User not found") }
    }
}

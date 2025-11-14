package com.retention.backend.controller

import com.retention.backend.repository.UserRepository
import com.retention.backend.utils.UserPrincipal
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/user")
class UserController (
    private val userRepository: UserRepository

){
    @GetMapping("/profile")
    fun getUserProfile(): ResponseEntity<Map<String, Any>> {
       val authentication = SecurityContextHolder.getContext().authentication
        val user = authentication.principal as UserPrincipal
        val userProfile = userRepository.findById(user.id).orElseThrow{ Exception("User not found") }
        return ResponseEntity.ok(
            mapOf<String, Any>(
                "id" to (userProfile.id as Any),
                "name" to (userProfile.name as Any),
                "email" to (userProfile.email as Any),
                "level" to (userProfile.level as Any)
            )
        )


    }




}
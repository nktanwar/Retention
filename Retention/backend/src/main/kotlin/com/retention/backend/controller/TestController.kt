package com.retention.backend.controller

import com.retention.backend.dto.PublicTestType
import com.retention.backend.dto.UserTestSubmitDto
import com.retention.backend.model.Subject
import com.retention.backend.model.UserModel
import com.retention.backend.service.UserTestService
import com.retention.backend.utils.UserPrincipal
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user/tests")
class UserTestController(
    private val userTestService: UserTestService
) {

    /**
     * ✅ Generate test based on user level and subject selection
     */
    @PostMapping("/generate")
    fun generateUserTest(
        @RequestParam type: PublicTestType,
        @RequestBody subjects: List<Subject>
    ): ResponseEntity<Any> {
        val user = getCurrentUser()

        val session = userTestService.generateUserTest(user, type, subjects)
        return ResponseEntity.ok(session)
    }

    /**
     * ✅ Submit test answers, calculate score, and update user level
     */
    @PostMapping("/submit")
    fun submitUserTest(@RequestBody submitDto: UserTestSubmitDto): ResponseEntity<Any> {
        val updatedSession = userTestService.submitUserTest(submitDto)
        return ResponseEntity.ok(updatedSession)
    }

    /**
     * 🧠 Utility — extract current logged-in user from SecurityContext
     */
    private fun getCurrentUser(): UserModel {
        val auth = SecurityContextHolder.getContext().authentication
        val user = auth.principal as UserPrincipal


            // You’d normally fetch user from DB
            return UserModel(
                id = user.id,
                name = user.name,
                email = "${user.name}@example.com", // placeholder
                password = "",
                level = 0
            )

    }
}

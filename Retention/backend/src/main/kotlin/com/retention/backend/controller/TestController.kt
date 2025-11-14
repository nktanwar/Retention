package com.retention.backend.controller

import com.retention.backend.dto.PublicTestDto
import com.retention.backend.dto.PublicTestType
import com.retention.backend.dto.TestResultResponseDto
import com.retention.backend.dto.UserTestSubmitDto
import com.retention.backend.model.Subject
import com.retention.backend.model.TestType
import com.retention.backend.model.UserModel
import com.retention.backend.service.UserTestService
import com.retention.backend.utils.UserPrincipal
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user/tests")
@PreAuthorize("hasRole('USER')")
class UserTestController(
    private val userTestService: UserTestService
) {

    @GetMapping("/health")
    fun healthCheck(): String {
        return "UserTestController is up and running!"
    }

    /**
     * ✅ Generate test based on user level and subject selection
     */
    @PostMapping("/generate")
    fun generateUserTest(@RequestBody request: PublicTestDto): ResponseEntity<Any> {

        print(request)

        val user = getCurrentUser()

        val session = userTestService.generateUserTest(user, request.type, request.subject)
        return ResponseEntity.ok(session)
    }

    /**
     * ✅ Submit test answers, calculate score, and update user level
     */
    @PostMapping("/submit")
    fun submitUserTest(@RequestBody submitDto: UserTestSubmitDto): ResponseEntity<Any> {

        print("📩 [SUBMIT] Incoming payload: $submitDto")

        val updatedSession = userTestService.submitUserTest(submitDto)

        print("📤 [SUBMIT] Updated session returned from service: $updatedSession")
        print("📤 [SUBMIT] Updated session userAnswers: ${updatedSession.userAnswers}")
        print("📤 [SUBMIT] Updated session scorePercent: ${updatedSession.scorePercent}")
        print("📤 [SUBMIT] Updated session obtainedMarks: ${updatedSession.obtainedMarks}")
        print("📤 [SUBMIT] Updated session totalMarks: ${updatedSession.totalMarks}")

        val response = TestResultResponseDto(
            sessionId = updatedSession.id ?: "",
            testTitle = when (updatedSession.type) {
                TestType.LONGTEST -> "Full GATE Test"
                TestType.QUICKTEST -> "Quick Test"
            },
            totalTime = null,
            timestamp = updatedSession.submittedAt!!,

            scorePercent = updatedSession.scorePercent ?: 0.0,
            totalMarks = updatedSession.totalMarks ?: 0.0,
            obtainedMarks = updatedSession.obtainedMarks ?: 0.0,
            levelBefore = updatedSession.levelBefore,
            levelAfter = updatedSession.levelAfter,

            answers = updatedSession.userAnswers,
            questions = updatedSession.questions
        )

        print("📤 [SUBMIT] Final response prepared: $response")

        return ResponseEntity.ok(response)
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


    @GetMapping("/history")
    fun getUserTestHistory(): ResponseEntity<Any> {
        val user = getCurrentUser()
        val history = userTestService.getUserTestHistory(user.id!!)
        return ResponseEntity.ok(history)
    }


    @GetMapping("/{sessionId}")
    fun getTestResult(@PathVariable sessionId: String): ResponseEntity<Any> {
        val user = getCurrentUser()
        val result = userTestService.getTestResultDetails(sessionId, user.id!!)
        return ResponseEntity.ok(result)
    }



//    @GetMapping("/{sessionId}")
//    fun getTestResult(@PathVariable sessionId: String): ResponseEntity<Any> {
//        val user = getCurrentUser()
//        val session = userTestService.getTestResultDetails(sessionId, user.id!!)
//
//        val response = TestResultResponseDto(
//            sessionId = session.sessionId,
//            testTitle = session.testTitle,
//            totalTime = session.totalTime,
//            timestamp = session.timestamp,
//
//            scorePercent = session.scorePercent,
//            totalMarks = session.totalMarks,
//            obtainedMarks = session.obtainedMarks,
//            levelBefore = session.levelBefore,
//            levelAfter = session.levelAfter,
//
//            // HISTORY VIEW DOESN’T HAVE USER’S ANSWERS
//            answers = session.answers,
//
//            // but questions list still helps display correct answers
//            questions = session.questions
//        )
//
//        return ResponseEntity.ok(response)
//    }


}

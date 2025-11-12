package com.retention.backend.controller

import com.retention.backend.dto.PublicTestDto
import com.retention.backend.service.PublicTestService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/public")
class PublicController(
    private val publicTestService: PublicTestService
) {

    @PostMapping("/firstTest")
    fun getFirstTest(@Valid @RequestBody request: PublicTestDto): List<Any> {

        print("request received: $request")
        val questions = publicTestService.getPublicTest(request)
        // You may map QuestionModel to a DTO for public exposure if needed
        return questions
    }

    @GetMapping("/health")
    fun healthCheck(): String {
        return "PublicController is up and running!"
    }
}
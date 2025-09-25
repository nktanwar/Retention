package com.retention.backend.controller

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController


@RestController("/questions")
class QuestionController {
    @PostMapping("/add")
    fun addQuestion() {

    }
}
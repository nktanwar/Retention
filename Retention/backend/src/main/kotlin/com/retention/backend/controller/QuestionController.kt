package com.retention.backend.controller

import com.retention.backend.dto.AddQuestionRequestDto
import com.retention.backend.model.QuestionModel
import com.retention.backend.service.QuestionService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/questions")
class QuestionController(
    private val questionService: QuestionService
) {
    @PostMapping("/add")
    fun addQuestion(@RequestBody request: AddQuestionRequestDto): QuestionModel =
        questionService.addQuestion(request)
}

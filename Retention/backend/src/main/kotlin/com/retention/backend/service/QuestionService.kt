package com.retention.backend.service

import com.retention.backend.dto.AddQuestionRequestDto
import com.retention.backend.model.QuestionModel
import com.retention.backend.repository.QuestionRepository
import org.springframework.stereotype.Service

@Service
class QuestionService (
    private val questionRepository: QuestionRepository
){
    fun addQuestion(dto: AddQuestionRequestDto): QuestionModel {
        val entity = QuestionModel(
            subject = dto.subject,
            questionText = dto.questionText,
            options = dto.options,
            correctAnswer = dto.correctAnswer,
            difficulty = dto.difficulty,
            topic = dto.topic,
            subTopic = dto.subTopic
        )
        return questionRepository.save(entity)
    }
}
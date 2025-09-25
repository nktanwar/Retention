package com.retention.backend.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "questions")
data class QuestionModel(
    @Id
    val id: String? = null,
    val subject: String,
    val questionText: String,
    val options: List<String>,
    val correctAnswer: String,
    val difficulty: String,
    val topic : List<String>,
    val subTopic : List<String>? = null,
)

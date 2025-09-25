package com.retention.backend.dto


data class AddQuestionRequestDto(
    val subject: String,
    val questionText: String,
    val options: List<String>,
    val correctAnswer: String,
    val difficulty: String,
    val topic: List<String>,
    val subTopic: List<String>? = null
)

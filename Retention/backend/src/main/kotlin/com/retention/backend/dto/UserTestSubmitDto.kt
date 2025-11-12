package com.retention.backend.dto


data class UserTestSubmitDto(
    val sessionId: String,
    val answers: Map<String, List<String>> // questionId → selected answers
)

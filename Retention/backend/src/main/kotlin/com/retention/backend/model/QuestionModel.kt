package com.retention.backend.model

import org.springframework.data.annotation.Id

data class QuestionModel(
    @Id
    val id: String? = null,
)

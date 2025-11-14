package com.retention.backend.model

// com.retention.backend.model.UserActivityModel.kt

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

@Document(collection = "user_activities")
data class UserActivityModel(
    @Id
    val id: String? = null,
    val userId: String,
    val date: LocalDate,
    val activityCount: Int = 0
)

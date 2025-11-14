package com.retention.backend.repository

// com.retention.backend.repository.UserActivityRepository.kt

import com.retention.backend.model.UserActivityModel
import org.springframework.data.mongodb.repository.MongoRepository
import java.time.LocalDate

interface UserActivityRepository : MongoRepository<UserActivityModel, String> {
    fun findByUserIdAndDateBetween(userId: String, start: LocalDate, end: LocalDate): List<UserActivityModel>
    fun findByUserIdAndDate(userId: String, date: LocalDate): UserActivityModel?
}

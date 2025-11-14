package com.retention.backend.service

// com.retention.backend.service.UserActivityService.kt

import com.retention.backend.model.UserActivityModel
import com.retention.backend.repository.UserActivityRepository
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class UserActivityService(
    private val repo: UserActivityRepository
) {
    fun getUserMonthlyActivity(userId: String, year: Int, month: Int): List<UserActivityModel> {
        val start = LocalDate.of(year, month, 1)
        val end = start.withDayOfMonth(start.lengthOfMonth())
        return repo.findByUserIdAndDateBetween(userId, start, end)
    }

    fun recordActivity(userId: String) {
        val today = LocalDate.now()
        val existing = repo.findByUserIdAndDate(userId, today)
        if (existing != null) {
            repo.save(existing.copy(activityCount = existing.activityCount + 1))
        } else {
            repo.save(UserActivityModel(userId = userId, date = today, activityCount = 1))
        }
    }
}

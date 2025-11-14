package com.retention.backend.repository


import com.retention.backend.model.TestSession
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface TestSessionRepository : MongoRepository<TestSession, String> {
    fun findByUserId(userId: String): List<TestSession>

    fun findByUserIdAndSubmittedAtIsNotNull(userId: String): List<TestSession>
    fun findByUserIdAndSubmittedAtBetween(userId: String, start: Instant, end: Instant): List<TestSession>
}

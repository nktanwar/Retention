package com.retention.backend.repository


import com.retention.backend.model.TestSession
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface TestSessionRepository : MongoRepository<TestSession, String> {
    fun findByUserId(userId: String): List<TestSession>
}

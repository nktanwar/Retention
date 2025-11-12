package com.retention.backend.repository

import com.retention.backend.model.UserModel
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : MongoRepository<UserModel, String> {
    fun existsByEmail(email: String): Boolean
    fun findByEmail(email: String): UserModel?
}

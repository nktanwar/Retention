package com.retention.backend.repository

import com.retention.backend.model.QuestionModel
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface QuestionRepository : MongoRepository<QuestionModel, String>
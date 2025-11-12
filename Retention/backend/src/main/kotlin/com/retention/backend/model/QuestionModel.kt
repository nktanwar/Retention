package com.retention.backend.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.aggregation.ArrayOperators
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "questions")
data class QuestionModel(
    @Id
    val id: String? = null,
    val subject: Subject,
    val questionText: String,
    val options: List<String>,
    val correctAnswer: List<String>,
    val difficulty: String,
    val topic : List<String>,
    val subTopic : List<String>? = null,
    val tags : List<String>? = null,
    val imageUrl : String? = null,
    val questionType : QuestionType,
    val pyqYear : Int? = null,
    val marks : Int? = null

)

enum class QuestionType {
    MSQ,
    MCQ,
    NAT
}

enum class Subject {
    EM,   // Engineering Mathematics
    DM,   // Discrete Mathematics
    DL,   // Digital Logic
    CO,   // Computer Organization / Architecture
    PDS,  // Programming and Data Structures
    ALGO, // Algorithms
    TOC,  // Theory of Computation
    CD,   // Compiler Design
    OS,   // Operating Systems
    DBMS, // Databases
    CN,   // Computer Networks
    SE, // Software Engineering,
    GA,  // General Aptitude

}

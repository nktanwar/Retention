package com.retention.backend.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "test_sessions")
data class TestSession(
    @Id
    val id: String? = null,

    val userId: String,
    val type: TestType,
    val subjects: List<Subject>,
    val questions: List<TestQuestion>,

    val startedAt: Instant = Instant.now(),
    var submittedAt: Instant? = null, // ✅ renamed (use this instead of completedAt)

    val durationMinutes: Int,

    val levelBefore: Int,
    var levelAfter: Int? = null,

    var scorePercent: Double? = null,   // ✅ add this
    var totalMarks: Double? = null,     // ✅ Double for consistency
    var obtainedMarks: Double? = null,   // ✅ Double for consistency
    var userAnswers: Map<String, List<String>> = emptyMap()
)

data class TestQuestion(
    val questionId: String,
    val subject: Subject,
    val questionText: String,
    val options: List<String>,
    val difficulty: String,
    val marks: Int? = 1,
    val questionType: QuestionType,
    val correctAnswer: List<String>
)

enum class TestType {
    QUICKTEST,
    LONGTEST
}

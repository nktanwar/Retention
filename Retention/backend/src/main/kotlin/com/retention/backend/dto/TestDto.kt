package com.retention.backend.dto
import com.retention.backend.model.TestType
import java.time.Instant
import com.retention.backend.model.Subject
import com.retention.backend.model.TestQuestion
data class PublicTestDto(
    val type : PublicTestType,
    val subject : List<Subject>
)

enum class PublicTestType {
    QUICKTEST,
    LONGTEST
}

data class DailyActivityDto(
    val date: String, // ISO date e.g. "2025-11-10"
    val count: Int
)




data class TestHistoryResponseDto(
    val id: String,
    val title: String,
    val type: TestType,
    val date: Instant,
    val level: Int,
    val scorePercent: Double
)




data class TestResultResponseDto(
    val sessionId: String,
    val testTitle: String,
    val totalTime: Int?, // seconds (null from backend if you don’t track time yet)
    val timestamp: Instant,

    val scorePercent: Double,
    val totalMarks: Double,
    val obtainedMarks: Double,
    val levelBefore: Int,
    val levelAfter: Int?,

    val answers: Map<String, List<String>>,
    val questions: List<TestQuestion>,
)



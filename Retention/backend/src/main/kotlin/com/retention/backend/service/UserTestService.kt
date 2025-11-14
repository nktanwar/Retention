package com.retention.backend.service

import com.retention.backend.dto.PublicTestType
import com.retention.backend.dto.TestHistoryResponseDto
import com.retention.backend.dto.TestResultResponseDto
import com.retention.backend.dto.UserTestSubmitDto
import com.retention.backend.model.*
import com.retention.backend.repository.QuestionRepository
import com.retention.backend.repository.TestSessionRepository
import com.retention.backend.repository.UserRepository
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class UserTestService(
    private val questionRepository: QuestionRepository,
    private val testSessionRepository: TestSessionRepository,
    private val userRepository: UserRepository,
    private val levelService: LevelService
) {

    /**
     * Generates a personalized test for the logged-in user
     */
    fun generateUserTest(user: UserModel, type: PublicTestType, subjects: List<Subject>): TestSession {
        val totalQuestions = when (type) {
            PublicTestType.QUICKTEST -> 10
            PublicTestType.LONGTEST -> 65
        }

        val duration = when (type) {
            PublicTestType.QUICKTEST -> 30
            PublicTestType.LONGTEST -> 180
        }

        val allSubjects = Subject.entries.toSet()
        val selectedQuestions = if (type == PublicTestType.LONGTEST && subjects.contains(Subject.ALL)) {
            generateAllSubjectsTest() // 👈 our new function
        } else {
            generateNormalTest(user, type, subjects)
        }

        val questionSnapshots = selectedQuestions.map {
            TestQuestion(
                questionId = it.id ?: "",
                subject = it.subject,
                questionText = it.questionText,
                options = it.options,
                difficulty = it.difficulty,
                marks = it.marks ?: 1,
                questionType = it.questionType,
                correctAnswer = it.correctAnswer
            )
        }

        val session = TestSession(
            userId = user.id ?: throw IllegalStateException("User must have valid ID"),
            type = TestType.valueOf(type.name),
            subjects = subjects,
            questions = questionSnapshots,
            durationMinutes = duration,
            levelBefore = user.level,
            startedAt = Instant.now()
        )

        return testSessionRepository.save(session)
    }

    private fun generateNormalTest(user: UserModel, type: PublicTestType, subjects: List<Subject>): List<QuestionModel> {
        val totalQuestions = when (type) {
            PublicTestType.QUICKTEST -> 10
            PublicTestType.LONGTEST -> 65
        }

        val difficultySplit = levelService.calculateQuestionSplit(totalQuestions, user.level)
        val allQuestions = questionRepository.findBySubjectIn(subjects)
        if (allQuestions.isEmpty()) throw IllegalStateException("No questions found for selected subjects")

        val selectedQuestions = mutableListOf<QuestionModel>()

        val easyQuestions = allQuestions.filter { it.difficulty.equals("Easy", true) }.shuffled()
        val mediumQuestions = allQuestions.filter { it.difficulty.equals("Medium", true) }.shuffled()
        val hardQuestions = allQuestions.filter { it.difficulty.equals("Hard", true) }.shuffled()

        selectedQuestions.addAll(easyQuestions.take(difficultySplit["Easy"] ?: 0))
        selectedQuestions.addAll(mediumQuestions.take(difficultySplit["Medium"] ?: 0))
        selectedQuestions.addAll(hardQuestions.take(difficultySplit["Hard"] ?: 0))

        if (selectedQuestions.size < totalQuestions) {
            val remaining = allQuestions.filter { !selectedQuestions.contains(it) }.shuffled()
            selectedQuestions.addAll(remaining.take(totalQuestions - selectedQuestions.size))
        }

        return selectedQuestions
    }

    // 🆕 NEW FUNCTION
    private fun generateAllSubjectsTest(): List<QuestionModel> {
        val subjectDistribution = mapOf(
            Subject.GA to 10,
            Subject.PDS to (8..10).random(),
            Subject.CO to (5..8).random(),
            Subject.OS to (6..7).random(),
            Subject.CN to (4..7).random(),
            Subject.DM to (4..6).random(),
            Subject.ALGO to (5..8).random(),
            Subject.DBMS to (5..7).random(),
            Subject.TOC to (4..6).random(),
            Subject.DL to (3..4).random(),
            Subject.CD to (2..5).random()
        )

        val allSelected = mutableListOf<QuestionModel>()

        // Step 1: Pick questions per subject
        subjectDistribution.forEach { (subject, count) ->
            val questions = questionRepository.findBySubjectIn(listOf(subject)).shuffled()
            allSelected.addAll(questions.take(count))
        }

        // Step 2: Adjust to exactly 65
        if (allSelected.size > 65) {
            allSelected.shuffle()
            return allSelected.take(65)
        } else if (allSelected.size < 65) {
            val all = questionRepository.findAll().shuffled()
            val remaining = all.filterNot { allSelected.contains(it) }
            allSelected.addAll(remaining.take(65 - allSelected.size))
        }

        // Step 3: Enforce type split
        val mcqs = allSelected.filter { it.questionType == QuestionType.MCQ }.take(32)
        val msqs = allSelected.filter { it.questionType == QuestionType.MSQ }.take(15)
        val nats = allSelected.filter { it.questionType == QuestionType.NAT }.take(18)

        val merged = (mcqs + msqs + nats).shuffled()
        return merged.take(65)
    }









    /**
     * Submits the user’s test, calculates result, updates user level.
     */
    fun submitUserTest(submitDto: UserTestSubmitDto): TestSession {
        print("▶️ [SERVICE] Submitting test for sessionId=${submitDto.sessionId}")

        val session = testSessionRepository.findById(submitDto.sessionId)
            .orElseThrow { IllegalArgumentException("Test session not found") }



        if (session.submittedAt != null) {
            print("⚠️ [SERVICE] Session already submitted! sessionId=${session.id}")
            throw IllegalStateException("This test has already been submitted")
        }

        var totalMarks = 0
        var scoredMarks = 0


        session.questions.forEach { question ->
            val userAnswersRaw = submitDto.answers[question.questionId] ?: emptyList()

            totalMarks += question.marks ?: 0

            // Normalize: Convert "actual label" -> "Option X"
            val normalizedUserAnswers = userAnswersRaw.map { raw ->
                // find index of the selected option
                val index = question.options.indexOf(raw)
                if (index != -1) {
                    "Option ${index + 1}"
                } else {
                    // If it does not match any option, keep raw
                    // (for NAT or future question types)
                    raw
                }
            }

            val correctAnswers = question.correctAnswer.map { it.trim() }

            println("🔍 [EVAL] Q=${question.questionId}")
            println("🔍 User Raw: $userAnswersRaw")
            println("🔍 Normalized: $normalizedUserAnswers")
            println("🔍 Correct: $correctAnswers")

            // Compare sets (supports MCQ + MSQ)
            if (normalizedUserAnswers.toSet() == correctAnswers.toSet()) {
                scoredMarks += question.marks ?: 0
                println("✔ CORRECT")
            } else {
                println("❌ WRONG")
            }
        }


        val percentage = if (totalMarks > 0) (scoredMarks * 100.0 / totalMarks) else 0.0
        print("📈 [SERVICE] Evaluation complete: scored=$scoredMarks / total=$totalMarks → ${percentage}%")

        val user = userRepository.findById(session.userId)
            .orElseThrow { IllegalArgumentException("User not found for test session") }

        val newLevel = levelService.calculateNewLevel(user.level, percentage)

        userRepository.save(user.copy(level = newLevel))


        val updatedSession = session.copy(
            levelAfter = newLevel,
            submittedAt = Instant.now(),
            totalMarks = totalMarks.toDouble(),
            obtainedMarks = scoredMarks.toDouble(),
            scorePercent = percentage,
            userAnswers = submitDto.answers
        )



        val saved = testSessionRepository.save(updatedSession)

        print("🟢 [SERVICE] Saved session scorePercent: ${saved.scorePercent}")

        return saved
    }


    fun getUserTestHistory(userId: String): List<Map<String, Any>> {
        val sessions = testSessionRepository.findByUserIdAndSubmittedAtIsNotNull(userId)

        return sessions.map { session ->
            mapOf<String,Any>(
                "id" to session.id!!,
                "title" to (
                        if (session.type == TestType.LONGTEST)
                            "Full GATE Test"
                        else
                            session.subjects.joinToString(", ") + " Test"
                        ),
                "type" to session.type.name,
                "date" to (session.submittedAt as Any),
                "level" to (session.levelAfter ?: session.levelBefore),
                "scorePercent" to (session.scorePercent ?: 0.0)
            )
        }
    }


    fun getTestResultDetails(sessionId: String, userId: String): TestResultResponseDto {
        val session = testSessionRepository.findById(sessionId)
            .orElseThrow { IllegalArgumentException("Test session not found") }

        if (session.userId != userId)
            throw IllegalAccessException("You cannot access someone else's test")

        return TestResultResponseDto(
            sessionId = session.id ?: "",
            testTitle = when(session.type) {
                TestType.LONGTEST -> "Full GATE Test"
                TestType.QUICKTEST -> {
                    if (session.subjects.isEmpty()) "Quick Test"
                    else session.subjects.joinToString(", ") + " Test"
                }
            },
            totalTime = session.durationMinutes * 60,
            timestamp = session.submittedAt ?: Instant.now(),

            scorePercent = session.scorePercent ?: 0.0,
            totalMarks = session.totalMarks ?: 0.0,
            obtainedMarks = session.obtainedMarks ?: 0.0,

            levelBefore = session.levelBefore,
            levelAfter = session.levelAfter ?: session.levelBefore,

            answers = session.userAnswers,
            questions = session.questions
        )
    }


    /**
     * Generates a readable title for test history.
     */
    private fun generateTestTitle(session: TestSession): String {
        return when (session.type) {
            TestType.LONGTEST -> "Full GATE Test"
            TestType.QUICKTEST -> {
                val subs = session.subjects.filter { it != Subject.ALL }
                if (subs.isEmpty()) "Quick Test"
                else "${subs.joinToString(", ")} Test"
            }
        }
    }


}

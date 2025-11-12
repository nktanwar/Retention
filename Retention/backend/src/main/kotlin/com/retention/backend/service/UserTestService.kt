package com.retention.backend.service

import com.retention.backend.dto.PublicTestType
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

        if (type == PublicTestType.LONGTEST && subjects.contains(Subject.GA).not()) {
            val gaQuestions = questionRepository.findBySubjectIn(listOf(Subject.GA))
            selectedQuestions.addAll(gaQuestions.shuffled().take(10))
        }

        if (selectedQuestions.size < totalQuestions) {
            val remaining = allQuestions.filter { !selectedQuestions.contains(it) }.shuffled()
            selectedQuestions.addAll(remaining.take(totalQuestions - selectedQuestions.size))
        }

        val questionSnapshots = selectedQuestions.take(totalQuestions).map {
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

    /**
     * Submits the user’s test, calculates result, updates user level.
     */
    fun submitUserTest(submitDto: UserTestSubmitDto): TestSession {
        val session = testSessionRepository.findById(submitDto.sessionId)
            .orElseThrow { IllegalArgumentException("Test session not found") }

        if (session.submittedAt != null) {
            throw IllegalStateException("This test has already been submitted")
        }


        var totalMarks = 0
        var scoredMarks = 0

        // Step 1: evaluate each question
        session.questions.forEach { question ->
            val userAnswer = submitDto.answers[question.questionId]
            totalMarks += question.marks?: 0

            if (userAnswer != null && userAnswer.toSet() == question.correctAnswer.toSet()) {
                scoredMarks += question.marks?:0
            }
        }

        // Step 2: calculate percentage
        val percentage = if (totalMarks > 0) (scoredMarks * 100.0 / totalMarks) else 0.0

        // Step 3: update user level
        val user = userRepository.findById(session.userId)
            .orElseThrow { IllegalArgumentException("User not found for test session") }

        val newLevel = levelService.calculateNewLevel(user.level, percentage)




        // Step 4: persist updates
        userRepository.save(user.copy(level = newLevel))
        val updatedSession = session.copy(
            levelAfter = newLevel,
            submittedAt = Instant.now(),
            totalMarks = totalMarks.toDouble(),
            obtainedMarks = scoredMarks.toDouble(),
            scorePercent = percentage
        )

        return testSessionRepository.save(updatedSession)
    }
}

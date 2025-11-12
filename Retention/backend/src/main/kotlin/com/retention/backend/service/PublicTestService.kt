package com.retention.backend.service

import com.retention.backend.dto.PublicTestDto
import com.retention.backend.dto.PublicTestType
import com.retention.backend.model.QuestionModel
import com.retention.backend.repository.QuestionRepository
import org.springframework.stereotype.Service
import kotlin.math.ceil

@Service
class PublicTestService(
    private val questionRepository: QuestionRepository
) {

    fun getPublicTest(request: PublicTestDto): List<QuestionModel> {
        val subjects = request.subject
        val totalQuestions = when(request.type) {
            PublicTestType.QUICKTEST -> 10
            PublicTestType.LONGTEST -> 25
        }

        val allQuestions = questionRepository.findBySubjectIn(subjects)
        if(allQuestions.isEmpty()) return emptyList()

        // Group questions by subject and difficulty
        val questionsBySubject = allQuestions.groupBy { it.subject }

        val selectedQuestions = mutableListOf<QuestionModel>()

        // Step 1: Pick one question per subject
        for(subject in subjects) {
            val subQuestions = questionsBySubject[subject]?.shuffled() ?: continue
            selectedQuestions.add(subQuestions.first())
        }

        // Step 2: Fill remaining questions by difficulty ratio
        val remainingCount = totalQuestions - selectedQuestions.size
        val easyCount = ceil(0.5 * totalQuestions).toInt()
        val mediumCount = ceil(0.4 * totalQuestions).toInt()
        val hardCount = totalQuestions - easyCount - mediumCount

        val easyQuestions = allQuestions.filter { it.difficulty.equals("Easy", true) && !selectedQuestions.contains(it) }.shuffled()
        val mediumQuestions = allQuestions.filter { it.difficulty.equals("Medium", true) && !selectedQuestions.contains(it) }.shuffled()
        val hardQuestions = allQuestions.filter { it.difficulty.equals("Hard", true) && !selectedQuestions.contains(it) }.shuffled()

        selectedQuestions.addAll(easyQuestions.take(easyCount - selectedQuestions.count { it.difficulty.equals("Easy", true) }))
        selectedQuestions.addAll(mediumQuestions.take(mediumCount - selectedQuestions.count { it.difficulty.equals("Medium", true) }))
        selectedQuestions.addAll(hardQuestions.take(hardCount - selectedQuestions.count { it.difficulty.equals("Hard", true) }))

        // Step 3: If still less than total, pick random remaining questions
        if(selectedQuestions.size < totalQuestions) {
            val remaining = allQuestions.filter { !selectedQuestions.contains(it) }.shuffled()
            selectedQuestions.addAll(remaining.take(totalQuestions - selectedQuestions.size))
        }

        return selectedQuestions.shuffled()
    }
}

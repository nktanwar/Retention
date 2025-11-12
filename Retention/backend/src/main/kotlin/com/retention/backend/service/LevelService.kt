package com.retention.backend.service


import org.springframework.stereotype.Service
import kotlin.math.roundToInt

@Service
class LevelService {

    /**
     * Returns difficulty distribution (Easy, Medium, Hard) for a given level.
     * Used during test generation.
     */
    fun getDifficultyDistribution(level: Int): Map<String, Double> {
        return when (level) {
            in 0..1 -> mapOf("Easy" to 0.75, "Medium" to 0.20, "Hard" to 0.05)
            in 2..4 -> mapOf("Easy" to 0.60, "Medium" to 0.30, "Hard" to 0.10)
            in 5..7 -> mapOf("Easy" to 0.40, "Medium" to 0.45, "Hard" to 0.15)
            in 8..10 -> mapOf("Easy" to 0.25, "Medium" to 0.50, "Hard" to 0.25)
            else -> mapOf("Easy" to 0.50, "Medium" to 0.40, "Hard" to 0.10)
        }
    }

    /**
     * Determines new user level after test submission.
     *
     * @param currentLevel user's current level (0–10)
     * @param scorePercent test score as percentage
     * @return updated level (still capped between 0–10)
     */
    fun calculateNewLevel(currentLevel: Int, scorePercent: Double): Int {
        val newLevel = when {
            scorePercent >= 80 -> currentLevel + 1
            scorePercent < 50 -> currentLevel - 1
            else -> currentLevel
        }

        // Ensure level remains between 0–10
        return newLevel.coerceIn(0, 10)
    }

    /**
     * Given a total number of questions and a difficulty ratio,
     * return how many Easy/Medium/Hard questions to pick.
     */
    fun calculateQuestionSplit(totalQuestions: Int, level: Int): Map<String, Int> {
        val ratio = getDifficultyDistribution(level)

        val easy = (totalQuestions * ratio["Easy"]!!).roundToInt()
        val medium = (totalQuestions * ratio["Medium"]!!).roundToInt()
        val hard = totalQuestions - easy - medium // ensure total sums exactly

        return mapOf(
            "Easy" to easy,
            "Medium" to medium,
            "Hard" to hard
        )
    }
}

package com.example.matemath.data

import androidx.compose.ui.graphics.Color
import kotlin.random.Random

// Core Learning Path Structure
data class LearningPath(
    val units: List<LearningUnit>,
    val totalXP: Int = 0,
    val completedUnits: Int = 0
)

data class LearningUnit(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val lessons: List<Lesson>,
    val isUnlocked: Boolean = false,
    val completionPercentage: Float = 0f,
    val xpEarned: Int = 0,
    val targetXP: Int = 100,
    val category: MathCategory
)

data class Lesson(
    val id: String,
    val title: String,
    val concept: MathConcept,
    val problems: List<MathProblem>,
    val isCompleted: Boolean = false,
    val masteryLevel: MasteryLevel = MasteryLevel.NOT_STARTED,
    val xpValue: Int = 20
)

enum class MathCategory(val displayName: String, val color: Color) {
    ADDITION("Suma", Color(0xFF4CAF50)),
    SUBTRACTION("Resta", Color(0xFF2196F3)),
    MULTIPLICATION("Multiplicación", Color(0xFFFF9800)),
    DIVISION("División", Color(0xFF9C27B0)),
    MIXED("Mixto", Color(0xFF607D8B))
}

enum class MathConcept(val displayName: String, val description: String) {
    SINGLE_DIGIT_ADDITION("Suma de 1 dígito", "Sumar números del 1 al 9"),
    DOUBLE_DIGIT_ADDITION("Suma de 2 dígitos", "Sumar números del 10 al 99"),
    ADDITION_WITH_CARRYING("Suma con llevada", "Suma que requiere llevar números"),
    SINGLE_DIGIT_SUBTRACTION("Resta de 1 dígito", "Restar números del 1 al 9"),
    DOUBLE_DIGIT_SUBTRACTION("Resta de 2 dígitos", "Restar números del 10 al 99"),
    SUBTRACTION_WITH_BORROWING("Resta con préstamo", "Resta que requiere prestar"),
    MULTIPLICATION_TABLES("Tablas de multiplicar", "Multiplicación básica 1-10"),
    DIVISION_BASICS("División básica", "División simple sin residuo")
}

enum class MasteryLevel(val displayName: String, val progressColor: Color) {
    NOT_STARTED("No iniciado", Color(0xFFE0E0E0)),
    LEARNING("Aprendiendo", Color(0xFFFFEB3B)),
    PRACTICING("Practicando", Color(0xFFFF9800)),
    MASTERED("Dominado", Color(0xFF4CAF50))
}

// Enhanced Math Problem with Difficulty Tracking
data class MathProblem(
    val id: String = Random.nextInt().toString(),
    val firstNumber: Int,
    val secondNumber: Int,
    val operation: MathOperation,
    val correctAnswer: Int,
    val options: List<Int>,
    val difficulty: DifficultyLevel,
    val concept: MathConcept,
    val hints: List<Hint> = emptyList(),
    val visualAid: VisualAid? = null,
    val explanation: ProblemExplanation? = null
)

data class Hint(
    val level: Int,
    val text: String,
    val type: HintType
)

enum class HintType {
    CONCEPTUAL,
    VISUAL,
    STEP_BY_STEP,
    ENCOURAGEMENT
}

enum class DifficultyLevel(val displayName: String) {
    BEGINNER("Principiante"),
    INTERMEDIATE("Intermedio"),
    ADVANCED("Avanzado"),
    EXPERT("Experto")
}

data class ProblemExplanation(
    val steps: List<ExplanationStep>,
    val visualRepresentation: String,
    val audioScript: String
)

data class ExplanationStep(
    val stepNumber: Int,
    val title: String,
    val description: String,
    val calculation: String,
    val visual: String? = null
)

enum class MathOperation(val symbol: String, val spanishName: String, val category: MathCategory) {
    ADDITION("+", "suma", MathCategory.ADDITION),
    SUBTRACTION("-", "resta", MathCategory.SUBTRACTION),
    MULTIPLICATION("×", "multiplicación", MathCategory.MULTIPLICATION),
    DIVISION("÷", "división", MathCategory.DIVISION)
}

data class VisualAid(
    val type: VisualType,
    val description: String,
    val elements: List<VisualElement>
)

data class VisualElement(
    val content: String,
    val position: Int,
    val isInteractive: Boolean = false
)

enum class VisualType {
    COUNTING_OBJECTS,
    NUMBER_LINE,
    GROUPING,
    DECOMPOSITION,
    VISUAL_EQUATION
}

// User Progress & Performance Tracking
data class UserProfile(
    val id: String,
    val name: String = "Estudiante",
    val avatar: String = "🦙",
    val xpTotal: Int = 0,
    val level: Int = 1,
    val streak: StreakData,
    val badges: List<Badge>,
    val preferences: UserPreferences,
    val progress: LearningProgress
)

data class StreakData(
    val current: Int = 0,
    val longest: Int = 0,
    val lastActivityDate: Long = System.currentTimeMillis()
)

data class Badge(
    val id: String,
    val name: String,
    val description: String,
    val icon: String,
    val unlockedAt: Long,
    val rarity: BadgeRarity
)

enum class BadgeRarity(val displayName: String, val color: Color) {
    COMMON("Común", Color(0xFF9E9E9E)),
    RARE("Raro", Color(0xFF2196F3)),
    EPIC("Épico", Color(0xFF9C27B0)),
    LEGENDARY("Legendario", Color(0xFFFFD700))
}

data class UserPreferences(
    val audioEnabled: Boolean = true,
    val animationsEnabled: Boolean = true,
    val difficulty: DifficultyLevel = DifficultyLevel.BEGINNER,
    val dailyGoal: Int = 50 // XP target per day
)

data class LearningProgress(
    val unitProgress: Map<String, UnitProgress>,
    val masteredConcepts: Set<MathConcept>,
    val weeklyXP: List<Int>, // Last 7 days
    val totalProblemsCompleted: Int = 0,
    val accuracyRate: Float = 0f
)

data class UnitProgress(
    val unitId: String,
    val completedLessons: Set<String>,
    val xpEarned: Int,
    val masteryLevel: MasteryLevel,
    val lastAccessedAt: Long
)

// Performance Analytics for Adaptive Learning
data class PerformanceData(
    val problemId: String,
    val concept: MathConcept,
    val difficulty: DifficultyLevel,
    val isCorrect: Boolean,
    val timeSpent: Long, // milliseconds
    val hintsUsed: Int,
    val attempts: Int,
    val timestamp: Long = System.currentTimeMillis()
)

data class ConceptMastery(
    val concept: MathConcept,
    val totalAttempts: Int,
    val correctAttempts: Int,
    val averageTime: Long,
    val averageHints: Float,
    val lastPracticed: Long,
    val masteryScore: Float // 0.0 to 1.0
) {
    val accuracyRate: Float get() = if (totalAttempts > 0) correctAttempts.toFloat() / totalAttempts else 0f
    val needsReview: Boolean get() = masteryScore < 0.7f || (System.currentTimeMillis() - lastPracticed) > 7 * 24 * 60 * 60 * 1000 // 7 days
}

// Application State
data class AppState(
    val user: UserProfile,
    val learningPath: LearningPath,
    val currentLesson: Lesson? = null,
    val performanceHistory: List<PerformanceData> = emptyList(),
    val conceptMasteries: Map<MathConcept, ConceptMastery> = emptyMap(),
    val recommendedPractice: List<MathConcept> = emptyList()
) 
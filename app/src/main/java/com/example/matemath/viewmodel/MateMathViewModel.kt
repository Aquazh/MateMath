package com.example.matemath.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import com.example.matemath.data.*
import com.example.matemath.TextToSpeechManager

class MateMathViewModel : ViewModel() {
    
    // Core App State
    private val _appState = MutableStateFlow(createInitialAppState())
    val appState: StateFlow<AppState> = _appState.asStateFlow()
    
    // Current Lesson State
    private val _currentLesson = MutableStateFlow<Lesson?>(null)
    val currentLesson: StateFlow<Lesson?> = _currentLesson.asStateFlow()
    
    // Current Problem State
    private val _currentProblem = MutableStateFlow<MathProblem?>(null)
    val currentProblem: StateFlow<MathProblem?> = _currentProblem.asStateFlow()
    
    // Quiz State
    private val _quizState = MutableStateFlow(QuizState())
    val quizState: StateFlow<QuizState> = _quizState.asStateFlow()
    
    // Learning State
    private val _learningState = MutableStateFlow(LearningState())
    val learningState: StateFlow<LearningState> = _learningState.asStateFlow()
    
    // TTS Manager
    private var ttsManager: TextToSpeechManager? = null
    
    data class QuizState(
        val selectedAnswer: Int? = null,
        val showResult: Boolean = false,
        val startTime: Long = 0L,
        val hintsUsed: Int = 0,
        val attempts: Int = 0
    )
    
    data class LearningState(
        val currentStep: Int = 0,
        val showStepByStep: Boolean = false,
        val showVisualAid: Boolean = true
    )
    
    init {
        updateRecommendations()
    }
    
    private fun createInitialAppState(): AppState {
        val learningPath = ContentGenerator.generateLearningPath()
        val userProfile = createDefaultUserProfile()
        
        return AppState(
            user = userProfile,
            learningPath = learningPath,
            currentLesson = null,
            performanceHistory = emptyList(),
            conceptMasteries = emptyMap(),
            recommendedPractice = emptyList()
        )
    }
    
    private fun createDefaultUserProfile(): UserProfile {
        return UserProfile(
            id = "default_user",
            name = "Estudiante",
            avatar = "🦙",
            xpTotal = 0,
            level = 1,
            streak = StreakData(),
            badges = emptyList(),
            preferences = UserPreferences(),
            progress = LearningProgress(
                unitProgress = emptyMap(),
                masteredConcepts = emptySet(),
                weeklyXP = listOf(0, 0, 0, 0, 0, 0, 0),
                totalProblemsCompleted = 0,
                accuracyRate = 0f
            )
        )
    }
    
    // TTS Management
    fun setTtsManager(ttsManager: TextToSpeechManager) {
        this.ttsManager = ttsManager
    }
    
    // Learning Path Navigation
    fun startLesson(lesson: Lesson) {
        _currentLesson.value = lesson
        val firstProblem = lesson.problems.firstOrNull()
        _currentProblem.value = firstProblem
        _quizState.value = QuizState(startTime = System.currentTimeMillis())
        _learningState.value = LearningState()
        
        // Speak lesson introduction
        ttsManager?.speak("Comenzamos la lección: ${lesson.title}")
        
        // Speak first problem
        firstProblem?.let { speakProblem(it) }
    }
    
    fun startUnit(unit: LearningUnit) {
        val firstLesson = unit.lessons.firstOrNull { !it.isCompleted }
        firstLesson?.let { startLesson(it) }
    }
    
    fun nextProblem() {
        val currentLesson = _currentLesson.value ?: return
        val currentProblem = _currentProblem.value ?: return
        val currentIndex = currentLesson.problems.indexOf(currentProblem)
        
        if (currentIndex < currentLesson.problems.size - 1) {
            // Next problem in same lesson
            val nextProblem = currentLesson.problems[currentIndex + 1]
            _currentProblem.value = nextProblem
            _quizState.value = QuizState(startTime = System.currentTimeMillis())
            _learningState.value = LearningState()
            speakProblem(nextProblem)
        } else {
            // Lesson completed
            completeLessonAndFindNext()
        }
    }
    
    private fun completeLessonAndFindNext() {
        val currentLesson = _currentLesson.value ?: return
        
        // Mark lesson as completed
        val updatedLesson = currentLesson.copy(
            isCompleted = true,
            masteryLevel = calculateLessonMastery(currentLesson)
        )
        
        // Update app state
        updateLessonProgress(updatedLesson)
        
        // Find next lesson
        val nextLesson = AdaptiveLearningEngine.recommendNextLesson(
            _appState.value.learningPath,
            _appState.value.user.progress
        )
        
        if (nextLesson != null) {
            ttsManager?.speak("¡Lección completada! Pasemos a la siguiente.")
            startLesson(nextLesson)
        } else {
            // All lessons completed
            _currentLesson.value = null
            _currentProblem.value = null
            ttsManager?.speak("¡Felicidades! Has completado todas las lecciones disponibles.")
        }
    }
    
    private fun calculateLessonMastery(lesson: Lesson): MasteryLevel {
        val currentState = _appState.value
        val conceptMastery = currentState.conceptMasteries[lesson.concept]
        
        return when {
            conceptMastery == null -> MasteryLevel.LEARNING
            conceptMastery.masteryScore >= 0.9f -> MasteryLevel.MASTERED
            conceptMastery.masteryScore >= 0.7f -> MasteryLevel.PRACTICING
            else -> MasteryLevel.LEARNING
        }
    }
    
    // Problem Interaction
    fun onAnswerSelected(selectedAnswer: Int) {
        val currentProblem = _currentProblem.value ?: return
        val currentState = _quizState.value
        
        val isCorrect = selectedAnswer == currentProblem.correctAnswer
        val timeSpent = System.currentTimeMillis() - currentState.startTime
        
        // Update quiz state
        _quizState.value = currentState.copy(
            selectedAnswer = selectedAnswer,
            showResult = true,
            attempts = currentState.attempts + 1
        )
        
        // Record performance
        val performanceData = PerformanceData(
            problemId = currentProblem.id,
            concept = currentProblem.concept,
            difficulty = currentProblem.difficulty,
            isCorrect = isCorrect,
            timeSpent = timeSpent,
            hintsUsed = currentState.hintsUsed,
            attempts = currentState.attempts + 1
        )
        
        recordPerformance(performanceData)
        
        // Calculate XP
        val xpEarned = AdaptiveLearningEngine.calculateXPForPerformance(performanceData)
        updateUserXP(xpEarned)
        
        // Provide feedback
        speakFeedback(isCorrect, currentProblem)
        
        // Check for new badges
        checkAndAwardBadges()
    }
    
    private fun recordPerformance(performanceData: PerformanceData) {
        val currentState = _appState.value
        val updatedHistory = currentState.performanceHistory + performanceData
        
        // Update concept masteries
        val updatedMasteries = AdaptiveLearningEngine.analyzePerformance(updatedHistory)
        
        // Update user progress
        val currentLesson = _currentLesson.value
        val updatedProgress = if (currentLesson != null) {
            AdaptiveLearningEngine.updateUserProgress(
                currentState.user.progress,
                currentLesson.id,
                AdaptiveLearningEngine.calculateXPForPerformance(performanceData),
                performanceData
            )
        } else {
            currentState.user.progress
        }
        
        _appState.value = currentState.copy(
            performanceHistory = updatedHistory,
            conceptMasteries = updatedMasteries,
            user = currentState.user.copy(progress = updatedProgress)
        )
        
        updateRecommendations()
    }
    
    private fun updateUserXP(xpEarned: Int) {
        val currentState = _appState.value
        val currentUser = currentState.user
        val newXPTotal = currentUser.xpTotal + xpEarned
        val newLevel = calculateLevel(newXPTotal)
        
        // Update weekly XP (assuming today is the last day in the array)
        val updatedWeeklyXP = currentUser.progress.weeklyXP.toMutableList()
        if (updatedWeeklyXP.isNotEmpty()) {
            updatedWeeklyXP[updatedWeeklyXP.size - 1] += xpEarned
        }
        
        // Update streak
        val updatedStreak = AdaptiveLearningEngine.updateStreakData(
            currentUser.streak,
            updatedWeeklyXP.lastOrNull() ?: 0,
            currentUser.preferences.dailyGoal
        )
        
        val updatedUser = currentUser.copy(
            xpTotal = newXPTotal,
            level = newLevel,
            streak = updatedStreak,
            progress = currentUser.progress.copy(weeklyXP = updatedWeeklyXP)
        )
        
        _appState.value = currentState.copy(user = updatedUser)
    }
    
    private fun calculateLevel(xp: Int): Int {
        return (xp / 100) + 1 // Simple level calculation: 100 XP per level
    }
    
    private fun checkAndAwardBadges() {
        val currentState = _appState.value
        val newBadges = AdaptiveLearningEngine.checkBadgeEligibility(
            currentState.user,
            currentState.performanceHistory
        )
        
        if (newBadges.isNotEmpty()) {
            val updatedUser = currentState.user.copy(
                badges = currentState.user.badges + newBadges
            )
            _appState.value = currentState.copy(user = updatedUser)
            
            // Announce new badges
            newBadges.forEach { badge ->
                ttsManager?.speak("¡Nuevo logro desbloqueado: ${badge.name}!")
            }
        }
    }
    
    // Hint System
    fun showHint() {
        val currentProblem = _currentProblem.value ?: return
        val currentState = _quizState.value
        
        if (currentState.hintsUsed < currentProblem.hints.size) {
            val hint = currentProblem.hints[currentState.hintsUsed]
            _quizState.value = currentState.copy(hintsUsed = currentState.hintsUsed + 1)
            ttsManager?.speak(hint.text)
        }
    }
    
    // Step-by-Step Learning
    fun toggleStepByStep() {
        val currentState = _learningState.value
        _learningState.value = currentState.copy(showStepByStep = !currentState.showStepByStep)
    }
    
    fun nextStep() {
        val currentProblem = _currentProblem.value ?: return
        val currentState = _learningState.value
        
        if (currentProblem.explanation != null && currentState.currentStep < currentProblem.explanation.steps.size - 1) {
            val nextStep = currentState.currentStep + 1
            _learningState.value = currentState.copy(currentStep = nextStep)
            
            val step = currentProblem.explanation.steps[nextStep]
            ttsManager?.speak("${step.title}. ${step.description}")
        }
    }
    
    fun previousStep() {
        val currentState = _learningState.value
        if (currentState.currentStep > 0) {
            val prevStep = currentState.currentStep - 1
            _learningState.value = currentState.copy(currentStep = prevStep)
            
            val currentProblem = _currentProblem.value
            currentProblem?.explanation?.steps?.get(prevStep)?.let { step ->
                ttsManager?.speak("${step.title}. ${step.description}")
            }
        }
    }
    
    // Practice Mode
    fun startPracticeSession(concepts: List<MathConcept> = emptyList()) {
        val practiceTopics = if (concepts.isNotEmpty()) {
            concepts
        } else {
            _appState.value.recommendedPractice.take(3)
        }
        
        if (practiceTopics.isNotEmpty()) {
            val randomConcept = practiceTopics.random()
            val difficulty = getDifficultyForConcept(randomConcept)
            val problem = ContentGenerator.generateProblemForConcept(randomConcept, difficulty)
            
            _currentProblem.value = problem
            _quizState.value = QuizState(startTime = System.currentTimeMillis())
            _learningState.value = LearningState()
            
            ttsManager?.speak("Vamos a practicar ${randomConcept.displayName}")
            speakProblem(problem)
        }
    }
    
    private fun getDifficultyForConcept(concept: MathConcept): DifficultyLevel {
        val mastery = _appState.value.conceptMasteries[concept]
        return if (mastery != null) {
            AdaptiveLearningEngine.adjustDifficulty(concept, mastery)
        } else {
            DifficultyLevel.BEGINNER
        }
    }
    
    // Audio Management
    private fun speakProblem(problem: MathProblem) {
        val operationWord = when (problem.operation) {
            MathOperation.ADDITION -> "más"
            MathOperation.SUBTRACTION -> "menos"
            MathOperation.MULTIPLICATION -> "por"
            MathOperation.DIVISION -> "dividido entre"
        }
        
        val questionText = "¿Cuánto es ${problem.firstNumber} $operationWord ${problem.secondNumber}?"
        ttsManager?.speak(questionText)
    }
    
    private fun speakFeedback(isCorrect: Boolean, problem: MathProblem) {
        val messages = if (isCorrect) {
            listOf(
                "¡Excelente! Muy bien hecho.",
                "¡Perfecto! Eres muy bueno en ${problem.operation.spanishName}.",
                "¡Correcto! Sigue así.",
                "¡Genial! Lo hiciste muy bien."
            )
        } else {
            listOf(
                "No te preocupes. ¡Sigamos intentando!",
                "Está bien equivocarse. Así aprendemos.",
                "¡Casi! Vamos a intentar de nuevo.",
                "No pasa nada. ¿Quieres una pista?"
            )
        }
        
        ttsManager?.speak(messages.random())
    }
    
    fun repeatQuestion() {
        val currentProblem = _currentProblem.value
        currentProblem?.let { speakProblem(it) }
    }
    
    // Recommendations Update
    private fun updateRecommendations() {
        val currentState = _appState.value
        val recommendations = AdaptiveLearningEngine.recommendPracticeTopics(
            currentState.conceptMasteries,
            maxRecommendations = 3
        )
        
        _appState.value = currentState.copy(recommendedPractice = recommendations)
    }
    
    // Unit Progress Update
    private fun updateLessonProgress(lesson: Lesson) {
        val currentState = _appState.value
        val unitId = lesson.id.substringBefore("_lesson")
        
        // Update learning path
        val updatedUnits = currentState.learningPath.units.map { unit ->
            if (unit.id == unitId) {
                val updatedLessons = unit.lessons.map { l ->
                    if (l.id == lesson.id) lesson else l
                }
                val completedCount = updatedLessons.count { it.isCompleted }
                val completionPercentage = completedCount.toFloat() / updatedLessons.size
                
                unit.copy(
                    lessons = updatedLessons,
                    completionPercentage = completionPercentage
                )
            } else {
                unit
            }
        }
        
        val updatedLearningPath = currentState.learningPath.copy(units = updatedUnits)
        
        // Check and unlock new units
        val finalLearningPath = AdaptiveLearningEngine.checkAndUnlockUnits(
            updatedLearningPath,
            currentState.user.progress
        )
        
        _appState.value = currentState.copy(learningPath = finalLearningPath)
    }
    
    override fun onCleared() {
        super.onCleared()
        ttsManager?.shutdown()
    }
} 
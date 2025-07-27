package com.example.matemath.data

import kotlin.math.*

object AdaptiveLearningEngine {
    
    // Performance Analysis
    fun analyzePerformance(performanceHistory: List<PerformanceData>): Map<MathConcept, ConceptMastery> {
        return performanceHistory
            .groupBy { it.concept }
            .mapValues { (concept, performances) ->
                calculateConceptMastery(concept, performances)
            }
    }
    
    private fun calculateConceptMastery(concept: MathConcept, performances: List<PerformanceData>): ConceptMastery {
        val totalAttempts = performances.size
        val correctAttempts = performances.count { it.isCorrect }
        val averageTime = performances.map { it.timeSpent }.average().toLong()
        val averageHints = performances.map { it.hintsUsed }.average().toFloat()
        val lastPracticed = performances.maxOfOrNull { it.timestamp } ?: 0L
        
        // Calculate mastery score based on multiple factors
        val accuracyScore = if (totalAttempts > 0) correctAttempts.toFloat() / totalAttempts else 0f
        val speedScore = calculateSpeedScore(averageTime, concept)
        val hintScore = calculateHintScore(averageHints)
        val consistencyScore = calculateConsistencyScore(performances)
        
        val masteryScore = (accuracyScore * 0.4f + speedScore * 0.2f + hintScore * 0.2f + consistencyScore * 0.2f)
            .coerceIn(0f, 1f)
        
        return ConceptMastery(
            concept = concept,
            totalAttempts = totalAttempts,
            correctAttempts = correctAttempts,
            averageTime = averageTime,
            averageHints = averageHints,
            lastPracticed = lastPracticed,
            masteryScore = masteryScore
        )
    }
    
    private fun calculateSpeedScore(averageTime: Long, concept: MathConcept): Float {
        val idealTime = getIdealTimeForConcept(concept)
        return (idealTime.toFloat() / maxOf(averageTime, idealTime)).coerceIn(0f, 1f)
    }
    
    private fun calculateHintScore(averageHints: Float): Float {
        return (1f - (averageHints / 4f)).coerceIn(0f, 1f) // Assuming max 4 hints
    }
    
    private fun calculateConsistencyScore(performances: List<PerformanceData>): Float {
        if (performances.size < 3) return 0.5f
        
        val recentPerformances = performances.takeLast(5)
        val accuracies = recentPerformances.map { if (it.isCorrect) 1f else 0f }
        val mean = accuracies.average().toFloat()
        val variance = accuracies.map { (it - mean).pow(2) }.average().toFloat()
        val standardDeviation = sqrt(variance)
        
        return (1f - standardDeviation).coerceIn(0f, 1f)
    }
    
    private fun getIdealTimeForConcept(concept: MathConcept): Long {
        return when (concept) {
            MathConcept.SINGLE_DIGIT_ADDITION -> 5000L // 5 seconds
            MathConcept.DOUBLE_DIGIT_ADDITION -> 8000L
            MathConcept.SINGLE_DIGIT_SUBTRACTION -> 6000L
            MathConcept.DOUBLE_DIGIT_SUBTRACTION -> 10000L
            MathConcept.MULTIPLICATION_TABLES -> 4000L
            MathConcept.DIVISION_BASICS -> 8000L
            else -> 7000L
        }
    }
    
    // Difficulty Adjustment
    fun adjustDifficulty(concept: MathConcept, mastery: ConceptMastery): DifficultyLevel {
        return when {
            mastery.masteryScore >= 0.9f && mastery.averageTime <= getIdealTimeForConcept(concept) -> 
                DifficultyLevel.EXPERT
            mastery.masteryScore >= 0.8f -> 
                DifficultyLevel.ADVANCED
            mastery.masteryScore >= 0.6f -> 
                DifficultyLevel.INTERMEDIATE
            else -> 
                DifficultyLevel.BEGINNER
        }
    }
    
    // Recommendation Engine
    fun recommendPracticeTopics(
        conceptMasteries: Map<MathConcept, ConceptMastery>,
        maxRecommendations: Int = 3
    ): List<MathConcept> {
        val now = System.currentTimeMillis()
        val oneWeek = 7 * 24 * 60 * 60 * 1000L
        
        return conceptMasteries.values
            .filter { mastery ->
                // Recommend if: low mastery OR not practiced recently
                mastery.masteryScore < 0.8f || (now - mastery.lastPracticed) > oneWeek
            }
            .sortedWith(compareBy<ConceptMastery> { it.masteryScore }
                .thenByDescending { now - it.lastPracticed })
            .take(maxRecommendations)
            .map { it.concept }
    }
    
    fun recommendNextLesson(
        learningPath: LearningPath,
        userProgress: LearningProgress
    ): Lesson? {
        return learningPath.units
            .filter { it.isUnlocked }
            .flatMap { it.lessons }
            .firstOrNull { lesson ->
                !lesson.isCompleted && 
                !(userProgress.unitProgress[lesson.id.substringBefore("_lesson")]
                    ?.completedLessons?.contains(lesson.id) ?: false)
            }
    }
    
    // Progress Tracking
    fun updateUserProgress(
        currentProgress: LearningProgress,
        lessonId: String,
        xpEarned: Int,
        performanceData: PerformanceData
    ): LearningProgress {
        val unitId = lessonId.substringBefore("_lesson")
        val currentUnitProgress = currentProgress.unitProgress[unitId] ?: UnitProgress(
            unitId = unitId,
            completedLessons = emptySet(),
            xpEarned = 0,
            masteryLevel = MasteryLevel.NOT_STARTED,
            lastAccessedAt = System.currentTimeMillis()
        )
        
        val updatedUnitProgress = currentUnitProgress.copy(
            completedLessons = currentUnitProgress.completedLessons + lessonId,
            xpEarned = currentUnitProgress.xpEarned + xpEarned,
            lastAccessedAt = System.currentTimeMillis()
        )
        
        val newMasteredConcepts = if (performanceData.isCorrect) {
            currentProgress.masteredConcepts + performanceData.concept
        } else {
            currentProgress.masteredConcepts
        }
        
        return currentProgress.copy(
            unitProgress = currentProgress.unitProgress + (unitId to updatedUnitProgress),
            masteredConcepts = newMasteredConcepts,
            totalProblemsCompleted = currentProgress.totalProblemsCompleted + 1,
            accuracyRate = calculateOverallAccuracy(currentProgress, performanceData)
        )
    }
    
    private fun calculateOverallAccuracy(
        currentProgress: LearningProgress,
        newPerformance: PerformanceData
    ): Float {
        val totalProblems = currentProgress.totalProblemsCompleted + 1
        val previousCorrect = (currentProgress.accuracyRate * currentProgress.totalProblemsCompleted).toInt()
        val newCorrect = previousCorrect + if (newPerformance.isCorrect) 1 else 0
        
        return if (totalProblems > 0) newCorrect.toFloat() / totalProblems else 0f
    }
    
    // Unit Unlocking Logic
    fun checkAndUnlockUnits(
        learningPath: LearningPath,
        userProgress: LearningProgress
    ): LearningPath {
        val updatedUnits = learningPath.units.mapIndexed { index, unit ->
            val shouldUnlock = when {
                index == 0 -> true // First unit always unlocked
                index <= learningPath.units.size - 1 -> {
                    // Unlock if previous unit has 70% completion
                    val previousUnit = learningPath.units[index - 1]
                    val previousProgress = userProgress.unitProgress[previousUnit.id]
                    val completionRate = previousProgress?.let {
                        it.completedLessons.size.toFloat() / previousUnit.lessons.size
                    } ?: 0f
                    completionRate >= 0.7f
                }
                else -> false
            }
            
            unit.copy(isUnlocked = unit.isUnlocked || shouldUnlock)
        }
        
        return learningPath.copy(units = updatedUnits)
    }
    
    // Badge System
    fun checkBadgeEligibility(
        userProfile: UserProfile,
        performanceHistory: List<PerformanceData>
    ): List<Badge> {
        val newBadges = mutableListOf<Badge>()
        val currentTime = System.currentTimeMillis()
        
        // Check for various achievement badges
        checkStreakBadges(userProfile, newBadges, currentTime)
        checkAccuracyBadges(performanceHistory, newBadges, currentTime)
        checkSpeedBadges(performanceHistory, newBadges, currentTime)
        checkPersistenceBadges(performanceHistory, newBadges, currentTime)
        
        return newBadges.filter { badge ->
            !userProfile.badges.any { it.id == badge.id }
        }
    }
    
    private fun checkStreakBadges(userProfile: UserProfile, badges: MutableList<Badge>, currentTime: Long) {
        val streakDays = userProfile.streak.current
        
        when (streakDays) {
            3 -> badges.add(Badge("streak_3", "¡Primera Racha!", "3 días seguidos", "🔥", currentTime, BadgeRarity.COMMON))
            7 -> badges.add(Badge("streak_7", "¡Una Semana!", "7 días seguidos", "📅", currentTime, BadgeRarity.RARE))
            14 -> badges.add(Badge("streak_14", "¡Dos Semanas!", "14 días seguidos", "⭐", currentTime, BadgeRarity.EPIC))
            30 -> badges.add(Badge("streak_30", "¡Campeón!", "30 días seguidos", "👑", currentTime, BadgeRarity.LEGENDARY))
        }
    }
    
    private fun checkAccuracyBadges(performanceHistory: List<PerformanceData>, badges: MutableList<Badge>, currentTime: Long) {
        val recentPerformances = performanceHistory.takeLast(10)
        if (recentPerformances.size >= 10) {
            val accuracy = recentPerformances.count { it.isCorrect }.toFloat() / recentPerformances.size
            
            when {
                accuracy >= 1.0f -> badges.add(Badge("perfect_10", "¡Perfecto!", "10 respuestas perfectas", "💯", currentTime, BadgeRarity.EPIC))
                accuracy >= 0.9f -> badges.add(Badge("accuracy_90", "¡Casi Perfecto!", "90% de precisión", "🎯", currentTime, BadgeRarity.RARE))
            }
        }
    }
    
    private fun checkSpeedBadges(performanceHistory: List<PerformanceData>, badges: MutableList<Badge>, currentTime: Long) {
        val fastAnswers = performanceHistory.count { it.timeSpent < 3000L && it.isCorrect }
        
        when (fastAnswers) {
            5 -> badges.add(Badge("speed_5", "¡Rápido!", "5 respuestas rápidas", "⚡", currentTime, BadgeRarity.COMMON))
            20 -> badges.add(Badge("speed_20", "¡Súper Rápido!", "20 respuestas rápidas", "🚀", currentTime, BadgeRarity.RARE))
        }
    }
    
    private fun checkPersistenceBadges(performanceHistory: List<PerformanceData>, badges: MutableList<Badge>, currentTime: Long) {
        val totalProblems = performanceHistory.size
        
        when (totalProblems) {
            50 -> badges.add(Badge("problems_50", "¡Persistente!", "50 problemas resueltos", "💪", currentTime, BadgeRarity.COMMON))
            100 -> badges.add(Badge("problems_100", "¡Dedicado!", "100 problemas resueltos", "🏆", currentTime, BadgeRarity.RARE))
            500 -> badges.add(Badge("problems_500", "¡Maestro!", "500 problemas resueltos", "🎓", currentTime, BadgeRarity.LEGENDARY))
        }
    }
    
    // Daily Goals and Streaks
    fun updateStreakData(
        currentStreak: StreakData,
        xpEarnedToday: Int,
        dailyGoal: Int
    ): StreakData {
        val now = System.currentTimeMillis()
        val lastActivityDate = currentStreak.lastActivityDate
        val daysDifference = (now - lastActivityDate) / (24 * 60 * 60 * 1000)
        
        return when {
            xpEarnedToday >= dailyGoal -> {
                // Goal achieved today
                val newCurrent = if (daysDifference <= 1) currentStreak.current + 1 else 1
                currentStreak.copy(
                    current = newCurrent,
                    longest = maxOf(currentStreak.longest, newCurrent),
                    lastActivityDate = now
                )
            }
            daysDifference > 1 -> {
                // Streak broken
                currentStreak.copy(
                    current = 0,
                    lastActivityDate = now
                )
            }
            else -> {
                // Same day, no change to streak
                currentStreak.copy(lastActivityDate = now)
            }
        }
    }
    
    fun calculateXPForPerformance(
        performanceData: PerformanceData,
        baseXP: Int = 10
    ): Int {
        var xp = baseXP
        
        // Bonus for correct answer
        if (performanceData.isCorrect) {
            xp *= 2
        }
        
        // Bonus for speed
        val idealTime = getIdealTimeForConcept(performanceData.concept)
        if (performanceData.timeSpent <= idealTime) {
            xp = (xp * 1.5f).toInt()
        }
        
        // Penalty for hints
        val hintPenalty = performanceData.hintsUsed * 0.1f
        xp = (xp * (1f - hintPenalty)).toInt()
        
        // Bonus for difficulty
        val difficultyMultiplier = when (performanceData.difficulty) {
            DifficultyLevel.BEGINNER -> 1f
            DifficultyLevel.INTERMEDIATE -> 1.2f
            DifficultyLevel.ADVANCED -> 1.5f
            DifficultyLevel.EXPERT -> 2f
        }
        
        return (xp * difficultyMultiplier).toInt().coerceAtLeast(1)
    }
} 
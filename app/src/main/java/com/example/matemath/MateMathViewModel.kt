package com.example.matemath

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MateMathViewModel : ViewModel() {
    
    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()
    
    private var ttsManager: TextToSpeechManager? = null
    
    fun setTtsManager(ttsManager: TextToSpeechManager) {
        this.ttsManager = ttsManager
    }
    
    fun startNewGame(isLearningMode: Boolean = false) {
        val newProblem = MathProblemGenerator.generateProblem(
            includeTeachingElements = isLearningMode
        )
        _gameState.value = GameState().copy(
            currentProblem = newProblem,
            isLearningMode = isLearningMode
        )
        // Automatically read the first problem
        ttsManager?.speakMathProblem(newProblem)
    }
    
    fun startLearningMode(operation: MathOperation? = null) {
        val newProblem = MathProblemGenerator.generateProblem(
            operation = operation,
            includeTeachingElements = true
        )
        _gameState.value = GameState().copy(
            currentProblem = newProblem,
            isLearningMode = true,
            currentStep = 0
        )
        
        // Speak the introduction to learning mode
        val operationName = newProblem.operation.spanishName
        ttsManager?.speak("Vamos a aprender sobre la $operationName. Te voy a enseñar paso a paso.")
        
        // Then speak the problem
        ttsManager?.speakMathProblem(newProblem)
    }
    
    fun nextStep() {
        val currentState = _gameState.value
        val problem = currentState.currentProblem ?: return
        
        if (currentState.currentStep < problem.steps.size - 1) {
            val nextStep = currentState.currentStep + 1
            _gameState.value = currentState.copy(currentStep = nextStep)
            
            // Speak the current step explanation
            val step = problem.steps[nextStep]
            ttsManager?.speak(step.description + ". " + step.explanation)
        }
    }
    
    fun previousStep() {
        val currentState = _gameState.value
        if (currentState.currentStep > 0) {
            val prevStep = currentState.currentStep - 1
            _gameState.value = currentState.copy(currentStep = prevStep)
            
            // Speak the current step explanation
            val problem = currentState.currentProblem
            problem?.steps?.get(prevStep)?.let { step ->
                ttsManager?.speak(step.description + ". " + step.explanation)
            }
        }
    }
    
    fun showHint() {
        val currentState = _gameState.value
        val problem = currentState.currentProblem ?: return
        val hintsUsed = currentState.hintsUsed
        
        if (hintsUsed < problem.hints.size) {
            val hint = problem.hints[hintsUsed]
            _gameState.value = currentState.copy(hintsUsed = hintsUsed + 1)
            
            // Speak the hint
            ttsManager?.speak(hint)
        }
    }
    
    fun onAnswerSelected(selectedAnswer: Int) {
        val currentState = _gameState.value
        val problem = currentState.currentProblem ?: return
        
        val isCorrect = selectedAnswer == problem.correctAnswer
        
        if (isCorrect) {
            val newConceptsLearned = if (currentState.isLearningMode) {
                currentState.conceptsLearned + problem.operation
            } else {
                currentState.conceptsLearned
            }
            
            _gameState.value = currentState.copy(
                score = currentState.score + if (currentState.isLearningMode) 15 else 10, // Bonus for learning mode
                yerbaCoins = currentState.yerbaCoins + 1,
                conceptsLearned = newConceptsLearned
            )
        }
        
        // Speak encouragement with educational context
        if (currentState.isLearningMode) {
            speakEducationalFeedback(isCorrect, problem)
        } else {
            ttsManager?.speakEncouragement(isCorrect)
        }
    }
    
    private fun speakEducationalFeedback(isCorrect: Boolean, problem: MathProblem) {
        if (isCorrect) {
            val messages = listOf(
                "¡Excelente! Entendiste bien cómo funciona la ${problem.operation.spanishName}.",
                "¡Perfecto! Ya sabes resolver problemas de ${problem.operation.spanishName}.",
                "¡Muy bien! Aplicaste correctamente lo que aprendiste."
            )
            ttsManager?.speak(messages.random())
        } else {
            val messages = listOf(
                "No te preocupes. Vamos a repasar los pasos juntos.",
                "Está bien equivocarse. Así aprendemos. ¿Quieres una pista?",
                "Intentemos de nuevo. Recuerda lo que acabamos de aprender."
            )
            ttsManager?.speak(messages.random())
        }
    }
    
    fun generateNextProblem() {
        val currentState = _gameState.value
        val newProblem = MathProblemGenerator.generateProblem(
            includeTeachingElements = currentState.isLearningMode
        )
        _gameState.value = currentState.copy(
            currentProblem = newProblem,
            problemCount = currentState.problemCount + 1,
            currentStep = 0,
            hintsUsed = 0
        )
        // Automatically read the new problem
        ttsManager?.speakMathProblem(newProblem)
    }
    
    fun generateNextProblemOfType(operation: MathOperation) {
        val currentState = _gameState.value
        val newProblem = MathProblemGenerator.generateProblem(
            operation = operation,
            includeTeachingElements = currentState.isLearningMode
        )
        _gameState.value = currentState.copy(
            currentProblem = newProblem,
            problemCount = currentState.problemCount + 1,
            currentStep = 0,
            hintsUsed = 0
        )
        ttsManager?.speakMathProblem(newProblem)
    }
    
    fun repeatQuestion() {
        val currentProblem = _gameState.value.currentProblem
        currentProblem?.let { 
            ttsManager?.speakMathProblem(it)
        }
    }
    
    fun repeatCurrentStep() {
        val currentState = _gameState.value
        val problem = currentState.currentProblem ?: return
        
        if (problem.steps.isNotEmpty() && currentState.currentStep < problem.steps.size) {
            val step = problem.steps[currentState.currentStep]
            ttsManager?.speak(step.description + ". " + step.explanation)
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        ttsManager?.shutdown()
    }
} 
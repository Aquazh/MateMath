package com.example.matemath

import kotlin.random.Random

data class MathProblem(
    val firstNumber: Int,
    val secondNumber: Int,
    val operation: MathOperation,
    val correctAnswer: Int,
    val options: List<Int>,
    val steps: List<SolutionStep> = emptyList(),
    val visualAid: VisualAid? = null,
    val hints: List<String> = emptyList()
) {
    val question: String
        get() = "$firstNumber ${operation.symbol} $secondNumber"
}

data class SolutionStep(
    val description: String,
    val calculation: String,
    val explanation: String,
    val visualRepresentation: String? = null
)

data class VisualAid(
    val type: VisualType,
    val description: String,
    val interactiveElements: List<String> = emptyList()
)

enum class VisualType {
    NUMBER_LINE,
    COUNTING_OBJECTS,
    GROUPING,
    DECOMPOSITION
}

enum class MathOperation(val symbol: String, val spanishName: String) {
    ADDITION("+", "suma"),
    SUBTRACTION("-", "resta"),
    MULTIPLICATION("×", "multiplicación"),
    DIVISION("÷", "división")
}

data class GameState(
    val score: Int = 0,
    val yerbaCoins: Int = 0,
    val currentProblem: MathProblem? = null,
    val problemCount: Int = 0,
    val isLearningMode: Boolean = false,
    val currentStep: Int = 0,
    val hintsUsed: Int = 0,
    val conceptsLearned: Set<MathOperation> = emptySet()
)

object MathProblemGenerator {
    fun generateProblem(
        maxNumber: Int = 10,
        operation: MathOperation? = null,
        includeTeachingElements: Boolean = false
    ): MathProblem {
        val selectedOperation = operation ?: if (Random.nextBoolean()) MathOperation.ADDITION else MathOperation.SUBTRACTION
        
        val (first, second) = when (selectedOperation) {
            MathOperation.ADDITION -> {
                val a = Random.nextInt(1, maxNumber + 1)
                val b = Random.nextInt(1, maxNumber + 1)
                a to b
            }
            MathOperation.SUBTRACTION -> {
                val a = Random.nextInt(5, maxNumber + 1) // Ensure positive result
                val b = Random.nextInt(1, a)
                a to b
            }
            MathOperation.MULTIPLICATION -> {
                val a = Random.nextInt(1, 6) // Keep multiplication small for beginners
                val b = Random.nextInt(1, 6)
                a to b
            }
            MathOperation.DIVISION -> {
                val b = Random.nextInt(2, 6)
                val result = Random.nextInt(1, 6)
                val a = b * result // Ensure clean division
                a to b
            }
        }
        
        val correct = when (selectedOperation) {
            MathOperation.ADDITION -> first + second
            MathOperation.SUBTRACTION -> first - second
            MathOperation.MULTIPLICATION -> first * second
            MathOperation.DIVISION -> first / second
        }
        
        // Generate 3 wrong answers close to the correct one
        val wrongAnswers = mutableSetOf<Int>()
        while (wrongAnswers.size < 3) {
            val wrongAnswer = correct + Random.nextInt(-3, 4)
            if (wrongAnswer != correct && wrongAnswer >= 0) {
                wrongAnswers.add(wrongAnswer)
            }
        }
        
        val options = (wrongAnswers + correct).shuffled()
        
        val steps = if (includeTeachingElements) generateSolutionSteps(first, second, selectedOperation, correct) else emptyList()
        val visualAid = if (includeTeachingElements) generateVisualAid(first, second, selectedOperation) else null
        val hints = if (includeTeachingElements) generateHints(first, second, selectedOperation) else emptyList()
        
        return MathProblem(first, second, selectedOperation, correct, options, steps, visualAid, hints)
    }
    
    private fun generateSolutionSteps(first: Int, second: Int, operation: MathOperation, answer: Int): List<SolutionStep> {
        return when (operation) {
            MathOperation.ADDITION -> listOf(
                SolutionStep(
                    description = "Paso 1: Identificamos los números",
                    calculation = "$first + $second",
                    explanation = "Tenemos $first y queremos agregar $second más",
                    visualRepresentation = "🟡".repeat(first) + " + " + "🟢".repeat(second)
                ),
                SolutionStep(
                    description = "Paso 2: Contamos desde el primer número",
                    calculation = "Empezamos en $first",
                    explanation = "Comenzamos contando desde $first: ${(first + 1..first + second).joinToString(", ")}",
                    visualRepresentation = "$first → ${first + 1} → ${first + 2} → ... → $answer"
                ),
                SolutionStep(
                    description = "Paso 3: Resultado final",
                    calculation = "$first + $second = $answer",
                    explanation = "Al sumar todos los elementos, obtenemos $answer",
                    visualRepresentation = "🟡".repeat(first) + "🟢".repeat(second) + " = $answer total"
                )
            )
            
            MathOperation.SUBTRACTION -> listOf(
                SolutionStep(
                    description = "Paso 1: Identificamos la operación",
                    calculation = "$first - $second",
                    explanation = "Tenemos $first y queremos quitar $second",
                    visualRepresentation = "🟡".repeat(first) + " (quitamos " + "❌".repeat(second) + ")"
                ),
                SolutionStep(
                    description = "Paso 2: Contamos hacia atrás",
                    calculation = "Desde $first, contamos $second hacia atrás",
                    explanation = "Comenzamos en $first: ${(first - 1 downTo answer).joinToString(", ")}",
                    visualRepresentation = "$first → ${first - 1} → ${first - 2} → ... → $answer"
                ),
                SolutionStep(
                    description = "Paso 3: Resultado final",
                    calculation = "$first - $second = $answer",
                    explanation = "Después de quitar $second elementos, nos quedan $answer",
                    visualRepresentation = "🟡".repeat(answer) + " (quedan $answer)"
                )
            )
            
            else -> emptyList() // Will implement multiplication/division later
        }
    }
    
    private fun generateVisualAid(first: Int, second: Int, operation: MathOperation): VisualAid? {
        return when (operation) {
            MathOperation.ADDITION -> VisualAid(
                type = VisualType.COUNTING_OBJECTS,
                description = "Cuenta los objetos para resolver $first + $second",
                interactiveElements = listOf(
                    "🟡".repeat(first),
                    "🟢".repeat(second),
                    "Total: ${first + second}"
                )
            )
            
            MathOperation.SUBTRACTION -> VisualAid(
                type = VisualType.COUNTING_OBJECTS,
                description = "Cuenta los objetos restantes después de quitar",
                interactiveElements = listOf(
                    "Inicial: " + "🟡".repeat(first),
                    "Quitar: " + "❌".repeat(second),
                    "Quedan: " + "🟡".repeat(first - second)
                )
            )
            
            else -> null
        }
    }
    
    private fun generateHints(first: Int, second: Int, operation: MathOperation): List<String> {
        return when (operation) {
            MathOperation.ADDITION -> listOf(
                "💡 Pista 1: Puedes usar tus dedos para contar",
                "💡 Pista 2: Empieza desde $first y cuenta $second números más",
                "💡 Pista 3: $first + $second significa 'agregar $second a $first'",
                "💡 Pista 4: La respuesta será mayor que $first"
            )
            
            MathOperation.SUBTRACTION -> listOf(
                "💡 Pista 1: Restar significa 'quitar' o 'sacar'",
                "💡 Pista 2: Empieza desde $first y cuenta $second números hacia atrás",
                "💡 Pista 3: $first - $second significa 'quitar $second de $first'",
                "💡 Pista 4: La respuesta será menor que $first"
            )
            
            else -> emptyList()
        }
    }
} 
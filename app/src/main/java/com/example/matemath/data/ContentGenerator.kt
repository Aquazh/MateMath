package com.example.matemath.data

import kotlin.random.Random

object ContentGenerator {
    
    fun generateLearningPath(): LearningPath {
        val units = listOf(
            generateUnit("unit_1", "Primeros Pasos", "Suma básica con números pequeños", "🌱", MathCategory.ADDITION),
            generateUnit("unit_2", "Suma Divertida", "Suma con números más grandes", "➕", MathCategory.ADDITION),
            generateUnit("unit_3", "Restar Fácil", "Resta básica paso a paso", "➖", MathCategory.SUBTRACTION),
            generateUnit("unit_4", "Resta Avanzada", "Resta con números grandes", "🎯", MathCategory.SUBTRACTION),
            generateUnit("unit_5", "Multiplicar", "Tablas de multiplicar", "✖️", MathCategory.MULTIPLICATION),
            generateUnit("unit_6", "Dividir", "División básica", "➗", MathCategory.DIVISION)
        )
        
        // First unit is unlocked by default
        val unlockedUnits = units.mapIndexed { index, unit ->
            unit.copy(isUnlocked = index == 0)
        }
        
        return LearningPath(units = unlockedUnits)
    }
    
    private fun generateUnit(id: String, title: String, description: String, icon: String, category: MathCategory): LearningUnit {
        val lessons = when (category) {
            MathCategory.ADDITION -> generateAdditionLessons(id)
            MathCategory.SUBTRACTION -> generateSubtractionLessons(id)
            MathCategory.MULTIPLICATION -> generateMultiplicationLessons(id)
            MathCategory.DIVISION -> generateDivisionLessons(id)
            MathCategory.MIXED -> generateMixedLessons(id)
        }
        
        return LearningUnit(
            id = id,
            title = title,
            description = description,
            icon = icon,
            lessons = lessons,
            category = category
        )
    }
    
    private fun generateAdditionLessons(unitId: String): List<Lesson> {
        return listOf(
            Lesson(
                id = "${unitId}_lesson_1",
                title = "Suma 1-5",
                concept = MathConcept.SINGLE_DIGIT_ADDITION,
                problems = generateAdditionProblems(1, 5, 5)
            ),
            Lesson(
                id = "${unitId}_lesson_2",
                title = "Suma 1-10",
                concept = MathConcept.SINGLE_DIGIT_ADDITION,
                problems = generateAdditionProblems(1, 10, 8)
            ),
            Lesson(
                id = "${unitId}_lesson_3",
                title = "Suma con 10+",
                concept = MathConcept.DOUBLE_DIGIT_ADDITION,
                problems = generateAdditionProblems(10, 20, 6)
            )
        )
    }
    
    private fun generateSubtractionLessons(unitId: String): List<Lesson> {
        return listOf(
            Lesson(
                id = "${unitId}_lesson_1",
                title = "Resta 1-5",
                concept = MathConcept.SINGLE_DIGIT_SUBTRACTION,
                problems = generateSubtractionProblems(1, 5, 5)
            ),
            Lesson(
                id = "${unitId}_lesson_2",
                title = "Resta 1-10",
                concept = MathConcept.SINGLE_DIGIT_SUBTRACTION,
                problems = generateSubtractionProblems(1, 10, 8)
            )
        )
    }
    
    private fun generateMultiplicationLessons(unitId: String): List<Lesson> {
        return listOf(
            Lesson(
                id = "${unitId}_lesson_1",
                title = "Tabla del 2",
                concept = MathConcept.MULTIPLICATION_TABLES,
                problems = generateMultiplicationProblems(2, 1, 10, 5)
            ),
            Lesson(
                id = "${unitId}_lesson_2",
                title = "Tabla del 3",
                concept = MathConcept.MULTIPLICATION_TABLES,
                problems = generateMultiplicationProblems(3, 1, 10, 5)
            )
        )
    }
    
    private fun generateDivisionLessons(unitId: String): List<Lesson> {
        return listOf(
            Lesson(
                id = "${unitId}_lesson_1",
                title = "División por 2",
                concept = MathConcept.DIVISION_BASICS,
                problems = generateDivisionProblems(2, 5)
            )
        )
    }
    
    private fun generateMixedLessons(unitId: String): List<Lesson> {
        return listOf(
            Lesson(
                id = "${unitId}_lesson_1",
                title = "Repaso General",
                concept = MathConcept.SINGLE_DIGIT_ADDITION,
                problems = generateMixedProblems(5)
            )
        )
    }
    
    fun generateProblemForConcept(concept: MathConcept, difficulty: DifficultyLevel): MathProblem {
        return when (concept) {
            MathConcept.SINGLE_DIGIT_ADDITION -> generateAdditionProblems(1, 9, 1, difficulty).first()
            MathConcept.DOUBLE_DIGIT_ADDITION -> generateAdditionProblems(10, 99, 1, difficulty).first()
            MathConcept.SINGLE_DIGIT_SUBTRACTION -> generateSubtractionProblems(1, 9, 1, difficulty).first()
            MathConcept.DOUBLE_DIGIT_SUBTRACTION -> generateSubtractionProblems(10, 99, 1, difficulty).first()
            MathConcept.MULTIPLICATION_TABLES -> generateMultiplicationProblems(Random.nextInt(2, 11), 1, 10, 1, difficulty).first()
            MathConcept.DIVISION_BASICS -> generateDivisionProblems(Random.nextInt(2, 6), 1, difficulty).first()
            else -> generateAdditionProblems(1, 9, 1, difficulty).first()
        }
    }
    
    private fun generateAdditionProblems(min: Int, max: Int, count: Int, difficulty: DifficultyLevel = DifficultyLevel.BEGINNER): List<MathProblem> {
        return (1..count).map {
            val first = Random.nextInt(min, max + 1)
            val second = Random.nextInt(min, max + 1)
            val correct = first + second
            
            val wrongAnswers = generateWrongAnswers(correct, 3)
            val options = (wrongAnswers + correct).shuffled()
            
            MathProblem(
                firstNumber = first,
                secondNumber = second,
                operation = MathOperation.ADDITION,
                correctAnswer = correct,
                options = options,
                difficulty = difficulty,
                concept = if (max <= 9) MathConcept.SINGLE_DIGIT_ADDITION else MathConcept.DOUBLE_DIGIT_ADDITION,
                hints = generateHintsForAddition(first, second, correct),
                visualAid = generateVisualAidForAddition(first, second),
                explanation = generateExplanationForAddition(first, second, correct)
            )
        }
    }
    
    private fun generateSubtractionProblems(min: Int, max: Int, count: Int, difficulty: DifficultyLevel = DifficultyLevel.BEGINNER): List<MathProblem> {
        return (1..count).map {
            val first = Random.nextInt(min + 1, max + 1)
            val second = Random.nextInt(min, first)
            val correct = first - second
            
            val wrongAnswers = generateWrongAnswers(correct, 3)
            val options = (wrongAnswers + correct).shuffled()
            
            MathProblem(
                firstNumber = first,
                secondNumber = second,
                operation = MathOperation.SUBTRACTION,
                correctAnswer = correct,
                options = options,
                difficulty = difficulty,
                concept = if (max <= 9) MathConcept.SINGLE_DIGIT_SUBTRACTION else MathConcept.DOUBLE_DIGIT_SUBTRACTION,
                hints = generateHintsForSubtraction(first, second, correct),
                visualAid = generateVisualAidForSubtraction(first, second),
                explanation = generateExplanationForSubtraction(first, second, correct)
            )
        }
    }
    
    private fun generateMultiplicationProblems(table: Int, minMultiplier: Int, maxMultiplier: Int, count: Int, difficulty: DifficultyLevel = DifficultyLevel.BEGINNER): List<MathProblem> {
        return (1..count).map {
            val first = table
            val second = Random.nextInt(minMultiplier, maxMultiplier + 1)
            val correct = first * second
            
            val wrongAnswers = generateWrongAnswers(correct, 3)
            val options = (wrongAnswers + correct).shuffled()
            
            MathProblem(
                firstNumber = first,
                secondNumber = second,
                operation = MathOperation.MULTIPLICATION,
                correctAnswer = correct,
                options = options,
                difficulty = difficulty,
                concept = MathConcept.MULTIPLICATION_TABLES,
                hints = generateHintsForMultiplication(first, second, correct),
                visualAid = generateVisualAidForMultiplication(first, second),
                explanation = generateExplanationForMultiplication(first, second, correct)
            )
        }
    }
    
    private fun generateDivisionProblems(divisor: Int, count: Int, difficulty: DifficultyLevel = DifficultyLevel.BEGINNER): List<MathProblem> {
        return (1..count).map {
            val quotient = Random.nextInt(1, 11)
            val first = divisor * quotient
            val second = divisor
            val correct = quotient
            
            val wrongAnswers = generateWrongAnswers(correct, 3)
            val options = (wrongAnswers + correct).shuffled()
            
            MathProblem(
                firstNumber = first,
                secondNumber = second,
                operation = MathOperation.DIVISION,
                correctAnswer = correct,
                options = options,
                difficulty = difficulty,
                concept = MathConcept.DIVISION_BASICS,
                hints = generateHintsForDivision(first, second, correct),
                visualAid = generateVisualAidForDivision(first, second),
                explanation = generateExplanationForDivision(first, second, correct)
            )
        }
    }
    
    private fun generateMixedProblems(count: Int): List<MathProblem> {
        val problems = mutableListOf<MathProblem>()
        repeat(count) {
            val operation = MathOperation.values().random()
            val problem = when (operation) {
                MathOperation.ADDITION -> generateAdditionProblems(1, 10, 1).first()
                MathOperation.SUBTRACTION -> generateSubtractionProblems(1, 10, 1).first()
                MathOperation.MULTIPLICATION -> generateMultiplicationProblems(Random.nextInt(2, 6), 1, 5, 1).first()
                MathOperation.DIVISION -> generateDivisionProblems(Random.nextInt(2, 6), 1).first()
            }
            problems.add(problem)
        }
        return problems
    }
    
    private fun generateWrongAnswers(correct: Int, count: Int): List<Int> {
        val wrongAnswers = mutableSetOf<Int>()
        val range = maxOf(1, correct / 3)
        
        while (wrongAnswers.size < count) {
            val wrong = correct + Random.nextInt(-range, range + 1)
            if (wrong != correct && wrong >= 0) {
                wrongAnswers.add(wrong)
            }
        }
        return wrongAnswers.toList()
    }
    
    // Hint Generators
    private fun generateHintsForAddition(first: Int, second: Int, answer: Int): List<Hint> {
        return listOf(
            Hint(1, "💡 Puedes usar tus dedos para contar", HintType.VISUAL),
            Hint(2, "🔢 Empieza desde $first y cuenta $second números más", HintType.STEP_BY_STEP),
            Hint(3, "➕ Sumar significa 'juntar' o 'agregar'", HintType.CONCEPTUAL),
            Hint(4, "✨ La respuesta será mayor que $first", HintType.ENCOURAGEMENT)
        )
    }
    
    private fun generateHintsForSubtraction(first: Int, second: Int, answer: Int): List<Hint> {
        return listOf(
            Hint(1, "💡 Restar significa 'quitar' algo", HintType.CONCEPTUAL),
            Hint(2, "🔢 Empieza desde $first y cuenta $second hacia atrás", HintType.STEP_BY_STEP),
            Hint(3, "➖ ¿Cuánto queda si quitas $second de $first?", HintType.VISUAL),
            Hint(4, "✨ La respuesta será menor que $first", HintType.ENCOURAGEMENT)
        )
    }
    
    private fun generateHintsForMultiplication(first: Int, second: Int, answer: Int): List<Hint> {
        return listOf(
            Hint(1, "💡 Multiplicar es sumar el mismo número varias veces", HintType.CONCEPTUAL),
            Hint(2, "🔢 $first × $second = $first + $first + ... ($second veces)", HintType.STEP_BY_STEP),
            Hint(3, "➖ Puedes hacer grupos de $first", HintType.VISUAL),
            Hint(4, "✨ ¿Recuerdas la tabla del $first?", HintType.ENCOURAGEMENT)
        )
    }
    
    private fun generateHintsForDivision(first: Int, second: Int, answer: Int): List<Hint> {
        return listOf(
            Hint(1, "💡 Dividir es repartir en grupos iguales", HintType.CONCEPTUAL),
            Hint(2, "🔢 ¿Cuántas veces cabe $second en $first?", HintType.STEP_BY_STEP),
            Hint(3, "➖ Piensa en la tabla del $second", HintType.VISUAL),
            Hint(4, "✨ $second × ? = $first", HintType.ENCOURAGEMENT)
        )
    }
    
    // Visual Aid Generators
    private fun generateVisualAidForAddition(first: Int, second: Int): VisualAid {
        return VisualAid(
            type = VisualType.COUNTING_OBJECTS,
            description = "Cuenta todos los objetos",
            elements = listOf(
                VisualElement("🟡".repeat(first), 0),
                VisualElement("🟢".repeat(second), 1),
                VisualElement("Total: ${first + second}", 2)
            )
        )
    }
    
    private fun generateVisualAidForSubtraction(first: Int, second: Int): VisualAid {
        return VisualAid(
            type = VisualType.COUNTING_OBJECTS,
            description = "Quita los objetos marcados",
            elements = listOf(
                VisualElement("Inicial: " + "🟡".repeat(first), 0),
                VisualElement("Quitar: " + "❌".repeat(second), 1),
                VisualElement("Quedan: " + "🟡".repeat(first - second), 2)
            )
        )
    }
    
    private fun generateVisualAidForMultiplication(first: Int, second: Int): VisualAid {
        return VisualAid(
            type = VisualType.GROUPING,
            description = "Cuenta los grupos",
            elements = (1..second).map { group ->
                VisualElement("Grupo $group: " + "🟡".repeat(first), group - 1)
            } + VisualElement("Total: ${first * second}", second)
        )
    }
    
    private fun generateVisualAidForDivision(first: Int, second: Int): VisualAid {
        val result = first / second
        return VisualAid(
            type = VisualType.GROUPING,
            description = "Reparte en grupos iguales",
            elements = listOf(
                VisualElement("Total: " + "🟡".repeat(first), 0),
                VisualElement("Grupos de $second: " + ("🟡".repeat(second) + " ").repeat(result), 1),
                VisualElement("Resultado: $result grupos", 2)
            )
        )
    }
    
    // Explanation Generators
    private fun generateExplanationForAddition(first: Int, second: Int, answer: Int): ProblemExplanation {
        return ProblemExplanation(
            steps = listOf(
                ExplanationStep(1, "Identifica los números", "Tenemos $first y $second", "$first + $second"),
                ExplanationStep(2, "Cuenta desde el primer número", "Empieza en $first y cuenta $second más", "$first → ${first + 1} → ... → $answer"),
                ExplanationStep(3, "Resultado", "Al juntar todo obtenemos $answer", "$first + $second = $answer")
            ),
            visualRepresentation = "🟡".repeat(first) + " + " + "🟢".repeat(second) + " = $answer",
            audioScript = "Vamos a sumar $first más $second. Empezamos con $first y agregamos $second más, eso nos da $answer."
        )
    }
    
    private fun generateExplanationForSubtraction(first: Int, second: Int, answer: Int): ProblemExplanation {
        return ProblemExplanation(
            steps = listOf(
                ExplanationStep(1, "Identifica los números", "Tenemos $first y queremos quitar $second", "$first - $second"),
                ExplanationStep(2, "Cuenta hacia atrás", "Desde $first, cuenta $second hacia atrás", "$first → ${first - 1} → ... → $answer"),
                ExplanationStep(3, "Resultado", "Después de quitar $second, quedan $answer", "$first - $second = $answer")
            ),
            visualRepresentation = "🟡".repeat(first) + " - " + "❌".repeat(second) + " = " + "🟡".repeat(answer),
            audioScript = "Vamos a restar $second de $first. Empezamos con $first y quitamos $second, nos quedan $answer."
        )
    }
    
    private fun generateExplanationForMultiplication(first: Int, second: Int, answer: Int): ProblemExplanation {
        return ProblemExplanation(
            steps = listOf(
                ExplanationStep(1, "Entender la multiplicación", "Multiplicar es sumar el mismo número varias veces", "$first × $second"),
                ExplanationStep(2, "Crear grupos", "Hacemos $second grupos de $first", "$first + $first + ... ($second veces)"),
                ExplanationStep(3, "Contar el total", "Contamos todo y obtenemos $answer", "$first × $second = $answer")
            ),
            visualRepresentation = ("🟡".repeat(first) + " ").repeat(second) + "= $answer",
            audioScript = "Vamos a multiplicar $first por $second. Eso significa $second grupos de $first, que nos da $answer."
        )
    }
    
    private fun generateExplanationForDivision(first: Int, second: Int, answer: Int): ProblemExplanation {
        return ProblemExplanation(
            steps = listOf(
                ExplanationStep(1, "Entender la división", "Dividir es repartir en grupos iguales", "$first ÷ $second"),
                ExplanationStep(2, "Hacer grupos", "Repartimos $first objetos en grupos de $second", "¿Cuántos grupos?"),
                ExplanationStep(3, "Contar grupos", "Obtenemos $answer grupos completos", "$first ÷ $second = $answer")
            ),
            visualRepresentation = "🟡".repeat(first) + " → " + ("🟡".repeat(second) + " ").repeat(answer) + "= $answer grupos",
            audioScript = "Vamos a dividir $first entre $second. Repartimos en grupos de $second y obtenemos $answer grupos."
        )
    }
} 
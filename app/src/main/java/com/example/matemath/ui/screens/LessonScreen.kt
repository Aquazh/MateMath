package com.example.matemath.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.example.matemath.data.*
import com.example.matemath.ui.theme.MateMathColors
import com.example.matemath.ui.theme.EducationalTypography
import com.example.matemath.viewmodel.MateMathViewModel
import androidx.compose.ui.draw.rotate

@Composable
fun LessonScreen(
    viewModel: MateMathViewModel,
    onComplete: () -> Unit
) {
    val currentProblem by viewModel.currentProblem.collectAsState()
    val quizState by viewModel.quizState.collectAsState()
    val learningState by viewModel.learningState.collectAsState()
    val appState by viewModel.appState.collectAsState()
    
    // Confetti animation state
    var showConfetti by remember { mutableStateOf(false) }
    
    LaunchedEffect(quizState.showResult) {
        if (quizState.showResult && quizState.selectedAnswer == currentProblem?.correctAnswer) {
            showConfetti = true
            delay(2000)
            showConfetti = false
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MateMathColors.Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with XP and progress
            LessonHeader(
                userProfile = appState.user,
                currentLesson = appState.currentLesson,
                onClose = onComplete
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            currentProblem?.let { problem ->
                // Problem display
                ProblemCard(
                    problem = problem,
                    quizState = quizState,
                    learningState = learningState,
                    onToggleStepByStep = { viewModel.toggleStepByStep() },
                    onNextStep = { viewModel.nextStep() },
                    onPreviousStep = { viewModel.previousStep() },
                    onRepeatQuestion = { viewModel.repeatQuestion() }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Answer options
                AnswerOptionsGrid(
                    problem = problem,
                    quizState = quizState,
                    onAnswerSelected = { answer ->
                        viewModel.onAnswerSelected(answer)
                    }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Action buttons
                ActionButtons(
                    problem = problem,
                    quizState = quizState,
                    onShowHint = { viewModel.showHint() },
                    onNextProblem = { viewModel.nextProblem() }
                )
            }
        }
        
        // Confetti overlay
        if (showConfetti) {
            ConfettiAnimation()
        }
    }
}

@Composable
fun LessonHeader(
    userProfile: UserProfile,
    currentLesson: Lesson?,
    onClose: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onClose) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Cerrar",
                tint = MateMathColors.OnSurfaceSecondary
            )
        }
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            currentLesson?.let {
                Text(
                    text = it.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MateMathColors.OnSurface,
                    fontWeight = FontWeight.Medium
                )
            }
            Text(
                text = "${userProfile.xpTotal} XP",
                style = EducationalTypography.GameScore,
                color = MateMathColors.Primary
            )
        }
        
        // Mascot with reaction
        Text(
            text = "🦙",
            fontSize = 32.sp,
            modifier = Modifier
                .padding(8.dp)
        )
    }
}

@Composable
fun ProblemCard(
    problem: MathProblem,
    quizState: MateMathViewModel.QuizState,
    learningState: MateMathViewModel.LearningState,
    onToggleStepByStep: () -> Unit,
    onNextStep: () -> Unit,
    onPreviousStep: () -> Unit,
    onRepeatQuestion: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MateMathColors.Surface),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Problem question
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${problem.firstNumber} ${problem.operation.symbol} ${problem.secondNumber} = ?",
                    style = EducationalTypography.MathProblem,
                    color = MateMathColors.OnSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
                
                // Control buttons
                Row {
                    IconButton(onClick = onRepeatQuestion) {
                        Icon(
                            Icons.Default.VolumeUp,
                            contentDescription = "Repetir pregunta",
                            tint = MateMathColors.Primary
                        )
                    }
                    
                    if (problem.explanation != null) {
                        IconButton(onClick = onToggleStepByStep) {
                            Icon(
                                if (learningState.showStepByStep) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Ver pasos",
                                tint = MateMathColors.Primary
                            )
                        }
                    }
                }
            }
            
            // Visual aid
            if (learningState.showVisualAid && problem.visualAid != null) {
                Spacer(modifier = Modifier.height(16.dp))
                VisualAidCard(visualAid = problem.visualAid)
            }
            
            // Step-by-step explanation
            AnimatedVisibility(
                visible = learningState.showStepByStep && problem.explanation != null,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                problem.explanation?.let { explanation ->
                    StepByStepCard(
                        explanation = explanation,
                        currentStep = learningState.currentStep,
                        onNextStep = onNextStep,
                        onPreviousStep = onPreviousStep
                    )
                }
            }
        }
    }
}

@Composable
fun VisualAidCard(
    visualAid: VisualAid
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MateMathColors.InfoLight),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = visualAid.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MateMathColors.OnSurface,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            visualAid.elements.forEach { element ->
                Text(
                    text = element.content,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MateMathColors.OnSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun StepByStepCard(
    explanation: ProblemExplanation,
    currentStep: Int,
    onNextStep: () -> Unit,
    onPreviousStep: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MateMathColors.SuccessLight),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Explicación paso a paso",
                    style = MaterialTheme.typography.titleMedium,
                    color = MateMathColors.OnSurface,
                    fontWeight = FontWeight.SemiBold
                )
                
                Row {
                    IconButton(
                        onClick = onPreviousStep,
                        enabled = currentStep > 0
                    ) {
                        Icon(
                            Icons.Default.ChevronLeft,
                            contentDescription = "Paso anterior",
                            tint = if (currentStep > 0) MateMathColors.OnSurface else MateMathColors.OnSurfaceTertiary
                        )
                    }
                    
                    IconButton(
                        onClick = onNextStep,
                        enabled = currentStep < explanation.steps.size - 1
                    ) {
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = "Siguiente paso",
                            tint = if (currentStep < explanation.steps.size - 1) MateMathColors.OnSurface else MateMathColors.OnSurfaceTertiary
                        )
                    }
                }
            }
            
            if (explanation.steps.isNotEmpty() && currentStep < explanation.steps.size) {
                val step = explanation.steps[currentStep]
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = step.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MateMathColors.Success,
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = step.calculation,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MateMathColors.OnSurface,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = step.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MateMathColors.OnSurface
                )
                
                step.visual?.let { visual ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = visual,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MateMathColors.OnSurface,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                // Progress dots
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(explanation.steps.size) { index ->
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    color = if (index <= currentStep) MateMathColors.Success else MateMathColors.OnSurfaceTertiary,
                                    shape = CircleShape
                                )
                        )
                        if (index < explanation.steps.size - 1) {
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnswerOptionsGrid(
    problem: MathProblem,
    quizState: MateMathViewModel.QuizState,
    onAnswerSelected: (Int) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        problem.options.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                row.forEach { option ->
                    AnswerOptionCard(
                        option = option,
                        isSelected = quizState.selectedAnswer == option,
                        isCorrect = option == problem.correctAnswer,
                        showResult = quizState.showResult,
                        onSelected = { onAnswerSelected(option) },
                        modifier = Modifier.weight(1f)
                    )
                }
                
                // Add spacer if row has only one item
                if (row.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun AnswerOptionCard(
    option: Int,
    isSelected: Boolean,
    isCorrect: Boolean,
    showResult: Boolean,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        showResult && isCorrect -> MateMathColors.Success
        showResult && isSelected && !isCorrect -> MateMathColors.Error
        isSelected -> MateMathColors.Primary
        else -> MateMathColors.Surface
    }
    
    val textColor = when {
        showResult && (isCorrect || (isSelected && !isCorrect)) -> MateMathColors.Surface
        isSelected -> MateMathColors.Surface
        else -> MateMathColors.OnSurface
    }
    
    // Animation for correct answer
    val scale by animateFloatAsState(
        targetValue = if (showResult && isCorrect) 1.05f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "answer_scale"
    )
    
    Card(
        modifier = modifier
            .aspectRatio(1.5f)
            .scale(scale)
            .clickable(enabled = !showResult) { onSelected() },
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(if (isSelected || showResult) 8.dp else 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = option.toString(),
                    style = EducationalTypography.AnswerOption,
                    color = textColor,
                    fontWeight = FontWeight.Bold
                )
                
                // Success indicator
                if (showResult && isCorrect) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "🎉",
                        fontSize = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ActionButtons(
    problem: MathProblem,
    quizState: MateMathViewModel.QuizState,
    onShowHint: () -> Unit,
    onNextProblem: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Hint button
        if (!quizState.showResult && problem.hints.isNotEmpty()) {
            Button(
                onClick = onShowHint,
                enabled = quizState.hintsUsed < problem.hints.size,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MateMathColors.Warning,
                    contentColor = MateMathColors.Surface
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    Icons.Default.Lightbulb,
                    contentDescription = "Pista",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Pista (${quizState.hintsUsed}/${problem.hints.size})",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }
        
        // Next problem button
        if (quizState.showResult) {
            Button(
                onClick = onNextProblem,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MateMathColors.Primary,
                    contentColor = MateMathColors.Surface
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(2f)
            ) {
                Text(
                    text = "Siguiente problema",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = "Siguiente",
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun ConfettiAnimation() {
    // Simple confetti effect with animated emojis
    val confettiItems = listOf("🎉", "⭐", "🌟", "✨", "🎊")
    
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        repeat(15) { index ->
            var animationStarted by remember { mutableStateOf(false) }
            
            val offsetY by animateFloatAsState(
                targetValue = if (animationStarted) 1000f else -100f,
                animationSpec = tween(
                    durationMillis = 2000 + (index * 100),
                    easing = EaseInOut
                ),
                label = "confetti_fall_$index"
            )
            
            val rotation by animateFloatAsState(
                targetValue = if (animationStarted) 360f * 3 else 0f,
                animationSpec = tween(
                    durationMillis = 2000 + (index * 100),
                    easing = LinearEasing
                ),
                label = "confetti_rotation_$index"
            )
            
            LaunchedEffect(Unit) {
                delay(index * 50L)
                animationStarted = true
            }
            
            Text(
                text = confettiItems[index % confettiItems.size],
                fontSize = 24.sp,
                modifier = Modifier
                    .offset(
                        x = (50 + (index * 25) % 300).dp,
                        y = offsetY.dp
                    )
                    .rotate(rotation)
            )
        }
    }
} 
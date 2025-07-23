package com.example.matemath

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun WelcomeScreen(
    onStartGame: () -> Unit
) {
    val gradientColors = listOf(
        Color(0xFF4CAF50), // Green
        Color(0xFF81C784)  // Light Green
    )
    
    // Animations for welcome screen
    val llamaScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "llama_scale"
    )
    
    val llamaRotation by animateFloatAsState(
        targetValue = 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessVeryLow
        ),
        label = "llama_rotation"
    )
    
    // Breathing animation for the llama
    val breathingScale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathing"
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(gradientColors)
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Animated Llama mascot
        Text(
            text = "🦙",
            fontSize = 120.sp,
            modifier = Modifier
                .padding(bottom = 32.dp)
                .scale(llamaScale * (0.95f + breathingScale * 0.05f))
        )
        
        // Welcome title with slide-in animation
        var titleVisible by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            delay(300)
            titleVisible = true
        }
        
        AnimatedVisibility(
            visible = titleVisible,
            enter = slideInVertically(
                initialOffsetY = { -it },
                animationSpec = tween(600)
            ) + fadeIn(animationSpec = tween(600))
        ) {
            Text(
                text = stringResource(R.string.welcome_title),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        
        // Subtitle with slide-in animation
        var subtitleVisible by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            delay(600)
            subtitleVisible = true
        }
        
        AnimatedVisibility(
            visible = subtitleVisible,
            enter = slideInVertically(
                initialOffsetY = { -it },
                animationSpec = tween(600)
            ) + fadeIn(animationSpec = tween(600))
        ) {
            Text(
                text = stringResource(R.string.welcome_subtitle),
                fontSize = 18.sp,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 48.dp)
            )
        }
        
        // Start button with slide-in animation
        var buttonVisible by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            delay(900)
            buttonVisible = true
        }
        
        AnimatedVisibility(
            visible = buttonVisible,
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(600)
            ) + fadeIn(animationSpec = tween(600))
        ) {
            var buttonPressed by remember { mutableStateOf(false) }
            val buttonScale by animateFloatAsState(
                targetValue = if (buttonPressed) 0.95f else 1f,
                animationSpec = tween(100),
                label = "button_press"
            )
            
            Button(
                onClick = {
                    buttonPressed = true
                    onStartGame()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .scale(buttonScale),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF4CAF50)
                ),
                shape = RoundedCornerShape(30.dp)
            ) {
                Text(
                    text = stringResource(R.string.start_playing),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun MathProblemScreen(
    gameState: GameState,
    onAnswerSelected: (Int) -> Unit,
    onNextQuestion: () -> Unit,
    onRepeatQuestion: () -> Unit
) {
    val problem = gameState.currentProblem ?: return
    var selectedAnswer by remember { mutableStateOf<Int?>(null) }
    var showResult by remember { mutableStateOf(false) }
    
    LaunchedEffect(problem) {
        selectedAnswer = null
        showResult = false
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3E5F5)) // Light purple background
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Score display
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Text(
                    text = stringResource(R.string.score, gameState.score),
                    modifier = Modifier.padding(12.dp),
                    fontWeight = FontWeight.Bold
                )
            }
            
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFD700)), // Gold
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Text(
                    text = stringResource(R.string.yerba_coins, gameState.yerbaCoins),
                    modifier = Modifier.padding(12.dp),
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Math problem with speaker button
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Question with speaker button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(
                            R.string.math_question,
                            problem.firstNumber,
                            problem.operation.symbol,
                            problem.secondNumber
                        ),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                    
                    // Animated speaker button
                    var isPressed by remember { mutableStateOf(false) }
                    val scale by animateFloatAsState(
                        targetValue = if (isPressed) 0.9f else 1f,
                        animationSpec = tween(100), 
                        label = "speaker_scale"
                    )
                    
                    FloatingActionButton(
                        onClick = {
                            isPressed = true
                            onRepeatQuestion()
                        },
                        modifier = Modifier
                            .scale(scale)
                            .size(56.dp),
                        containerColor = Color(0xFF4CAF50),
                        shape = CircleShape
                    ) {
                        Icon(
                            Icons.Default.VolumeUp,
                            contentDescription = "Repetir pregunta",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    
                    LaunchedEffect(isPressed) {
                        if (isPressed) {
                            delay(200)
                            isPressed = false
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Answer options with animations
                problem.options.forEachIndexed { index, option ->
                    val isSelected = selectedAnswer == option
                    val isCorrect = option == problem.correctAnswer
                    val cardColor = when {
                        showResult && isCorrect -> Color(0xFF4CAF50) // Green
                        showResult && isSelected && !isCorrect -> Color(0xFFF44336) // Red
                        isSelected -> Color(0xFF2196F3) // Blue
                        else -> Color.White
                    }
                    
                    // Animation for the card appearance
                    val animatedScale by animateFloatAsState(
                        targetValue = if (showResult && isCorrect) 1.05f else 1f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        label = "correct_answer_scale"
                    )
                    
                    // Slide in animation with delay
                    val slideOffset by animateIntAsState(
                        targetValue = 0,
                        animationSpec = tween(
                            durationMillis = 300,
                            delayMillis = index * 100
                        ),
                        label = "slide_in"
                    )
                    
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .scale(animatedScale)
                            .offset(x = slideOffset.dp)
                            .selectable(
                                selected = isSelected,
                                onClick = {
                                    if (!showResult) {
                                        selectedAnswer = option
                                        showResult = true
                                        onAnswerSelected(option)
                                    }
                                }
                            ),
                        colors = CardDefaults.cardColors(containerColor = cardColor),
                        elevation = CardDefaults.cardElevation(if (isSelected) 8.dp else 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = option.toString(),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (cardColor == Color.White) Color.Black else Color.White,
                                textAlign = TextAlign.Center
                            )
                            
                            // Add celebration emoji for correct answer
                            if (showResult && isCorrect) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "🎉",
                                    fontSize = 20.sp,
                                    modifier = Modifier.scale(animatedScale)
                                )
                            }
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Result message and next button with animations
        AnimatedVisibility(
            visible = showResult,
            enter = slideInVertically(
                initialOffsetY = { it / 2 },
                animationSpec = tween(400)
            ) + fadeIn(animationSpec = tween(400))
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val isCorrect = selectedAnswer == problem.correctAnswer
                
                // Animated result message
                val messageScale by animateFloatAsState(
                    targetValue = if (showResult) 1f else 0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    ),
                    label = "message_scale"
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Fun emoji for feedback
                    Text(
                        text = if (isCorrect) "🌟" else "💪",
                        fontSize = 24.sp,
                        modifier = Modifier.scale(messageScale)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isCorrect) stringResource(R.string.correct_answer) 
                              else stringResource(R.string.wrong_answer),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCorrect) Color(0xFF4CAF50) else Color(0xFFF44336),
                        modifier = Modifier.scale(messageScale)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isCorrect) "🌟" else "💪",
                        fontSize = 24.sp,
                        modifier = Modifier.scale(messageScale)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Animated next button
                val buttonScale by animateFloatAsState(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "button_scale"
                )
                
                Button(
                    onClick = onNextQuestion,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .scale(buttonScale),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    ),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text(
                        text = stringResource(R.string.next_question),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ModeSelectionScreen(
    onModeSelected: (Boolean) -> Unit
) {
    val gradientColors = listOf(
        Color(0xFF6A4C93), // Purple
        Color(0xFF9C89B8)  // Light Purple
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(gradientColors)
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Title
        Text(
            text = "¿Cómo quieres jugar?",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 48.dp)
        )
        
        // Learning Mode Card
        var learningPressed by remember { mutableStateOf(false) }
        val learningScale by animateFloatAsState(
            targetValue = if (learningPressed) 0.95f else 1f,
            animationSpec = tween(100),
            label = "learning_scale"
        )
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(bottom = 16.dp)
                .scale(learningScale),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50)),
            elevation = CardDefaults.cardElevation(8.dp),
            onClick = {
                learningPressed = true
                onModeSelected(true)
            }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.School,
                    contentDescription = "Aprender",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "🎓 Modo Aprendizaje",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Te enseño paso a paso",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }
        
        // Practice Mode Card
        var practicePressed by remember { mutableStateOf(false) }
        val practiceScale by animateFloatAsState(
            targetValue = if (practicePressed) 0.95f else 1f,
            animationSpec = tween(100),
            label = "practice_scale"
        )
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .scale(practiceScale),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2196F3)),
            elevation = CardDefaults.cardElevation(8.dp),
            onClick = {
                practicePressed = true
                onModeSelected(false)
            }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Sports,
                    contentDescription = "Practicar",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "⚡ Modo Práctica",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Resuelve problemas rápido",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Mascot
        Text(
            text = "🦙",
            fontSize = 80.sp,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun LearningModeScreen(
    gameState: GameState,
    onAnswerSelected: (Int) -> Unit,
    onNextQuestion: () -> Unit,
    onRepeatQuestion: () -> Unit,
    onShowHint: () -> Unit,
    onNextStep: () -> Unit,
    onPreviousStep: () -> Unit,
    onRepeatStep: () -> Unit
) {
    val problem = gameState.currentProblem ?: return
    var selectedAnswer by remember { mutableStateOf<Int?>(null) }
    var showResult by remember { mutableStateOf(false) }
    var showStepByStep by remember { mutableStateOf(false) }
    
    LaunchedEffect(problem) {
        selectedAnswer = null
        showResult = false
        showStepByStep = false
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header with learning mode indicator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50)),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.School,
                        contentDescription = "Learning Mode",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Aprendiendo",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Row {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.score, gameState.score),
                        modifier = Modifier.padding(12.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFD700)),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.yerba_coins, gameState.yerbaCoins),
                        modifier = Modifier.padding(12.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Problem and controls
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Question with controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(
                            R.string.math_question,
                            problem.firstNumber,
                            problem.operation.symbol,
                            problem.secondNumber
                        ),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                    
                    // Control buttons
                    Row {
                        FloatingActionButton(
                            onClick = onRepeatQuestion,
                            modifier = Modifier.size(48.dp),
                            containerColor = Color(0xFF2196F3)
                        ) {
                            Icon(
                                Icons.Default.VolumeUp,
                                contentDescription = "Repetir pregunta",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        FloatingActionButton(
                            onClick = { showStepByStep = !showStepByStep },
                            modifier = Modifier.size(48.dp),
                            containerColor = Color(0xFF4CAF50)
                        ) {
                            Icon(
                                Icons.Default.MenuBook,
                                contentDescription = "Ver pasos",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
                
                // Visual aid if available
                problem.visualAid?.let { visualAid ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = visualAid.description,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            visualAid.interactiveElements.forEach { element ->
                                Text(
                                    text = element,
                                    fontSize = 18.sp,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Step-by-step explanation (expandable)
        AnimatedVisibility(
            visible = showStepByStep && problem.steps.isNotEmpty(),
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E8))
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
                            text = "📚 Explicación paso a paso",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Row {
                            IconButton(
                                onClick = onPreviousStep,
                                enabled = gameState.currentStep > 0
                            ) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Paso anterior")
                            }
                            
                            IconButton(onClick = onRepeatStep) {
                                Icon(Icons.Default.VolumeUp, contentDescription = "Repetir paso")
                            }
                            
                            IconButton(
                                onClick = onNextStep,
                                enabled = gameState.currentStep < problem.steps.size - 1
                            ) {
                                Icon(Icons.Default.ArrowForward, contentDescription = "Siguiente paso")
                            }
                        }
                    }
                    
                    if (problem.steps.isNotEmpty() && gameState.currentStep < problem.steps.size) {
                        val currentStep = problem.steps[gameState.currentStep]
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Text(
                            text = currentStep.description,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF2E7D32)
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = currentStep.calculation,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = currentStep.explanation,
                            fontSize = 14.sp,
                            color = Color(0xFF4E4E4E)
                        )
                        
                        currentStep.visualRepresentation?.let { visual ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = visual,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        
                        // Progress indicator
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            repeat(problem.steps.size) { index ->
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(
                                            if (index <= gameState.currentStep) Color(0xFF4CAF50)
                                            else Color(0xFFBDBDBD),
                                            CircleShape
                                        )
                                )
                                if (index < problem.steps.size - 1) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Answer options
        problem.options.forEachIndexed { index, option ->
            val isSelected = selectedAnswer == option
            val isCorrect = option == problem.correctAnswer
            val cardColor = when {
                showResult && isCorrect -> Color(0xFF4CAF50)
                showResult && isSelected && !isCorrect -> Color(0xFFF44336)
                isSelected -> Color(0xFF2196F3)
                else -> Color.White
            }
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .selectable(
                        selected = isSelected,
                        onClick = {
                            if (!showResult) {
                                selectedAnswer = option
                                showResult = true
                                onAnswerSelected(option)
                            }
                        }
                    ),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                elevation = CardDefaults.cardElevation(if (isSelected) 8.dp else 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = option.toString(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (cardColor == Color.White) Color.Black else Color.White
                    )
                    
                    if (showResult && isCorrect) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "🎉", fontSize = 16.sp)
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Hint button and next question
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (!showResult) {
                Button(
                    onClick = onShowHint,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                    enabled = gameState.hintsUsed < problem.hints.size
                ) {
                    Icon(Icons.Default.Lightbulb, contentDescription = "Pista")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Pista (${gameState.hintsUsed}/${problem.hints.size})")
                }
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }
            
            if (showResult) {
                Button(
                    onClick = onNextQuestion,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("Siguiente Problema")
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = "Siguiente")
                }
            }
        }
    }
}
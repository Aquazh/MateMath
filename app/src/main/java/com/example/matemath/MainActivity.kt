package com.example.matemath

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.matemath.ui.theme.MateMathTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MateMathTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MateMathApp()
                }
            }
        }
    }
}

@Composable
fun MateMathApp() {
    val navController = rememberNavController()
    val viewModel: MateMathViewModel = viewModel()
    val gameState by viewModel.gameState.collectAsState()
    val context = LocalContext.current
    
    // Initialize TTS
    val ttsManager = remember { TextToSpeechManager(context) }
    
    DisposableEffect(Unit) {
        viewModel.setTtsManager(ttsManager)
        onDispose {
            ttsManager.shutdown()
        }
    }
    
    NavHost(
        navController = navController,
        startDestination = "welcome"
    ) {
        composable("welcome") {
            WelcomeScreen(
                onStartGame = {
                    navController.navigate("mode_selection")
                }
            )
        }
        
        composable("mode_selection") {
            ModeSelectionScreen(
                onModeSelected = { isLearningMode ->
                    if (isLearningMode) {
                        viewModel.startLearningMode()
                        navController.navigate("learning")
                    } else {
                        viewModel.startNewGame(false)
                        navController.navigate("game")
                    }
                }
            )
        }
        
        composable("game") {
            MathProblemScreen(
                gameState = gameState,
                onAnswerSelected = { answer ->
                    viewModel.onAnswerSelected(answer)
                },
                onNextQuestion = {
                    viewModel.generateNextProblem()
                },
                onRepeatQuestion = {
                    viewModel.repeatQuestion()
                }
            )
        }
        
        composable("learning") {
            LearningModeScreen(
                gameState = gameState,
                onAnswerSelected = { answer ->
                    viewModel.onAnswerSelected(answer)
                },
                onNextQuestion = {
                    viewModel.generateNextProblem()
                },
                onRepeatQuestion = {
                    viewModel.repeatQuestion()
                },
                onShowHint = {
                    viewModel.showHint()
                },
                onNextStep = {
                    viewModel.nextStep()
                },
                onPreviousStep = {
                    viewModel.previousStep()
                },
                onRepeatStep = {
                    viewModel.repeatCurrentStep()
                }
            )
        }
    }
}
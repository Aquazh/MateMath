package com.example.matemath

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.matemath.ui.theme.MateMathTheme
import com.example.matemath.ui.theme.MateMathColors
import com.example.matemath.viewmodel.MateMathViewModel
import com.example.matemath.ui.screens.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MateMathTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MateMathColors.Background
                ) {
                    MateMathApp()
                }
            }
        }
    }
}

sealed class NavigationItem(
    val route: String,
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    object Learn : NavigationItem("learn", "Aprender", Icons.Default.School)
    object Practice : NavigationItem("practice", "Practicar", Icons.Default.Quiz)
    object Profile : NavigationItem("profile", "Perfil", Icons.Default.Person)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MateMathApp() {
    val navController = rememberNavController()
    val viewModel: MateMathViewModel = viewModel()
    val context = LocalContext.current
    
    // Initialize TTS
    val ttsManager = remember { TextToSpeechManager(context) }
    
    DisposableEffect(Unit) {
        viewModel.setTtsManager(ttsManager)
        onDispose {
            ttsManager.shutdown()
        }
    }
    
    val navigationItems = listOf(
        NavigationItem.Learn,
        NavigationItem.Practice,
        NavigationItem.Profile
    )
    
    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MateMathColors.Surface,
                contentColor = MateMathColors.Primary
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                
                navigationItems.forEach { item ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                item.icon,
                                contentDescription = item.title,
                                tint = if (currentDestination?.hierarchy?.any { it.route == item.route } == true) {
                                    MateMathColors.Primary
                                } else {
                                    MateMathColors.OnSurfaceTertiary
                                }
                            )
                        },
                        label = {
                            Text(
                                text = item.title,
                                color = if (currentDestination?.hierarchy?.any { it.route == item.route } == true) {
                                    MateMathColors.Primary
                                } else {
                                    MateMathColors.OnSurfaceTertiary
                                }
                            )
                        },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MateMathColors.Primary,
                            selectedTextColor = MateMathColors.Primary,
                            unselectedIconColor = MateMathColors.OnSurfaceTertiary,
                            unselectedTextColor = MateMathColors.OnSurfaceTertiary,
                            indicatorColor = MateMathColors.ProgressBackground
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavigationItem.Learn.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavigationItem.Learn.route) {
                LearnScreen(
                    viewModel = viewModel,
                    onNavigateToLesson = { lesson ->
                        // Navigate to lesson screen or start lesson
                        viewModel.startLesson(lesson)
                    }
                )
            }
            
            composable(NavigationItem.Practice.route) {
                PracticeScreen(
                    viewModel = viewModel,
                    onStartPractice = { concepts ->
                        viewModel.startPracticeSession(concepts)
                    }
                )
            }
            
            composable(NavigationItem.Profile.route) {
                ProfileScreen(
                    viewModel = viewModel
                )
            }
            
            // Lesson/Problem solving screen (can be accessed from Learn or Practice)
            composable("lesson_screen") {
                LessonScreen(
                    viewModel = viewModel,
                    onComplete = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
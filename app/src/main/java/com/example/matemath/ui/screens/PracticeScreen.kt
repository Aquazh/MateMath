package com.example.matemath.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matemath.data.*
import com.example.matemath.ui.theme.MateMathColors
import com.example.matemath.viewmodel.MateMathViewModel

@Composable
fun PracticeScreen(
    viewModel: MateMathViewModel,
    onStartPractice: (List<MathConcept>) -> Unit
) {
    val appState by viewModel.appState.collectAsState()
    val userProfile = appState.user
    val recommendedPractice = appState.recommendedPractice
    val conceptMasteries = appState.conceptMasteries
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MateMathColors.Background)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            PracticeScreenHeader(userProfile = userProfile)
        }
        
        // Quick Practice section
        item {
            QuickPracticeSection(
                recommendedConcepts = recommendedPractice,
                onStartQuickPractice = { onStartPractice(recommendedPractice) }
            )
        }
        
        // Recommended Practice section
        if (recommendedPractice.isNotEmpty()) {
            item {
                SectionHeader(
                    title = "Recomendado para ti",
                    subtitle = "Basado en tu rendimiento reciente"
                )
            }
            
            items(recommendedPractice) { concept ->
                val mastery = conceptMasteries[concept]
                PracticeTopicCard(
                    concept = concept,
                    mastery = mastery,
                    onClick = { onStartPractice(listOf(concept)) }
                )
            }
        }
        
        // All Topics section
        item {
            SectionHeader(
                title = "Todos los temas",
                subtitle = "Practica cualquier concepto"
            )
        }
        
        items(MathConcept.values().toList()) { concept ->
            val mastery = conceptMasteries[concept]
            PracticeTopicCard(
                concept = concept,
                mastery = mastery,
                onClick = { onStartPractice(listOf(concept)) },
                showMasteryLevel = true
            )
        }
        
        // Bottom spacing for navigation bar
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun PracticeScreenHeader(
    userProfile: UserProfile
) {
    Column(
        modifier = Modifier.padding(vertical = 20.dp)
    ) {
        Text(
            text = "¡Hora de practicar!",
            style = MaterialTheme.typography.headlineLarge,
            color = MateMathColors.OnSurface,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Daily goal progress
        val dailyGoal = userProfile.preferences.dailyGoal
        val todayXP = userProfile.progress.weeklyXP.lastOrNull() ?: 0
        val progress = (todayXP.toFloat() / dailyGoal).coerceIn(0f, 1f)
        
        DailyGoalProgress(
            currentXP = todayXP,
            goalXP = dailyGoal,
            progress = progress
        )
    }
}

@Composable
fun DailyGoalProgress(
    currentXP: Int,
    goalXP: Int,
    progress: Float
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MateMathColors.SurfaceVariant),
        elevation = CardDefaults.cardElevation(2.dp),
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
                    text = "Meta diaria",
                    style = MaterialTheme.typography.labelLarge,
                    color = MateMathColors.OnSurfaceSecondary
                )
                Text(
                    text = "$currentXP / $goalXP XP",
                    style = MaterialTheme.typography.labelLarge,
                    color = MateMathColors.Primary,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (progress >= 1f) MateMathColors.Success else MateMathColors.Primary,
                trackColor = MateMathColors.ProgressBackground
            )
            
            if (progress >= 1f) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🎉",
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "¡Meta completada!",
                        style = MaterialTheme.typography.labelMedium,
                        color = MateMathColors.Success,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun QuickPracticeSection(
    recommendedConcepts: List<MathConcept>,
    onStartQuickPractice: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onStartQuickPractice() },
        colors = CardDefaults.cardColors(
            containerColor = MateMathColors.Primary
        ),
        elevation = CardDefaults.cardElevation(6.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⚡",
                        fontSize = 24.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Práctica Rápida",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MateMathColors.Surface,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = if (recommendedConcepts.isNotEmpty()) {
                        "Temas recomendados según tu progreso"
                    } else {
                        "Problemas de todos los temas"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MateMathColors.Surface.copy(alpha = 0.9f)
                )
            }
            
            Icon(
                Icons.Default.PlayArrow,
                contentDescription = "Comenzar práctica rápida",
                tint = MateMathColors.Surface,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String
) {
    Column(
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MateMathColors.OnSurface,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MateMathColors.OnSurfaceSecondary
        )
    }
}

@Composable
fun PracticeTopicCard(
    concept: MathConcept,
    mastery: ConceptMastery?,
    onClick: () -> Unit,
    showMasteryLevel: Boolean = false
) {
    val masteryLevel = when {
        mastery == null -> MasteryLevel.NOT_STARTED
        mastery.masteryScore >= 0.9f -> MasteryLevel.MASTERED
        mastery.masteryScore >= 0.7f -> MasteryLevel.PRACTICING
        else -> MasteryLevel.LEARNING
    }
    
    val needsReview = mastery?.needsReview ?: false
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (needsReview) MateMathColors.WarningLight else MateMathColors.Surface
        ),
        elevation = CardDefaults.cardElevation(3.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Concept icon/indicator
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = masteryLevel.progressColor.copy(alpha = 0.2f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                val icon = when (concept) {
                    MathConcept.SINGLE_DIGIT_ADDITION, MathConcept.DOUBLE_DIGIT_ADDITION, MathConcept.ADDITION_WITH_CARRYING -> "➕"
                    MathConcept.SINGLE_DIGIT_SUBTRACTION, MathConcept.DOUBLE_DIGIT_SUBTRACTION, MathConcept.SUBTRACTION_WITH_BORROWING -> "➖"
                    MathConcept.MULTIPLICATION_TABLES -> "✖️"
                    MathConcept.DIVISION_BASICS -> "➗"
                }
                
                Text(
                    text = icon,
                    fontSize = 20.sp
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Concept details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = concept.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        color = MateMathColors.OnSurface,
                        fontWeight = FontWeight.Medium
                    )
                    
                    if (needsReview) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = MateMathColors.Warning,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Repasar",
                                style = MaterialTheme.typography.labelSmall,
                                color = MateMathColors.Surface,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                
                Text(
                    text = concept.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MateMathColors.OnSurfaceSecondary
                )
                
                if (showMasteryLevel || mastery != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Mastery indicator
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    color = masteryLevel.progressColor,
                                    shape = CircleShape
                                )
                        )
                        
                        Spacer(modifier = Modifier.width(6.dp))
                        
                        Text(
                            text = masteryLevel.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            color = MateMathColors.OnSurfaceSecondary
                        )
                        
                        if (mastery != null) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "• ${(mastery.accuracyRate * 100).toInt()}% precisión",
                                style = MaterialTheme.typography.labelSmall,
                                color = MateMathColors.OnSurfaceSecondary
                            )
                        }
                    }
                }
            }
            
            // Practice button icon
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "Practicar",
                tint = MateMathColors.OnSurfaceSecondary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
} 
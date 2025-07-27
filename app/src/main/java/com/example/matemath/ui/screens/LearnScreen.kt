package com.example.matemath.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matemath.data.*
import com.example.matemath.ui.theme.MateMathColors
import com.example.matemath.ui.theme.EducationalTypography
import com.example.matemath.viewmodel.MateMathViewModel

@Composable
fun LearnScreen(
    viewModel: MateMathViewModel,
    onNavigateToLesson: (Lesson) -> Unit
) {
    val appState by viewModel.appState.collectAsState()
    val userProfile = appState.user
    val learningPath = appState.learningPath
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MateMathColors.Background)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header with user info and streak
        item {
            LearnScreenHeader(
                userProfile = userProfile,
                onProfileClick = { /* Navigate to profile */ }
            )
        }
        
        // Continue learning section
        item {
            val nextLesson = AdaptiveLearningEngine.recommendNextLesson(learningPath, userProfile.progress)
            if (nextLesson != null) {
                ContinueLearningCard(
                    lesson = nextLesson,
                    onClick = { onNavigateToLesson(nextLesson) }
                )
            }
        }
        
        // Learning path units
        itemsIndexed(learningPath.units) { index, unit ->
            LearningUnitCard(
                unit = unit,
                index = index,
                onClick = {
                    if (unit.isUnlocked) {
                        viewModel.startUnit(unit)
                    }
                }
            )
        }
        
        // Bottom spacing for navigation bar
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun LearnScreenHeader(
    userProfile: UserProfile,
    onProfileClick: () -> Unit
) {
    Column(
        modifier = Modifier.padding(vertical = 20.dp)
    ) {
        // Greeting and streak
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "¡Hola, ${userProfile.name}!",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MateMathColors.OnSurface,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Nivel ${userProfile.level} • ${userProfile.xpTotal} XP",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MateMathColors.OnSurfaceSecondary
                )
            }
            
            // Streak indicator
            StreakIndicator(streak = userProfile.streak.current)
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // XP Progress bar
        XPProgressBar(
            currentXP = userProfile.xpTotal,
            level = userProfile.level
        )
    }
}

@Composable
fun StreakIndicator(streak: Int) {
    Card(
        modifier = Modifier
            .padding(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (streak > 0) MateMathColors.Success else MateMathColors.SurfaceVariant
        ),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🔥",
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$streak",
                style = MaterialTheme.typography.labelLarge,
                color = if (streak > 0) MateMathColors.Surface else MateMathColors.OnSurfaceSecondary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun XPProgressBar(
    currentXP: Int,
    level: Int
) {
    val xpForCurrentLevel = (level - 1) * 100
    val xpForNextLevel = level * 100
    val progressInLevel = currentXP - xpForCurrentLevel
    val xpNeeded = xpForNextLevel - xpForCurrentLevel
    val progress = (progressInLevel.toFloat() / xpNeeded).coerceIn(0f, 1f)
    
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Nivel $level",
                style = MaterialTheme.typography.labelMedium,
                color = MateMathColors.OnSurfaceSecondary
            )
            Text(
                text = "${xpNeeded - progressInLevel} XP para nivel ${level + 1}",
                style = MaterialTheme.typography.labelMedium,
                color = MateMathColors.OnSurfaceSecondary
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = MateMathColors.Primary,
            trackColor = MateMathColors.ProgressBackground
        )
    }
}

@Composable
fun ContinueLearningCard(
    lesson: Lesson,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MateMathColors.Primary),
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
                Text(
                    text = "Continúa aprendiendo",
                    style = MaterialTheme.typography.labelLarge,
                    color = MateMathColors.Surface.copy(alpha = 0.9f)
                )
                Text(
                    text = lesson.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MateMathColors.Surface,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = lesson.concept.displayName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MateMathColors.Surface.copy(alpha = 0.8f)
                )
            }
            
            Icon(
                Icons.Default.PlayArrow,
                contentDescription = "Continuar",
                tint = MateMathColors.Surface,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun LearningUnitCard(
    unit: LearningUnit,
    index: Int,
    onClick: () -> Unit
) {
    val isEven = index % 2 == 0
    val animatedAlpha by animateFloatAsState(
        targetValue = if (unit.isUnlocked) 1f else 0.5f,
        animationSpec = tween(300),
        label = "unit_alpha"
    )
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isEven) Arrangement.Start else Arrangement.End
    ) {
        Card(
            modifier = Modifier
                .width(280.dp)
                .alpha(animatedAlpha)
                .clickable(enabled = unit.isUnlocked) { onClick() },
            colors = CardDefaults.cardColors(
                containerColor = if (unit.isUnlocked) MateMathColors.Surface else MateMathColors.SurfaceVariant
            ),
            elevation = CardDefaults.cardElevation(if (unit.isUnlocked) 4.dp else 2.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // Unit header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Unit icon
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    color = unit.category.color.copy(alpha = 0.1f),
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = unit.icon,
                                fontSize = 24.sp
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Column {
                            Text(
                                text = unit.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = if (unit.isUnlocked) MateMathColors.OnSurface else MateMathColors.OnSurfaceTertiary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = unit.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (unit.isUnlocked) MateMathColors.OnSurfaceSecondary else MateMathColors.OnSurfaceTertiary
                            )
                        }
                    }
                    
                    // Lock/unlock indicator
                    if (!unit.isUnlocked) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = "Bloqueado",
                            tint = MateMathColors.OnSurfaceTertiary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Progress indicator
                if (unit.isUnlocked) {
                    UnitProgressIndicator(
                        completionPercentage = unit.completionPercentage,
                        xpEarned = unit.xpEarned,
                        targetXP = unit.targetXP
                    )
                } else {
                    Text(
                        text = "Completa la unidad anterior para desbloquear",
                        style = MaterialTheme.typography.bodySmall,
                        color = MateMathColors.OnSurfaceTertiary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
    
    // Connecting line to next unit (except for last unit)
    if (index < 5) { // Assuming max 6 units for now
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(40.dp)
                    .background(
                        color = if (unit.isUnlocked) MateMathColors.Primary else MateMathColors.OnSurfaceTertiary,
                        shape = RoundedCornerShape(2.dp)
                    )
            )
        }
    }
}

@Composable
fun UnitProgressIndicator(
    completionPercentage: Float,
    xpEarned: Int,
    targetXP: Int
) {
    Column {
        // Progress ring/bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${(completionPercentage * 100).toInt()}% completado",
                style = MaterialTheme.typography.labelMedium,
                color = MateMathColors.OnSurfaceSecondary
            )
            Text(
                text = "$xpEarned/$targetXP XP",
                style = MaterialTheme.typography.labelMedium,
                color = MateMathColors.Primary,
                fontWeight = FontWeight.Medium
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Animated progress bar
        val animatedProgress by animateFloatAsState(
            targetValue = completionPercentage,
            animationSpec = tween(800, easing = EaseOutCubic),
            label = "progress"
        )
        
        LinearProgressIndicator(
            progress = animatedProgress,
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = MateMathColors.Primary,
            trackColor = MateMathColors.ProgressBackground
        )
    }
} 
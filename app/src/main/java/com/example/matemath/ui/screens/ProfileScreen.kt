package com.example.matemath.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matemath.data.*
import com.example.matemath.ui.theme.MateMathColors
import com.example.matemath.viewmodel.MateMathViewModel

@Composable
fun ProfileScreen(
    viewModel: MateMathViewModel
) {
    val appState by viewModel.appState.collectAsState()
    val userProfile = appState.user
    val conceptMasteries = appState.conceptMasteries
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MateMathColors.Background)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Profile Header
        item {
            ProfileHeader(userProfile = userProfile)
        }
        
        // Stats Overview
        item {
            StatsOverview(userProfile = userProfile)
        }
        
        // Weekly Progress
        item {
            WeeklyProgressCard(weeklyXP = userProfile.progress.weeklyXP)
        }
        
        // Badges Section
        item {
            BadgesSection(badges = userProfile.badges)
        }
        
        // Mastery Progress
        item {
            MasteryProgressSection(
                conceptMasteries = conceptMasteries,
                masteredConcepts = userProfile.progress.masteredConcepts
            )
        }
        
        // Bottom spacing for navigation bar
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun ProfileHeader(
    userProfile: UserProfile
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MateMathColors.Primary),
        elevation = CardDefaults.cardElevation(6.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        color = MateMathColors.Surface.copy(alpha = 0.2f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userProfile.avatar,
                    fontSize = 40.sp
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Name and level
            Text(
                text = userProfile.name,
                style = MaterialTheme.typography.headlineMedium,
                color = MateMathColors.Surface,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "Nivel ${userProfile.level}",
                style = MaterialTheme.typography.titleMedium,
                color = MateMathColors.Surface.copy(alpha = 0.9f)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // XP Progress
            val xpForCurrentLevel = (userProfile.level - 1) * 100
            val xpForNextLevel = userProfile.level * 100
            val progressInLevel = userProfile.xpTotal - xpForCurrentLevel
            val xpNeeded = xpForNextLevel - xpForCurrentLevel
            val progress = (progressInLevel.toFloat() / xpNeeded).coerceIn(0f, 1f)
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${userProfile.xpTotal} XP total",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MateMathColors.Surface.copy(alpha = 0.9f)
                )
                Text(
                    text = "${xpNeeded - progressInLevel} XP para nivel ${userProfile.level + 1}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MateMathColors.Surface.copy(alpha = 0.9f)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MateMathColors.Surface,
                trackColor = MateMathColors.Surface.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
fun StatsOverview(
    userProfile: UserProfile
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MateMathColors.Surface),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Estadísticas",
                style = MaterialTheme.typography.titleLarge,
                color = MateMathColors.OnSurface,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    icon = "🔥",
                    value = "${userProfile.streak.current}",
                    label = "Racha actual"
                )
                
                StatItem(
                    icon = "🏆",
                    value = "${userProfile.streak.longest}",
                    label = "Mejor racha"
                )
                
                StatItem(
                    icon = "📊",
                    value = "${(userProfile.progress.accuracyRate * 100).toInt()}%",
                    label = "Precisión"
                )
                
                StatItem(
                    icon = "🎯",
                    value = "${userProfile.progress.totalProblemsCompleted}",
                    label = "Problemas"
                )
            }
        }
    }
}

@Composable
fun StatItem(
    icon: String,
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = icon,
            fontSize = 24.sp
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            color = MateMathColors.OnSurface,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MateMathColors.OnSurfaceSecondary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun WeeklyProgressCard(
    weeklyXP: List<Int>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MateMathColors.Surface),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Progreso semanal",
                style = MaterialTheme.typography.titleLarge,
                color = MateMathColors.OnSurface,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val days = listOf("L", "M", "X", "J", "V", "S", "D")
                val maxXP = weeklyXP.maxOrNull() ?: 1
                
                weeklyXP.forEachIndexed { index, xp ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // XP Bar
                        val height = ((xp.toFloat() / maxXP) * 60).dp.coerceAtLeast(4.dp)
                        
                        Box(
                            modifier = Modifier
                                .width(24.dp)
                                .height(60.dp),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(24.dp)
                                    .height(height)
                                    .background(
                                        color = if (xp > 0) MateMathColors.Primary else MateMathColors.ProgressBackground,
                                        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                                    )
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = days[index],
                            style = MaterialTheme.typography.labelMedium,
                            color = MateMathColors.OnSurfaceSecondary
                        )
                        
                        Text(
                            text = "$xp",
                            style = MaterialTheme.typography.labelSmall,
                            color = MateMathColors.OnSurfaceSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BadgesSection(
    badges: List<Badge>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MateMathColors.Surface),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Logros",
                    style = MaterialTheme.typography.titleLarge,
                    color = MateMathColors.OnSurface,
                    fontWeight = FontWeight.SemiBold
                )
                
                Text(
                    text = "${badges.size} desbloqueados",
                    style = MaterialTheme.typography.labelMedium,
                    color = MateMathColors.OnSurfaceSecondary
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (badges.isEmpty()) {
                Text(
                    text = "¡Sigue practicando para desbloquear tus primeros logros!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MateMathColors.OnSurfaceSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(badges) { badge ->
                        BadgeItem(badge = badge)
                    }
                }
            }
        }
    }
}

@Composable
fun BadgeItem(
    badge: Badge
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = badge.rarity.color.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .width(80.dp)
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = badge.icon,
                fontSize = 24.sp
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = badge.name,
                style = MaterialTheme.typography.labelSmall,
                color = MateMathColors.OnSurface,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

@Composable
fun MasteryProgressSection(
    conceptMasteries: Map<MathConcept, ConceptMastery>,
    masteredConcepts: Set<MathConcept>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MateMathColors.Surface),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Progreso por tema",
                style = MaterialTheme.typography.titleLarge,
                color = MateMathColors.OnSurface,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            val allConcepts = MathConcept.values()
            
            allConcepts.forEach { concept ->
                val mastery = conceptMasteries[concept]
                val isMastered = masteredConcepts.contains(concept)
                
                MasteryProgressItem(
                    concept = concept,
                    mastery = mastery,
                    isMastered = isMastered
                )
                
                if (concept != allConcepts.last()) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun MasteryProgressItem(
    concept: MathConcept,
    mastery: ConceptMastery?,
    isMastered: Boolean
) {
    val masteryLevel = when {
        mastery == null -> MasteryLevel.NOT_STARTED
        mastery.masteryScore >= 0.9f -> MasteryLevel.MASTERED
        mastery.masteryScore >= 0.7f -> MasteryLevel.PRACTICING
        else -> MasteryLevel.LEARNING
    }
    
    val progress = mastery?.masteryScore ?: 0f
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Concept icon
        val icon = when (concept) {
            MathConcept.SINGLE_DIGIT_ADDITION, MathConcept.DOUBLE_DIGIT_ADDITION, MathConcept.ADDITION_WITH_CARRYING -> "➕"
            MathConcept.SINGLE_DIGIT_SUBTRACTION, MathConcept.DOUBLE_DIGIT_SUBTRACTION, MathConcept.SUBTRACTION_WITH_BORROWING -> "➖"
            MathConcept.MULTIPLICATION_TABLES -> "✖️"
            MathConcept.DIVISION_BASICS -> "➗"
        }
        
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(
                    color = masteryLevel.progressColor.copy(alpha = 0.2f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = icon,
                fontSize = 16.sp
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = concept.displayName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MateMathColors.OnSurface,
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelMedium,
                    color = MateMathColors.OnSurfaceSecondary
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = masteryLevel.progressColor,
                trackColor = MateMathColors.ProgressBackground
            )
            
            if (mastery != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${mastery.totalAttempts} intentos • ${(mastery.accuracyRate * 100).toInt()}% precisión",
                    style = MaterialTheme.typography.labelSmall,
                    color = MateMathColors.OnSurfaceSecondary
                )
            }
        }
    }
} 
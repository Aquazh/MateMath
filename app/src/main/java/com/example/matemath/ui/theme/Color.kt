package com.example.matemath.ui.theme

import androidx.compose.ui.graphics.Color

// Minimalist Color Palette - Single Primary Color Theme
object MateMathColors {
    
    // Primary Color (Single accent color for the entire app)
    val Primary = Color(0xFF6366F1) // Soft indigo - calming yet engaging
    val PrimaryLight = Color(0xFF818CF8)
    val PrimaryDark = Color(0xFF4F46E5)
    
    // Neutral Background Colors (Off-white/Light gray)
    val Background = Color(0xFFFBFBFB) // Almost white, very light gray
    val Surface = Color(0xFFFFFFFF) // Pure white for cards
    val SurfaceVariant = Color(0xFFF8F9FA) // Slightly gray for secondary surfaces
    
    // Text Colors (High contrast for readability)
    val OnSurface = Color(0xFF1F2937) // Dark gray for primary text
    val OnSurfaceSecondary = Color(0xFF6B7280) // Medium gray for secondary text
    val OnSurfaceTertiary = Color(0xFF9CA3AF) // Light gray for tertiary text
    
    // Success & Error (Minimal, soft colors)
    val Success = Color(0xFF10B981) // Soft green
    val SuccessLight = Color(0xFFD1FAE5) // Very light green background
    val Error = Color(0xFFEF4444) // Soft red
    val ErrorLight = Color(0xFFFEE2E2) // Very light red background
    
    // Warning & Info
    val Warning = Color(0xFFF59E0B) // Soft amber
    val WarningLight = Color(0xFFFEF3C7) // Very light amber background
    val Info = Color(0xFF3B82F6) // Soft blue
    val InfoLight = Color(0xFFDEF0FF) // Very light blue background
    
    // Progress & Gamification Colors (Subtle)
    val Progress = Primary // Use primary color for progress
    val ProgressBackground = Color(0xFFF1F5F9) // Very light gray
    val XP = Color(0xFF8B5CF6) // Soft purple for XP
    val Streak = Color(0xFFFF6B6B) // Soft orange-red for streaks
    val Coins = Color(0xFFFBBF24) // Soft gold for Yerba Coins
    
    // Mastery Level Colors (Soft, pastel-like)
    val MasteryNotStarted = Color(0xFFE5E7EB)
    val MasteryLearning = Color(0xFFFDE68A)
    val MasteryPracticing = Color(0xFFBFDBFE)
    val MasteryMastered = Color(0xFFBBF7D0)
    
    // Transparent overlays
    val Overlay = Color(0x40000000) // 25% black overlay
    val OverlayLight = Color(0x1A000000) // 10% black overlay
}

// Light Theme Colors
val Purple80 = MateMathColors.PrimaryLight
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = MateMathColors.Primary
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)
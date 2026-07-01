package com.example.cutetodo.ui.theme

import androidx.compose.ui.graphics.Color

// ============================================================
//  🌸 LIGHT MODE COLORS (Pinterest Pastel)
// ============================================================
val SoftPink = Color(0xFFFFD6E8)
val SoftLavender = Color(0xFFE6D6FF)
val SoftMint = Color(0xFFD6F5E8)
val SoftYellow = Color(0xFFFFF4D6)
val SoftPeach = Color(0xFFFFE0D6)
val SoftBlue = Color(0xFFD6E8FF)
val PrimaryPurple = Color(0xFFB388FF)
val BackgroundColorLight = Color(0xFFFFF5FB)

val LightGradientColors = listOf(
    Color(0xFFFFEEF8),
    Color(0xFFF3E5FF),
    Color(0xFFE7D8FF),
)

// ============================================================
//  🌙 DARK MODE COLORS (Dreamy Midnight)
// ============================================================
val DarkBackground = Color(0xFF12121E)    // Deeper, richer midnight
val DarkSurface = Color(0xFF1E1E2E)       // Slightly lighter surface
val DarkPrimary = Color(0xFFB48CF2)       // Glowing lavender
val DarkSecondary = Color(0xFFF6C8E0)     // Pastel pink accent
val DarkTextPrimary = Color(0xFFE2E2F0)   // Soft off-white
val DarkTextSecondary = Color(0xFFA0A0C0) // Muted lavender-grey

val DarkGradientColors = listOf(
    Color(0xFF12121E), // Midnight
    Color(0xFF1E122E), // Plum tint
    Color(0xFF121E2E), // Indigo tint
)

// Helper for the cycling grid colors (Light Mode)
val BoxColorsLight = listOf(
    SoftPink, SoftLavender, SoftMint, SoftYellow,
    SoftPeach, SoftBlue, SoftPink, SoftLavender,
)

// Helper for the cycling grid colors (Aesthetic Dark - Jewel Tones)
// These colors are chosen to "pop" softly against the dark background
val BoxColorsDark = listOf(
    Color(0xFF2D2445), // Muted Amethyst
    Color(0xFF452434), // Muted Rose
    Color(0xFF24453A), // Muted Emerald
    Color(0xFF453A24), // Muted Amber
    Color(0xFF243445), // Muted Sapphire
    Color(0xFF3A2445), // Muted Deep Violet
    Color(0xFF2D2445),
    Color(0xFF452434),
)

val CategoryPersonal = Color(0xFFFFD6E8)
val CategoryStudy = Color(0xFFD6E8FF)
val CategoryWork = Color(0xFFE6D6FF)

package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// ==========================================
// CareOsis Medical Brand Identity Palette (Material 3)
// ==========================================

// Primary: Clinical Emerald / Deep Surgical Teal
val MedicalEmeraldPrimary = Color(0xFF006A60)
val MedicalEmeraldOnPrimary = Color(0xFFFFFFFF)
val MedicalEmeraldPrimaryContainer = Color(0xFF73F8E6)
val MedicalEmeraldOnPrimaryContainer = Color(0xFF00201C)
val MedicalEmeraldDark = Color(0xFF003833)
val MedicalEmeraldLight = Color(0xFF4DB6AC)

// Secondary: Clinical Slate / Medical Navy
val MedicalSecondary = Color(0xFF4A635F)
val MedicalOnSecondary = Color(0xFFFFFFFF)
val MedicalSecondaryContainer = Color(0xFFCCE8E2)
val MedicalOnSecondaryContainer = Color(0xFF05201C)

// Tertiary: Medical Amber / Gold Accent
val MedicalTertiary = Color(0xFF9E6C00)
val MedicalOnTertiary = Color(0xFFFFFFFF)
val MedicalTertiaryContainer = Color(0xFFFFDF9E)
val MedicalOnTertiaryContainer = Color(0xFF2B1700)

// Neutral & Clinical Surfaces (Light Theme)
val MedicalBackground = Color(0xFFF8FAF9)
val MedicalOnBackground = Color(0xFF191C1B)
val MedicalSurface = Color(0xFFFFFFFF)
val MedicalOnSurface = Color(0xFF191C1B)
val MedicalSurfaceVariant = Color(0xFFDAE5E2)
val MedicalOnSurfaceVariant = Color(0xFF3F4947)
val MedicalSurfaceContainerLow = Color(0xFFF3F7F5)
val MedicalSurfaceContainer = Color(0xFFEDF3F0)
val MedicalSurfaceContainerHigh = Color(0xFFE5EDE9)
val MedicalOutline = Color(0xFF6F7977)
val MedicalOutlineVariant = Color(0xFFBCC7C4)

// Status & Semantic Clinical Tokens
val StatusSuccess = Color(0xFF1B873F)
val StatusSuccessContainer = Color(0xFFE6F4EA)
val StatusWarning = Color(0xFFD97706)
val StatusWarningContainer = Color(0xFFFEF3C7)
val StatusError = Color(0xFFBA1A1A)
val StatusErrorContainer = Color(0xFFFFDAD6)
val StatusInfo = Color(0xFF006A60)
val StatusInfoContainer = Color(0xFFE0F2F1)
val StatusNeutral = Color(0xFF625B71)
val StatusNeutralContainer = Color(0xFFE8DEF8)

// Dark Theme Medical Tokens
val DarkMedicalPrimary = Color(0xFF53DBC9)
val DarkMedicalOnPrimary = Color(0xFF003731)
val DarkMedicalPrimaryContainer = Color(0xFF005048)
val DarkMedicalOnPrimaryContainer = Color(0xFF73F8E6)
val DarkMedicalSecondary = Color(0xFFB1CCC6)
val DarkMedicalOnSecondary = Color(0xFF1C3531)
val DarkMedicalSecondaryContainer = Color(0xFF334B47)
val DarkMedicalOnSecondaryContainer = Color(0xFFCCE8E2)
val DarkMedicalTertiary = Color(0xFFFFBA39)
val DarkMedicalOnTertiary = Color(0xFF432C00)
val DarkMedicalTertiaryContainer = Color(0xFF604100)
val DarkMedicalOnTertiaryContainer = Color(0xFFFFDF9E)
val DarkMedicalBackground = Color(0xFF101413)
val DarkMedicalOnBackground = Color(0xFFE0E3E1)
val DarkMedicalSurface = Color(0xFF191C1B)
val DarkMedicalOnSurface = Color(0xFFE0E3E1)
val DarkMedicalSurfaceVariant = Color(0xFF3F4947)
val DarkMedicalOnSurfaceVariant = Color(0xFFBEC9C5)
val DarkMedicalOutline = Color(0xFF899390)

// Spending & Chart Category Palette (Recharts / Healthcare Analytics)
val ChartCategoryFuel = Color(0xFF006A60)            // Clinical Emerald
val ChartCategoryTravel = Color(0xFF0284C7)          // Sky / Ocean Blue
val ChartCategoryFood = Color(0xFFD97706)            // Warm Amber
val ChartCategoryHotel = Color(0xFF7C3AED)           // Royal Purple
val ChartCategoryParking = Color(0xFF0D9488)         // Teal
val ChartCategoryDoctorEngagement = Color(0xFFE11D48)// Crimson Rose
val ChartCategoryOther = Color(0xFF64748B)           // Slate Steel

// ==========================================
// Backward Compatibility Mappings
// ==========================================
val GeoBackground = MedicalBackground
val GeoSurface = MedicalSurfaceContainerLow
val GeoSurfaceVariant = MedicalSecondaryContainer
val GeoSurfaceWhite = MedicalSurface
val GeoHeroContainer = MedicalEmeraldPrimaryContainer
val GeoHeroOnContainer = MedicalEmeraldOnPrimaryContainer
val GeoPrimary = MedicalEmeraldPrimary
val GeoOnPrimary = MedicalEmeraldOnPrimary
val GeoSecondary = MedicalSecondary
val GeoSecondaryContainer = MedicalSecondaryContainer
val GeoOnSecondaryContainer = MedicalOnSecondaryContainer
val GeoTextPrimary = MedicalOnSurface
val GeoTextSecondary = MedicalOnSurfaceVariant
val GeoBorder = MedicalOutlineVariant
val GeoBorderLight = MedicalSurfaceContainerLow
val GeoSuccess = StatusSuccess
val GeoSuccessContainer = StatusSuccessContainer
val GeoError = StatusError
val GeoErrorContainer = StatusErrorContainer

val EmeraldPrimary = MedicalEmeraldPrimary
val EmeraldDark = MedicalEmeraldDark
val EmeraldLight = MedicalEmeraldLight
val EmeraldContainer = MedicalEmeraldPrimaryContainer
val OnEmeraldContainer = MedicalEmeraldOnPrimaryContainer

val GoldAccent = MedicalTertiary
val GoldMetallic = MedicalTertiary
val GoldMetallicLight = Color(0xFFFFF4D6)
val GoldDark = Color(0xFF724E00)
val GoldLight = Color(0xFFFFDF9E)
val GoldContainer = MedicalTertiaryContainer
val OnGoldContainer = MedicalOnTertiaryContainer

val ClinicalWhite = Color(0xFFFFFFFF)
val ClinicalSurface = MedicalSurface
val ClinicalSurfaceVariant = MedicalSurfaceVariant
val ClinicalBackground = MedicalBackground
val NeutralTextPrimary = MedicalOnSurface
val NeutralTextSecondary = MedicalOnSurfaceVariant
val NeutralBorder = MedicalOutlineVariant

val DarkEmeraldPrimary = DarkMedicalPrimary
val DarkEmeraldContainer = DarkMedicalPrimaryContainer
val DarkBackground = DarkMedicalBackground
val DarkSurface = DarkMedicalSurface
val DarkSurfaceVariant = DarkMedicalSurfaceVariant
val DarkTextPrimary = DarkMedicalOnSurface
val DarkTextSecondary = DarkMedicalOnSurfaceVariant




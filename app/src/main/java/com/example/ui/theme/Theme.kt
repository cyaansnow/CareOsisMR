package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = DarkMedicalPrimary,
    onPrimary = DarkMedicalOnPrimary,
    primaryContainer = DarkMedicalPrimaryContainer,
    onPrimaryContainer = DarkMedicalOnPrimaryContainer,
    secondary = DarkMedicalSecondary,
    onSecondary = DarkMedicalOnSecondary,
    secondaryContainer = DarkMedicalSecondaryContainer,
    onSecondaryContainer = DarkMedicalOnSecondaryContainer,
    tertiary = DarkMedicalTertiary,
    onTertiary = DarkMedicalOnTertiary,
    tertiaryContainer = DarkMedicalTertiaryContainer,
    onTertiaryContainer = DarkMedicalOnTertiaryContainer,
    background = DarkMedicalBackground,
    onBackground = DarkMedicalOnBackground,
    surface = DarkMedicalSurface,
    onSurface = DarkMedicalOnSurface,
    surfaceVariant = DarkMedicalSurfaceVariant,
    onSurfaceVariant = DarkMedicalOnSurfaceVariant,
    outline = DarkMedicalOutline,
    outlineVariant = DarkMedicalSurfaceVariant
)

private val LightColorScheme = lightColorScheme(
    primary = MedicalEmeraldPrimary,
    onPrimary = MedicalEmeraldOnPrimary,
    primaryContainer = MedicalEmeraldPrimaryContainer,
    onPrimaryContainer = MedicalEmeraldOnPrimaryContainer,
    secondary = MedicalSecondary,
    onSecondary = MedicalOnSecondary,
    secondaryContainer = MedicalSecondaryContainer,
    onSecondaryContainer = MedicalOnSecondaryContainer,
    tertiary = MedicalTertiary,
    onTertiary = MedicalOnTertiary,
    tertiaryContainer = MedicalTertiaryContainer,
    onTertiaryContainer = MedicalOnTertiaryContainer,
    background = MedicalBackground,
    onBackground = MedicalOnBackground,
    surface = MedicalSurface,
    onSurface = MedicalOnSurface,
    surfaceVariant = MedicalSurfaceContainerLow,
    onSurfaceVariant = MedicalOnSurfaceVariant,
    outline = MedicalOutline,
    outlineVariant = MedicalOutlineVariant
)

@Composable
fun CareOsisTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Preserve CareOsis Medical Brand identity
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Backward compatibility alias
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) = CareOsisTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)



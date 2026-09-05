import 'package:flutter/material.dart';

class CareOsisColors {
  static const Color medicalEmeraldPrimary = Color(0xFF006A60);
  static const Color medicalOnPrimary = Color(0xFFFFFFFF);
  static const Color medicalPrimaryContainer = Color(0xFF74F8E5);
  static const Color medicalOnPrimaryContainer = Color(0xFF00201C);

  static const Color medicalSecondary = Color(0xFF4A635F);
  static const Color medicalOnSecondary = Color(0xFFFFFFFF);
  static const Color medicalSecondaryContainer = Color(0xFFCCE8E2);
  static const Color medicalOnSecondaryContainer = Color(0xFF05201C);

  static const Color medicalTertiary = Color(0xFF9E6C00);
  static const Color medicalOnTertiary = Color(0xFFFFFFFF);
  static const Color medicalTertiaryContainer = Color(0xFFFFDF9E);
  static const Color medicalOnTertiaryContainer = Color(0xFF261900);

  static const Color medicalBackground = Color(0xFFFBFDFA);
  static const Color medicalOnBackground = Color(0xFF191C1B);
  static const Color medicalSurface = Color(0xFFFBFDFA);
  static const Color medicalOnSurface = Color(0xFF191C1B);
  static const Color medicalSurfaceVariant = Color(0xFFDAE5E1);
  static const Color medicalOnSurfaceVariant = Color(0xFF3F4946);
  static const Color medicalOutline = Color(0xFF6F7976);

  static const Color goldMetallic = Color(0xFFD4AF37);
  static const Color statusGreen = Color(0xFF00875A);
  static const Color statusOrange = Color(0xFFFF8B00);
  static const Color statusRed = Color(0xFFDE350B);
  static const Color statusBlue = Color(0xFF0052CC);
}

class CareOsisTheme {
  static ThemeData get lightTheme {
    return ThemeData(
      useMaterial3: true,
      colorScheme: ColorScheme.light(
        primary: CareOsisColors.medicalEmeraldPrimary,
        onPrimary: CareOsisColors.medicalOnPrimary,
        primaryContainer: CareOsisColors.medicalPrimaryContainer,
        onPrimaryContainer: CareOsisColors.medicalOnPrimaryContainer,
        secondary: CareOsisColors.medicalSecondary,
        onSecondary: CareOsisColors.medicalOnSecondary,
        secondaryContainer: CareOsisColors.medicalSecondaryContainer,
        onSecondaryContainer: CareOsisColors.medicalOnSecondaryContainer,
        tertiary: CareOsisColors.medicalTertiary,
        onTertiary: CareOsisColors.medicalOnTertiary,
        tertiaryContainer: CareOsisColors.medicalTertiaryContainer,
        onTertiaryContainer: CareOsisColors.medicalOnTertiaryContainer,
        surface: CareOsisColors.medicalSurface,
        onSurface: CareOsisColors.medicalOnSurface,
        outline: CareOsisColors.medicalOutline,
      ),
      appBarTheme: const AppBarTheme(
        backgroundColor: CareOsisColors.medicalEmeraldPrimary,
        foregroundColor: Colors.white,
        elevation: 0,
        centerTitle: false,
      ),
      cardTheme: CardThemeData(
        elevation: 1,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(16),
          side: const BorderSide(color: Color(0xFFE2E8F0)),
        ),
        color: Colors.white,
      ),
      elevatedButtonTheme: ElevatedButtonThemeData(
        style: ElevatedButton.styleFrom(
          backgroundColor: CareOsisColors.medicalEmeraldPrimary,
          foregroundColor: Colors.white,
          elevation: 0,
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(12),
          ),
          padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 14),
          textStyle: const TextStyle(fontWeight: FontWeight.w600, fontSize: 15),
        ),
      ),
    );
  }
}

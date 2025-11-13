package com.example.journal.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

// Use monospace for the entire app
private val AppFontFamily = FontFamily.Monospace

// Create an AppTypography that uses monospace for all text styles
private val AppTypography = Typography(
    displayLarge = Typography().displayLarge.copy(fontFamily = AppFontFamily),
    displayMedium = Typography().displayMedium.copy(fontFamily = AppFontFamily),
    displaySmall = Typography().displaySmall.copy(fontFamily = AppFontFamily),

    headlineLarge = Typography().headlineLarge.copy(fontFamily = AppFontFamily),
    headlineMedium = Typography().headlineMedium.copy(fontFamily = AppFontFamily),
    headlineSmall = Typography().headlineSmall.copy(fontFamily = AppFontFamily),

    titleLarge = Typography().titleLarge.copy(fontFamily = AppFontFamily),
    titleMedium = Typography().titleMedium.copy(fontFamily = AppFontFamily),
    titleSmall = Typography().titleSmall.copy(fontFamily = AppFontFamily),

    bodyLarge = Typography().bodyLarge.copy(fontFamily = AppFontFamily),
    bodyMedium = Typography().bodyMedium.copy(fontFamily = AppFontFamily),
    bodySmall = Typography().bodySmall.copy(fontFamily = AppFontFamily),

    labelLarge = Typography().labelLarge.copy(fontFamily = AppFontFamily),
    labelMedium = Typography().labelMedium.copy(fontFamily = AppFontFamily),
    labelSmall = Typography().labelSmall.copy(fontFamily = AppFontFamily)
)

@Composable
fun JournalTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
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
        typography = AppTypography,
        content = content
    )
}

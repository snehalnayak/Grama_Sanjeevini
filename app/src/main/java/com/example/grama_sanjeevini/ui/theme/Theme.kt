package com.example.grama_sanjeevini.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = MedicalPrimaryDark,
    secondary = MedicalSecondaryDark,
    tertiary = MedicalTertiaryDark,
    error = Color(0xFFF2B8B5)
)

private val LightColorScheme = lightColorScheme(
    primary = MedicalPrimary,
    secondary = MedicalSecondary,
    tertiary = MedicalTertiary,
    background = Color(0xFFF8FDFF),
    surface = Color(0xFFF8FDFF),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    error = EmergencyRed
)

@Composable
fun Grama_SanjeeviniTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set to false to keep our healthcare palette
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
        typography = Typography,
        content = content
    )
}
package com.gaden.checkin.presentation.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = Teal600,
    onPrimary = Neutral10,
    primaryContainer = Teal100,
    onPrimaryContainer = Teal900,
    secondary = Neutral600,
    background = Neutral10,
    surface = Neutral10,
    onSurface = Neutral900,
    surfaceVariant = Neutral50,
    error = StatusAbsentLight,
)

private val DarkColorScheme = darkColorScheme(
    primary = Teal200,
    onPrimary = Teal900,
    primaryContainer = Teal800,
    onPrimaryContainer = Teal100,
    secondary = Neutral200,
    background = Neutral900,
    surface = Neutral900,
    onSurface = Neutral50,
    error = StatusAbsentDark,
)

@Composable
fun CheckinTheme(
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
        typography = Typography,
        content = content
    )
}
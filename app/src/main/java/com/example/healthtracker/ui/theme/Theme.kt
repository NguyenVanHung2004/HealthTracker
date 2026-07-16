package com.example.healthtracker.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryOrange,
    secondary = PrimaryOrangeLight,
    tertiary = PrimaryOrangeDark,
    background = BackgroundDark,
    surface = SurfaceDark,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    primaryContainer = PrimaryOrangeDark,
    onPrimaryContainer = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryOrange,
    secondary = PrimaryOrangeLight,
    tertiary = PrimaryOrangeDark,
    background = BackgroundLight,
    surface = SurfaceLight,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight,
    primaryContainer = PrimaryOrangeLight,
    onPrimaryContainer = PrimaryOrangeDark
)

private val DarkGreenColorScheme = darkColorScheme(
    primary = PrimaryGreenLight,
    secondary = PrimaryGreen,
    tertiary = PrimaryGreenDark,
    background = BackgroundDark,
    surface = SurfaceDark,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    primaryContainer = PrimaryGreenDark,
    onPrimaryContainer = Color.White
)

private val LightGreenColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    secondary = PrimaryGreenLight,
    tertiary = PrimaryGreenDark,
    background = BackgroundLight,
    surface = SurfaceLight,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight,
    primaryContainer = PrimaryGreenLight,
    onPrimaryContainer = PrimaryGreenDark
)

private val DarkBlueColorScheme = darkColorScheme(
    primary = PrimaryBlueLight,
    secondary = PrimaryBlue,
    tertiary = PrimaryBlueDark,
    background = BackgroundDark,
    surface = SurfaceDark,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    primaryContainer = PrimaryBlueDark,
    onPrimaryContainer = Color.White
)

private val LightBlueColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    secondary = PrimaryBlueLight,
    tertiary = PrimaryBlueDark,
    background = BackgroundLight,
    surface = SurfaceLight,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight,
    primaryContainer = PrimaryBlueLight,
    onPrimaryContainer = PrimaryBlueDark
)

private val DarkPurpleColorScheme = darkColorScheme(
    primary = PrimaryPurpleLight,
    secondary = PrimaryPurple,
    tertiary = PrimaryPurpleDark,
    background = BackgroundDark,
    surface = SurfaceDark,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    primaryContainer = PrimaryPurpleDark,
    onPrimaryContainer = Color.White
)

private val LightPurpleColorScheme = lightColorScheme(
    primary = PrimaryPurple,
    secondary = PrimaryPurpleLight,
    tertiary = PrimaryPurpleDark,
    background = BackgroundLight,
    surface = SurfaceLight,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight,
    primaryContainer = PrimaryPurpleLight,
    onPrimaryContainer = PrimaryPurpleDark
)

private val DarkRoseColorScheme = darkColorScheme(
    primary = PrimaryRoseLight,
    secondary = PrimaryRose,
    tertiary = PrimaryRoseDark,
    background = BackgroundDark,
    surface = SurfaceDark,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    primaryContainer = PrimaryRoseDark,
    onPrimaryContainer = Color.White
)

private val LightRoseColorScheme = lightColorScheme(
    primary = PrimaryRose,
    secondary = PrimaryRoseLight,
    tertiary = PrimaryRoseDark,
    background = BackgroundLight,
    surface = SurfaceLight,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight,
    primaryContainer = PrimaryRoseLight,
    onPrimaryContainer = PrimaryRoseDark
)

private val DarkTealColorScheme = darkColorScheme(
    primary = PrimaryTealLight,
    secondary = PrimaryTeal,
    tertiary = PrimaryTealDark,
    background = BackgroundDark,
    surface = SurfaceDark,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    primaryContainer = PrimaryTealDark,
    onPrimaryContainer = Color.White
)

private val LightTealColorScheme = lightColorScheme(
    primary = PrimaryTeal,
    secondary = PrimaryTealLight,
    tertiary = PrimaryTealDark,
    background = BackgroundLight,
    surface = SurfaceLight,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight,
    primaryContainer = PrimaryTealLight,
    onPrimaryContainer = PrimaryTealDark
)

@Composable
fun HealthTrackerTheme(
    themePref: String = "system",
    fontSizePref: String = "medium",
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themePref) {
        "green_light" -> LightGreenColorScheme
        "green_dark" -> DarkGreenColorScheme
        "green" -> if (darkTheme) DarkGreenColorScheme else LightGreenColorScheme
        "blue_light" -> LightBlueColorScheme
        "blue_dark" -> DarkBlueColorScheme
        "blue" -> if (darkTheme) DarkBlueColorScheme else LightBlueColorScheme
        "purple_light" -> LightPurpleColorScheme
        "purple_dark" -> DarkPurpleColorScheme
        "purple" -> if (darkTheme) DarkPurpleColorScheme else LightPurpleColorScheme
        "rose_light" -> LightRoseColorScheme
        "rose_dark" -> DarkRoseColorScheme
        "rose" -> if (darkTheme) DarkRoseColorScheme else LightRoseColorScheme
        "teal_light" -> LightTealColorScheme
        "teal_dark" -> DarkTealColorScheme
        "teal" -> if (darkTheme) DarkTealColorScheme else LightTealColorScheme
        "dark" -> DarkColorScheme
        "light" -> LightColorScheme
        else -> if (darkTheme) DarkColorScheme else LightColorScheme
    }

    val fontScale = when (fontSizePref) {
        "small" -> 0.85f
        "large" -> 1.15f
        else -> 1f
    }

    androidx.compose.runtime.CompositionLocalProvider(
        LocalSpacing provides Spacing()
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = getTypography(fontScale),
            content = content
        )
    }
}
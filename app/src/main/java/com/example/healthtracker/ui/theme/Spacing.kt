package com.example.healthtracker.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Spacing(
    val default: Dp = 0.dp,
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 16.dp,
    val large: Dp = 32.dp,
    val extraLarge: Dp = 64.dp,
    val cornerSmall: Dp = 12.dp,
    val cornerMedium: Dp = 16.dp,
    val cornerLarge: Dp = 24.dp,
    val buttonHeight: Dp = 56.dp,
    val paddingHorizontal: Dp = 32.dp,
    val paddingVertical: Dp = 12.dp
)

val LocalSpacing = compositionLocalOf { Spacing() }

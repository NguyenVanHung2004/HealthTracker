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
    val paddingVertical: Dp = 12.dp,
    val loadingCardSize: Dp = 150.dp,
    val loadingSpinnerSize: Dp = 56.dp,
    val circularProgressSize: Dp = 180.dp,
    val circularProgressStroke: Dp = 12.dp,
    val reportCardWidth: Dp = 360.dp,
    val maxFormWidth: Dp = 640.dp,
    val maxDialogWidth: Dp = 560.dp,
    val minGridCardSize: Dp = 85.dp,
    val maxGridCardHeight: Dp = 96.dp,
    val gridCardIconSize: Dp = 38.dp,
    val borderWidthSelected: Dp = 2.dp,
    val borderWidthUnselected: Dp = 1.dp
)

val LocalSpacing = compositionLocalOf { Spacing() }

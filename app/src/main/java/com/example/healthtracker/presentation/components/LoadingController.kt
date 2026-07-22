package com.example.healthtracker.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.healthtracker.R
import com.example.healthtracker.ui.theme.LocalSpacing

private const val BackgroundDimAlpha: Float = 0.5f
private const val GlassmorphicSurfaceAlpha: Float = 0.85f
private const val BorderAlpha: Float = 0.12f

object LoadingController {
    private var loadingCount by mutableIntStateOf(0)

    val isLoading: Boolean
        get() = loadingCount > 0

    fun show() {
        loadingCount++
    }

    fun hide() {
        if (loadingCount > 0) {
            loadingCount--
        }
    }

    suspend fun <T> withLoading(block: suspend () -> T): T {
        show()
        try {
            return block()
        } finally {
            hide()
        }
    }
}

@Composable
fun PremiumSpinner(
    modifier: Modifier = Modifier,
    primaryColor: Color = MaterialTheme.colorScheme.primary,
    secondaryColor: Color = MaterialTheme.colorScheme.secondary
) {
    val infiniteTransition = rememberInfiniteTransition(label = "premium_spinner_transition")
    
    val angleClockwise by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "clockwise_angle"
    )
    
    val angleCounterClockwise by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "counter_clockwise_angle"
    )

    Canvas(modifier = modifier) {
        val strokeWidth = size.width * 0.08f
        
        // Outer Ring - primary brand color
        drawArc(
            color = primaryColor,
            startAngle = angleClockwise,
            sweepAngle = 280f,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
        
        // Inner Ring - secondary brand color rotating in opposite direction
        val innerPadding = strokeWidth * 2.2f
        drawArc(
            color = secondaryColor,
            startAngle = angleCounterClockwise,
            sweepAngle = 180f,
            useCenter = false,
            style = Stroke(width = strokeWidth * 0.75f, cap = StrokeCap.Round),
            topLeft = androidx.compose.ui.geometry.Offset(innerPadding, innerPadding),
            size = androidx.compose.ui.geometry.Size(
                size.width - innerPadding * 2f,
                size.height - innerPadding * 2f
            )
        )
    }
}

@Composable
fun GlobalLoadingOverlay(
    isLoading: Boolean = LoadingController.isLoading
) {
    val spacing = LocalSpacing.current
    
    val visibleState = remember { MutableTransitionState(false) }
    visibleState.targetState = isLoading

    if (visibleState.currentState || visibleState.targetState) {
        Dialog(
            onDismissRequest = {},
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false
            )
        ) {
            val dialogWindowProvider = LocalView.current.parent as? DialogWindowProvider
            dialogWindowProvider?.window?.setDimAmount(0f)

            AnimatedVisibility(
                visibleState = visibleState,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = BackgroundDimAlpha))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {}
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(spacing.cornerMedium),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = GlassmorphicSurfaceAlpha)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = spacing.small),
                        modifier = Modifier
                            .width(spacing.loadingCardSize)
                            .height(spacing.loadingCardSize)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = BorderAlpha),
                                shape = RoundedCornerShape(spacing.cornerMedium)
                            )
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            PremiumSpinner(
                                modifier = Modifier.size(spacing.loadingSpinnerSize)
                            )
                            Spacer(modifier = Modifier.height(spacing.medium))
                            Text(
                                text = stringResource(R.string.loading),
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

package com.example.healthtracker.presentation.dashboard.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import com.example.healthtracker.ui.theme.LocalSpacing

@Composable
fun CalorieCircularProgress(
    progress: Float,
    color: androidx.compose.ui.graphics.Color,
    netCalories: Int,
    targetCalories: Int,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current

    MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    MaterialTheme.colorScheme.error

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(spacing.loadingCardSize)) {
            val strokeWidth = spacing.cornerSmall.toPx()
            val innerRadius = (size.minDimension - strokeWidth) / 2
            
            // Draw background track
            drawCircle(
                color = trackColor,
                radius = innerRadius,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            
            // Draw progress arc
            val sweepAngle = (progress * 360f).coerceAtMost(360f)
            
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "$netCalories",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "/ $targetCalories kcal",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

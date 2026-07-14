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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.healthtracker.R
import com.example.healthtracker.ui.theme.LocalSpacing

@Composable
fun CalorieCircularProgress(
    target: Int,
    consumed: Int,
    burned: Int,
    remaining: Int,
    isExceeded: Boolean,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    val progress = if (target > 0) {
        (consumed.toFloat() / target.toFloat()).coerceIn(0f, 2f)
    } else 0f

    val primaryColor = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    val exceededColor = MaterialTheme.colorScheme.error

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
            val arcColor = if (isExceeded) exceededColor else primaryColor
            val sweepAngle = (progress * 360f).coerceAtMost(360f)
            
            drawArc(
                color = arcColor,
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
                text = remaining.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = if (isExceeded) exceededColor else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(spacing.extraSmall / 2))
            Text(
                text = stringResource(
                    if (isExceeded) R.string.dashboard_calories_exceeded 
                    else R.string.dashboard_calories_remaining
                ),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

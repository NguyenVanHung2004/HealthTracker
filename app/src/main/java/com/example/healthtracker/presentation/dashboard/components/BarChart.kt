package com.example.healthtracker.presentation.dashboard.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.style.TextAlign
import com.example.healthtracker.presentation.dashboard.BarChartData
import com.example.healthtracker.ui.theme.LocalSpacing

@Composable
fun BarChart(
    data: List<BarChartData>,
    target: Int,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    val maxCal = (data.maxOfOrNull { it.calories } ?: 0f)
        .coerceAtLeast(target.toFloat())
        .coerceAtLeast(1000f)
    val barColor = MaterialTheme.colorScheme.primary
    val targetLineColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)
    val textColor = MaterialTheme.colorScheme.onSurfaceVariant
    val gridColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
    
    Column(modifier = modifier) {
        Canvas(modifier = Modifier
            .fillMaxWidth()
            .height(spacing.loadingCardSize)
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            
            val textHeight = spacing.cornerSmall.toPx()
            val chartBottom = canvasHeight - textHeight
            val chartTop = spacing.medium.toPx()
            val chartHeight = chartBottom - chartTop
            
            // Draw grid lines
            val gridLines = 3
            val gridStroke = (spacing.extraSmall / 4).toPx()
            for (i in 0..gridLines) {
                val y = chartTop + (chartHeight / gridLines) * i
                drawLine(
                    color = gridColor,
                    start = Offset(0f, y),
                    end = Offset(canvasWidth, y),
                    strokeWidth = gridStroke
                )
            }
            
            // Draw target line
            val targetY = chartBottom - (target.toFloat() / maxCal) * chartHeight
            if (targetY in chartTop..chartBottom) {
                val targetStroke = (spacing.extraSmall * 0.375f).toPx()
                val dashLength = spacing.small.toPx()
                drawLine(
                    color = targetLineColor,
                    start = Offset(0f, targetY),
                    end = Offset(canvasWidth, targetY),
                    strokeWidth = targetStroke,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashLength, dashLength), 0f)
                )
            }
            
            // Draw Bars
            val barCount = data.size
            if (barCount > 0) {
                val barWidth = spacing.cornerSmall.toPx()
                val sectionWidth = canvasWidth / barCount
                val barRadius = spacing.extraSmall.toPx()
                
                data.forEachIndexed { index, item ->
                    val x = sectionWidth * index + (sectionWidth - barWidth) / 2
                    val barHeight = (item.calories / maxCal) * chartHeight
                    val y = chartBottom - barHeight
                    
                    if (barHeight > 0) {
                        drawRoundRect(
                            color = barColor,
                            topLeft = Offset(x, y),
                            size = Size(barWidth, barHeight),
                            cornerRadius = CornerRadius(barRadius, barRadius)
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(spacing.extraSmall))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            data.forEach { item ->
                Text(
                    text = item.dayLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = textColor,
                    modifier = Modifier.width(spacing.large + spacing.extraSmall),
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
    }
}

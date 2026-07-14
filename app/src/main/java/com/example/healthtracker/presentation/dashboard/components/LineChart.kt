package com.example.healthtracker.presentation.dashboard.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import com.example.healthtracker.presentation.dashboard.LineChartPoint
import com.example.healthtracker.ui.theme.LocalSpacing

@Composable
fun LineChart(
    data: List<LineChartPoint>,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    val maxVal = (data.flatMap { listOf(it.caloriesConsumed, it.caloriesBurned) }.maxOrNull() ?: 0f)
        .coerceAtLeast(1000f)
    val consumedColor = MaterialTheme.colorScheme.primary
    val burnedColor = MaterialTheme.colorScheme.secondary
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
            
            val pointCount = data.size
            if (pointCount > 1) {
                val sectionWidth = canvasWidth / (pointCount - 1)
                
                val consumedPoints = data.mapIndexed { index, point ->
                    val x = sectionWidth * index
                    val y = chartBottom - (point.caloriesConsumed / maxVal) * chartHeight
                    Offset(x, y)
                }
                
                val burnedPoints = data.mapIndexed { index, point ->
                    val x = sectionWidth * index
                    val y = chartBottom - (point.caloriesBurned / maxVal) * chartHeight
                    Offset(x, y)
                }
                
                fun drawSmoothPath(points: List<Offset>, color: Color) {
                    val path = Path().apply {
                        if (points.isNotEmpty()) {
                            moveTo(points[0].x, points[0].y)
                            for (i in 1 until points.size) {
                                val prev = points[i - 1]
                                val curr = points[i]
                                val controlX = (prev.x + curr.x) / 2
                                cubicTo(controlX, prev.y, controlX, curr.y, curr.x, curr.y)
                            }
                        }
                    }
                    
                    val lineStroke = (spacing.extraSmall * 0.75f).toPx()
                    drawPath(
                        path = path,
                        color = color,
                        style = Stroke(width = lineStroke, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                    
                    val outerRadius = spacing.extraSmall.toPx()
                    val innerRadius = (spacing.extraSmall / 2).toPx()
                    points.forEach { point ->
                        drawCircle(
                            color = color,
                            radius = outerRadius,
                            center = point
                        )
                        drawCircle(
                            color = Color.White,
                            radius = innerRadius,
                            center = point
                        )
                    }
                }
                
                drawSmoothPath(consumedPoints, consumedColor)
                drawSmoothPath(burnedPoints, burnedColor)
            }
        }
        
        Spacer(modifier = Modifier.height(spacing.extraSmall))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            data.forEachIndexed { index, item ->
                Text(
                    text = item.label,
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

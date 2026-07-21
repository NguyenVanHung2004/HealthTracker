package com.example.healthtracker.presentation.settings.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MonitorWeight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.healthtracker.R
import com.example.healthtracker.ui.theme.LocalSpacing

@Composable
fun BmiGaugeCard(bmi: Float, modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current

    val primary = MaterialTheme.colorScheme.primary
    val error = MaterialTheme.colorScheme.error

    val underweightColor = primary.copy(alpha = 0.3f)
    val normalColor = primary.copy(alpha = 0.6f)
    val overweightColor = primary
    val obeseColor = error

    val (categoryRes, statusColor, description) = when {
        bmi < 18.5f -> Triple(
            R.string.bmi_category_underweight,
            underweightColor,
            "BMI < 18.5"
        )
        bmi < 25f -> Triple(
            R.string.bmi_category_normal,
            normalColor,
            "18.5 ≤ BMI < 25"
        )
        bmi < 30f -> Triple(
            R.string.bmi_category_overweight,
            overweightColor,
            "25 ≤ BMI < 30"
        )
        else -> Triple(
            R.string.bmi_category_obese,
            obeseColor,
            "BMI ≥ 30"
        )
    }

    // Normalize BMI to 0..1 range for progress (BMI 10-40 range)
    val progress = ((bmi - 10f) / 30f).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 800),
        label = "bmiProgress"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(spacing.cornerLarge),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = spacing.extraSmall / 2)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.medium)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(spacing.small))
                            .background(statusColor.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.MonitorWeight,
                            contentDescription = null,
                            tint = statusColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(spacing.small))
                    Text(
                        text = stringResource(R.string.your_bmi),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Surface(
                    shape = RoundedCornerShape(spacing.cornerSmall),
                    color = statusColor
                ) {
                    Text(
                        text = stringResource(categoryRes),
                        modifier = Modifier.padding(
                            horizontal = spacing.small + spacing.extraSmall,
                            vertical = spacing.extraSmall
                        ),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.medium))

            // BMI Value
            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.padding(start = spacing.extraSmall)
            ) {
                Text(
                    text = String.format(java.util.Locale.US, "%.1f", bmi),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = statusColor
                )
                Spacer(modifier = Modifier.width(spacing.small))
                Text(
                    text = "kg/m²",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = spacing.extraSmall + 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(spacing.small))

            // Progress bar with gradient
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(spacing.small)
                    .clip(RoundedCornerShape(spacing.extraSmall))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                val widthPx = with(LocalDensity.current) { maxWidth.toPx() }
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(spacing.extraSmall))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(underweightColor, normalColor, overweightColor, obeseColor),
                                startX = 0f,
                                endX = widthPx
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(spacing.small))

            // Scale labels
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.bmi_scale_18_5), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(0.283f))
                Text(stringResource(R.string.bmi_scale_25), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(0.217f))
                Text(stringResource(R.string.bmi_scale_30), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(0.166f))
                Box(modifier = Modifier.weight(0.334f), contentAlignment = Alignment.CenterEnd) {
                    Text(stringResource(R.string.bmi_scale_40), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

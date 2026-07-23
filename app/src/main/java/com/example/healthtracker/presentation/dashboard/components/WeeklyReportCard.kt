package com.example.healthtracker.presentation.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.healthtracker.R
import com.example.healthtracker.domain.model.Goal
import com.example.healthtracker.presentation.dashboard.DashboardUiState
import com.example.healthtracker.ui.theme.LocalSpacing
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun WeeklyReportCard(
    uiState: DashboardUiState,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val todayStr = LocalDate.now().format(dateFormatter)

    Card(
        modifier = modifier
            .width(spacing.reportCardWidth)
            .padding(spacing.medium),
        shape = RoundedCornerShape(spacing.cornerLarge),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = spacing.small)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
                .padding(spacing.large),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = spacing.medium, vertical = spacing.extraSmall)
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(spacing.medium)
                )
                Spacer(modifier = Modifier.width(spacing.small))
                Text(
                    text = stringResource(R.string.report_badge_title),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Spacer(modifier = Modifier.height(spacing.medium))

            Text(
                text = stringResource(R.string.weekly_report_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Text(
                text = stringResource(R.string.report_export_date, todayStr),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(spacing.medium))

            // Main Metrics Surface
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                shape = RoundedCornerShape(spacing.cornerMedium),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(spacing.medium),
                    verticalArrangement = Arrangement.spacedBy(spacing.small)
                ) {
                    MetricRow(
                        icon = Icons.Default.Restaurant,
                        iconTint = MaterialTheme.colorScheme.primary,
                        label = stringResource(R.string.stats_avg_consumed),
                        value = "${uiState.avgCaloriesConsumed.toInt()} ${stringResource(R.string.kcal)}"
                    )

                    MetricRow(
                        icon = Icons.Default.LocalFireDepartment,
                        iconTint = MaterialTheme.colorScheme.error,
                        label = stringResource(R.string.stats_avg_burned),
                        value = "${uiState.avgCaloriesBurned.toInt()} ${stringResource(R.string.kcal)}"
                    )

                    MetricRow(
                        icon = Icons.Default.CheckCircle,
                        iconTint = MaterialTheme.colorScheme.primary,
                        label = stringResource(R.string.stats_days_met),
                        value = stringResource(R.string.stats_days_unit, uiState.daysTargetMet)
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.medium))

            // Target Summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.report_target_calories),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${uiState.targetCalories} ${stringResource(R.string.kcal)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                val goalText = when (uiState.goal) {
                    Goal.LOSE_WEIGHT -> stringResource(R.string.goal_lose_weight)
                    Goal.MAINTAIN_WEIGHT -> stringResource(R.string.goal_maintain)
                    Goal.GAIN_WEIGHT -> stringResource(R.string.goal_gain_weight)
                    Goal.BUILD_MUSCLE -> stringResource(R.string.goal_build_muscle)
                }

                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(spacing.cornerMedium)
                ) {
                    Text(
                        text = goalText,
                        modifier = Modifier.padding(horizontal = spacing.medium, vertical = spacing.extraSmall),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.medium))

            Text(
                text = stringResource(R.string.report_slogan),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun MetricRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    label: String,
    value: String
) {
    val spacing = LocalSpacing.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(spacing.medium)
            )
            Spacer(modifier = Modifier.width(spacing.small))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

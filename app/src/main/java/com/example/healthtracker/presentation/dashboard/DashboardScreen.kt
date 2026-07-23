package com.example.healthtracker.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.healthtracker.R
import com.example.healthtracker.presentation.components.PremiumSpinner
import com.example.healthtracker.presentation.dashboard.components.CalorieCircularProgress
import com.example.healthtracker.presentation.dashboard.components.BarChart
import com.example.healthtracker.domain.model.Goal
import com.example.healthtracker.presentation.dashboard.components.DashboardCard
import com.example.healthtracker.presentation.dashboard.components.LineChart
import com.example.healthtracker.presentation.dashboard.components.ShortcutButton
import com.example.healthtracker.presentation.dashboard.components.TodayExercisesCard
import com.example.healthtracker.presentation.dashboard.components.TodayMealsCard
import com.example.healthtracker.ui.theme.LocalSpacing
import com.example.healthtracker.ui.theme.Spacing
import org.koin.androidx.compose.koinViewModel
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.platform.LocalContext
import com.example.healthtracker.presentation.dashboard.components.WeeklyReportCard
import com.example.healthtracker.presentation.utils.ReportShareUtils

@Composable
fun DashboardScreen(
    onNavigateToMeal: () -> Unit,
    onNavigateToActivity: () -> Unit,
    viewModel: DashboardViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    DashboardScreenContent(
        uiState = uiState,
        onNavigateToMeal = onNavigateToMeal,
        onNavigateToActivity = onNavigateToActivity
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreenContent(
    uiState: DashboardUiState,
    onNavigateToMeal: () -> Unit,
    onNavigateToActivity: () -> Unit
) {
    val spacing = LocalSpacing.current
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val onShareReport = {
        ReportShareUtils.shareComposableAsImage(
            context = context,
            chooserTitle = context.getString(R.string.share_chooser_title)
        ) {
            WeeklyReportCard(uiState = uiState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.tab_dashboard),
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = onShareReport) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = stringResource(R.string.weekly_report_title),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                PremiumSpinner(modifier = Modifier.size(48.dp))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(scrollState)
                    .padding(horizontal = spacing.medium, vertical = spacing.small),
                verticalArrangement = Arrangement.spacedBy(spacing.large)
            ) {
                // 1. Current Date Header
                val dateFormatter = remember {
                    DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy", Locale.getDefault())
                }
                val formattedDate = uiState.selectedDate.format(dateFormatter)
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth()
                )

                // 2. Goal-Based Calorie Card
                DashboardCard(title = stringResource(R.string.daily_nutrition)) {
                    GoalBasedCalorieContent(
                        goal = uiState.goal,
                        targetCalories = uiState.targetCalories,
                        tdee = uiState.tdee,
                        consumed = uiState.consumedCaloriesToday,
                        burned = uiState.burnedCaloriesToday,
                        spacing = spacing
                    )
                }
                // 4. Quick Shortcuts
                Column {
                    Text(
                        text = stringResource(R.string.dashboard_shortcut_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = spacing.small)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing.medium)
                    ) {
                        ShortcutButton(
                            label = stringResource(R.string.dashboard_add_meal),
                            icon = Icons.Default.Restaurant,
                            onClick = onNavigateToMeal,
                            modifier = Modifier.weight(1f)
                        )
                        ShortcutButton(
                            label = stringResource(R.string.dashboard_add_activity),
                            icon = Icons.AutoMirrored.Filled.DirectionsRun,
                            onClick = onNavigateToActivity,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // 4.1 Today's Logs (Meals & Exercises)
                TodayMealsCard(meals = uiState.todayMeals)
                TodayExercisesCard(exercises = uiState.todayExercises)

                // 5. Statistics Charts
                Text(
                    text = stringResource(R.string.stats_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = spacing.small)
                )

                // 5.1 Bar Chart - Last 7 Days Calories
                DashboardCard(title = stringResource(R.string.stats_last_7_days)) {
                    BarChart(
                        data = uiState.last7DaysCalories,
                        target = uiState.targetCalories,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // 5.2 Line Chart - Weekly Trend
                DashboardCard(title = stringResource(R.string.stats_weekly_trend)) {
                    LineChart(
                        data = uiState.weeklyTrend,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(spacing.medium))
                    // Legend for line chart
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(spacing.cornerSmall)
                                .background(MaterialTheme.colorScheme.primary, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(spacing.extraSmall))
                        Text(
                            text = stringResource(R.string.dashboard_consumed),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(spacing.medium))
                        Box(
                            modifier = Modifier
                                .size(spacing.cornerSmall)
                                .background(MaterialTheme.colorScheme.secondary, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(spacing.extraSmall))
                        Text(
                            text = stringResource(R.string.dashboard_burned),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // 5.3 Weekly Summary Card
                DashboardCard(title = stringResource(R.string.stats_weekly_summary)) {
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(R.string.stats_avg_consumed),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${uiState.avgCaloriesConsumed.toInt()} ${stringResource(R.string.kcal)}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(R.string.stats_avg_burned),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${uiState.avgCaloriesBurned.toInt()} ${stringResource(R.string.kcal)}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(R.string.stats_days_met),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = stringResource(R.string.stats_days_unit, uiState.daysTargetMet),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(spacing.small))

                        OutlinedButton(
                            onClick = onShareReport,
                            modifier = Modifier.fillMaxWidth(),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(spacing.cornerMedium),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                modifier = Modifier.size(spacing.medium)
                            )
                            Spacer(modifier = Modifier.width(spacing.small))
                            Text(
                                text = stringResource(R.string.weekly_report_title),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(spacing.extraLarge))
            }
        }
    }
}

@Composable
fun GoalBasedCalorieContent(
    goal: Goal,
    targetCalories: Int,
    tdee: Int,
    consumed: Int,
    burned: Int,
    spacing: Spacing
) {
    val netCalories = consumed - burned
    val isOver = netCalories > targetCalories
    val isUnder = netCalories < targetCalories

    val progress = if (targetCalories > 0) {
        minOf(1f, netCalories.toFloat() / targetCalories.toFloat()).coerceAtLeast(0f)
    } else 0f

    val color = when (goal) {
        Goal.LOSE_WEIGHT, Goal.MAINTAIN_WEIGHT -> {
            if (isOver) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
        }
        Goal.GAIN_WEIGHT, Goal.BUILD_MUSCLE -> {
            if (isUnder) androidx.compose.ui.graphics.Color(0xFFFFA000) // Orange
            else MaterialTheme.colorScheme.primary
        }
    }

    val message = when (goal) {
        Goal.LOSE_WEIGHT -> {
            if (isOver) stringResource(R.string.dash_msg_lose_bad, netCalories - targetCalories)
            else stringResource(R.string.dash_msg_lose_good)
        }
        Goal.MAINTAIN_WEIGHT -> {
            if (netCalories > targetCalories + 100) stringResource(R.string.dash_msg_maintain_bad)
            else stringResource(R.string.dash_msg_maintain_good)
        }
        Goal.GAIN_WEIGHT -> {
            if (isUnder) stringResource(R.string.dash_msg_gain_bad, targetCalories - netCalories)
            else stringResource(R.string.dash_msg_gain_good)
        }
        Goal.BUILD_MUSCLE -> {
            if (isUnder) stringResource(R.string.dash_msg_gain_bad, targetCalories - netCalories)
            else stringResource(R.string.dash_msg_muscle_good)
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = color,
            fontWeight = FontWeight.Medium,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(horizontal = spacing.medium)
        )
        
        Spacer(modifier = Modifier.height(spacing.large))

        CalorieCircularProgress(
            progress = progress,
            color = color,
            netCalories = netCalories,
            targetCalories = targetCalories,
            modifier = Modifier.size(spacing.circularProgressSize)
        )

        Spacer(modifier = Modifier.height(spacing.extraLarge))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$consumed",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = stringResource(R.string.dashboard_consumed),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$burned",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
                Text(
                    text = stringResource(R.string.dashboard_burned),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$tdee",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = stringResource(R.string.dash_label_tdee),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

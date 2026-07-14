package com.example.healthtracker.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.healthtracker.R
import com.example.healthtracker.presentation.dashboard.components.*
import com.example.healthtracker.ui.theme.LocalSpacing
import org.koin.androidx.compose.koinViewModel
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToMeal: () -> Unit,
    onNavigateToActivity: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val spacing = LocalSpacing.current
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.tab_dashboard),
                        fontWeight = FontWeight.Bold
                    )
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
                CircularProgressIndicator()
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

                // 2. Circular Progress Card
                DashboardCard(title = stringResource(R.string.daily_nutrition)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CalorieCircularProgress(
                            target = uiState.targetCalories,
                            consumed = uiState.consumedCaloriesToday,
                            burned = uiState.burnedCaloriesToday,
                            remaining = uiState.remainingCaloriesToday,
                            isExceeded = uiState.isRemainingExceeded,
                            modifier = Modifier.weight(1.2f)
                        )

                        Spacer(modifier = Modifier.width(spacing.medium))

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(spacing.small)
                        ) {
                            CalorieStatItem(
                                label = stringResource(R.string.dashboard_target),
                                calories = uiState.targetCalories,
                                color = MaterialTheme.colorScheme.primary
                            )
                            CalorieStatItem(
                                label = stringResource(R.string.dashboard_consumed),
                                calories = uiState.consumedCaloriesToday,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            CalorieStatItem(
                                label = stringResource(R.string.dashboard_burned),
                                calories = uiState.burnedCaloriesToday,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                // 3. Summary & Advice Card
                DashboardCard(title = stringResource(R.string.dashboard_summary)) {
                    val netCalories = uiState.consumedCaloriesToday - uiState.burnedCaloriesToday
                    
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(R.string.dashboard_net_calories),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "$netCalories ${stringResource(R.string.kcal)}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        val adviceText = when (uiState.adviceType) {
                            AdviceType.UNDER_TARGET -> stringResource(
                                R.string.dashboard_advice_under_target,
                                uiState.adviceDiffCalories
                            )
                            AdviceType.TARGET_MET -> stringResource(R.string.dashboard_advice_target_met)
                            AdviceType.OVER_TARGET -> stringResource(
                                R.string.dashboard_advice_over_target,
                                uiState.adviceDiffCalories
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(spacing.cornerSmall)
                                )
                                .padding(spacing.medium)
                        ) {
                            Text(
                                text = adviceText,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
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
                    }
                }

                Spacer(modifier = Modifier.height(spacing.extraLarge))
            }
        }
    }
}

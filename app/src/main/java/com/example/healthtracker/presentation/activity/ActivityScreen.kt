package com.example.healthtracker.presentation.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.healthtracker.R
import com.example.healthtracker.domain.model.ExerciseLog
import com.example.healthtracker.presentation.components.SnackbarController
import com.example.healthtracker.ui.theme.*
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate
import java.util.Locale
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.compose.ui.Alignment
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters.firstDayOfMonth
import java.time.temporal.TemporalAdjusters.lastDayOfMonth
import java.time.temporal.TemporalAdjusters.nextOrSame
import java.time.temporal.TemporalAdjusters.previousOrSame


@Composable
fun ActivityScreen(
    viewModel: ActivityViewModel = koinViewModel(),
    onNavigateToAdd: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is ActivityUiEvent.ShowSuccess -> {
                    SnackbarController.showSnackbar(
                        message = context.getString(event.messageId),
                        isError = false
                    )
                }
                is ActivityUiEvent.ShowError -> {
                    SnackbarController.showSnackbar(
                        message = context.getString(event.messageId),
                        isError = true
                    )
                }
                else -> {}
            }
        }
    }

    ActivityScreenContent(
        uiState = uiState,
        onNavigateToAdd = onNavigateToAdd,
        onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityScreenContent(
    uiState: ActivityUiState,
    onNavigateToAdd: () -> Unit,
    onEvent: (ActivityEvent) -> Unit
) {
    val spacing = LocalSpacing.current
    var showDatePicker by remember { mutableStateOf(false) }

    val dateRangeText = remember(uiState.filterType, uiState.selectedDate, uiState.customStartDate, uiState.customEndDate) {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val shortFormatter = DateTimeFormatter.ofPattern("dd/MM")
        val today = LocalDate.now()
        when (uiState.filterType) {
            DateFilterType.TODAY -> today.format(formatter)
            DateFilterType.WEEK -> {
                val monday = today.with(previousOrSame(java.time.DayOfWeek.MONDAY))
                val sunday = today.with(nextOrSame(java.time.DayOfWeek.SUNDAY))
                "${monday.format(shortFormatter)} - ${sunday.format(shortFormatter)}"
            }
            DateFilterType.MONTH -> {
                val start = today.with(firstDayOfMonth())
                val end = today.with(lastDayOfMonth())
                "${start.format(shortFormatter)} - ${end.format(shortFormatter)}"
            }
            DateFilterType.CUSTOM -> {
                if (uiState.customStartDate.isEqual(uiState.customEndDate)) {
                    uiState.customStartDate.format(formatter)
                } else {
                    "${uiState.customStartDate.format(shortFormatter)} - ${uiState.customEndDate.format(shortFormatter)}"
                }
            }
        }
    }

    if (showDatePicker) {
        val dateRangePickerState = rememberDateRangePickerState(
            initialSelectedStartDateMillis = uiState.customStartDate
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli(),
            initialSelectedEndDateMillis = uiState.customEndDate
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },

            modifier = Modifier.padding(horizontal = spacing.medium),

            confirmButton = {
                TextButton(
                    onClick = {
                        val startMillis = dateRangePickerState.selectedStartDateMillis
                        val endMillis = dateRangePickerState.selectedEndDateMillis
                        if (startMillis != null) {
                            val startDate = Instant.ofEpochMilli(startMillis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            val endDate = if (endMillis != null) {
                                Instant.ofEpochMilli(endMillis)
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate()
                            } else {
                                startDate
                            }
                            onEvent(ActivityEvent.OnCustomRangeSelected(startDate, endDate))
                        }
                        showDatePicker = false
                    }
                ) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        ) {
            DateRangePicker(
                state = dateRangePickerState,
                title = {
                    Text(
                        text = stringResource(R.string.filter_custom),
                        modifier = Modifier.padding(spacing.medium),
                        fontWeight = FontWeight.Bold
                    )
                },
                headline = {
                    val startStr = dateRangePickerState.selectedStartDateMillis?.let {
                        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    } ?: "..."
                    val endStr = dateRangePickerState.selectedEndDateMillis?.let {
                        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    } ?: "..."
                    Text(
                        text = "$startStr - $endStr",
                        modifier = Modifier.padding(horizontal = spacing.medium),
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                modifier = Modifier.weight(1f)
            )
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.tab_activity),
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAdd,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(spacing.cornerLarge)
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_activity))
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Selected Filter Summary
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.medium, vertical = spacing.small),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(spacing.small),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = when (uiState.filterType) {
                            DateFilterType.TODAY -> stringResource(R.string.filter_today)
                            DateFilterType.WEEK -> stringResource(R.string.filter_week)
                            DateFilterType.MONTH -> stringResource(R.string.filter_month)
                            DateFilterType.CUSTOM -> stringResource(R.string.filter_custom)
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = dateRangeText,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Segmented Filter Pills Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.medium)
                    .padding(bottom = spacing.medium),
                horizontalArrangement = Arrangement.spacedBy(spacing.small)
            ) {
                val filters = listOf(
                    DateFilterType.TODAY to R.string.filter_today,
                    DateFilterType.WEEK to R.string.filter_week,
                    DateFilterType.MONTH to R.string.filter_month,
                    DateFilterType.CUSTOM to R.string.filter_custom
                )

                filters.forEach { (type, labelRes) ->
                    val isSelected = uiState.filterType == type
                    val containerColor = if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    }
                    val contentColor = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .background(containerColor, RoundedCornerShape(spacing.cornerSmall))
                            .clickable {
                                if (type == DateFilterType.CUSTOM) {
                                    showDatePicker = true
                                } else {
                                    onEvent(ActivityEvent.OnFilterTypeChanged(type))
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(labelRes),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = contentColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(spacing.small))

            // Calorie summary row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.medium)
                    .padding(bottom = spacing.medium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = null,
                    tint = CalorieRed,
                    modifier = Modifier.size(spacing.medium + spacing.extraSmall * 3)
                )
                Spacer(modifier = Modifier.width(spacing.small))
                Text(
                    text = "${uiState.totalCaloriesBurned} ${stringResource(R.string.kcal)} ${stringResource(R.string.total_calories_burned)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(spacing.medium))

            // Exercise List Content
            if (uiState.exercises.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_activities_yet),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                val groupedExercises = remember(uiState.exercises) {
                    uiState.exercises.groupBy { it.date }.toSortedMap(compareByDescending { it })
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentPadding = PaddingValues(
                        start = spacing.medium,
                        end = spacing.medium,
                        bottom = spacing.extraLarge + spacing.extraLarge
                    ),
                    verticalArrangement = Arrangement.spacedBy(spacing.medium)
                ) {
                    groupedExercises.forEach { (date, exercisesForDate) ->
                        item(key = date.toString()) {
                            val today = LocalDate.now()
                            val dateLabel = if (date.isEqual(today)) {
                                stringResource(R.string.filter_today)
                            } else {
                                val formatter = remember { DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy", Locale.getDefault()) }
                                date.format(formatter)
                            }

                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(spacing.cornerSmall),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = spacing.small)
                            ) {
                                Text(
                                    text = dateLabel,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = spacing.medium, vertical = spacing.small)
                                )
                            }
                        }

                        items(exercisesForDate, key = { it.id }) { exercise ->
                            ExerciseItem(
                                exercise = exercise,
                                onDelete = { onEvent(ActivityEvent.OnDeleteExercise(exercise)) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExerciseItem(
    exercise: ExerciseLog,
    onDelete: () -> Unit
) {
    val spacing = LocalSpacing.current
    val themeGradients = getThemeExerciseGradients()
    val bgGradient = themeGradients[exercise.type.ordinal % themeGradients.size]
    val progress = minOf(1f, exercise.durationMinutes / 60f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(spacing.cornerLarge),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = spacing.extraSmall / 2)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(exercise.type.nameRes),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(spacing.extraSmall))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.extraSmall)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        modifier = Modifier.size(spacing.medium),
                        tint = CalorieRed
                    )
                    Text(
                        text = "${exercise.caloriesBurned} ${stringResource(R.string.kcal)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(spacing.extraSmall / 2))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.extraSmall)
                ) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        modifier = Modifier.size(spacing.medium),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${exercise.durationMinutes} ${stringResource(R.string.minutes)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(spacing.small))
                Text(
                    text = "${exercise.durationMinutes}/60m",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(spacing.extraSmall / 2))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .width(spacing.extraLarge + spacing.medium)
                        .height(spacing.extraSmall),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    strokeCap = StrokeCap.Round
                )
            }

            Spacer(modifier = Modifier.width(spacing.medium))

            Box(
                modifier = Modifier
                    .width(spacing.extraLarge * 2)
                    .height(spacing.extraLarge + spacing.large)
                    .background(bgGradient, RoundedCornerShape(spacing.cornerMedium)),
                contentAlignment = Alignment.Center
            ) {
                val meta = exerciseMeta[exercise.type]
                Icon(
                    imageVector = meta?.icon ?: Icons.Default.FitnessCenter,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.35f),
                    modifier = Modifier.size(spacing.extraLarge + spacing.small)
                )

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(spacing.large)
                        .padding(spacing.extraSmall)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.delete),
                        tint = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        modifier = Modifier.size(spacing.medium)
                    )
                }
            }
        }
    }
}

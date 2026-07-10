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
import androidx.compose.ui.Alignment
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


@Composable
fun ActivityScreen(
    viewModel: ActivityViewModel = koinViewModel(),
    onNavigateToAdd: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
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
        onDeleteExercise = viewModel::deleteExercise
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityScreenContent(
    uiState: ActivityUiState,
    onNavigateToAdd: () -> Unit,
    onDeleteExercise: (ExerciseLog) -> Unit
) {
    val spacing = LocalSpacing.current

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
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
            Text(
                text = stringResource(R.string.tab_activity),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(
                    horizontal = spacing.large,
                    vertical = spacing.medium
                )
            )

            // Calorie summary row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.large)
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
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentPadding = PaddingValues(
                        start = spacing.large,
                        end = spacing.large,
                        bottom = spacing.extraLarge + spacing.extraLarge
                    ),
                    verticalArrangement = Arrangement.spacedBy(spacing.medium)
                ) {
                    items(uiState.exercises) { exercise ->
                        ExerciseItem(
                            exercise = exercise,
                            onDelete = { onDeleteExercise(exercise) }
                        )
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
    val bgGradient = exerciseGradients[exercise.type.ordinal % exerciseGradients.size]
    // Progress: 60 min = 100%, cap at 1f
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
            // Left: info + progress
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
                // Progress label
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

            // Right: gradient box + delete button
            Box(
                modifier = Modifier
                    .width(spacing.extraLarge * 2)
                    .height(spacing.extraLarge + spacing.large)
                    .background(bgGradient, RoundedCornerShape(spacing.cornerMedium)),
                contentAlignment = Alignment.Center
            ) {
                // Large centered exercise icon
                val meta = exerciseMeta[exercise.type]
                Icon(
                    imageVector = meta?.icon ?: Icons.Default.FitnessCenter,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.35f),
                    modifier = Modifier.size(spacing.extraLarge + spacing.small)
                )

                // Delete button in top end
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

package com.example.healthtracker.presentation.activity

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.example.healthtracker.R
import com.example.healthtracker.domain.model.ExerciseType
import com.example.healthtracker.ui.theme.*
import com.example.healthtracker.presentation.components.SnackbarController
import org.koin.androidx.compose.koinViewModel



@Composable
fun AddExerciseScreen(
    viewModel: ActivityViewModel = koinViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is ActivityUiEvent.ShowError -> {
                    SnackbarController.showSnackbar(
                        message = context.getString(event.messageId),
                        isError = true
                    )
                }
                is ActivityUiEvent.ShowSuccess -> {
                    SnackbarController.showSnackbar(
                        message = context.getString(event.messageId),
                        isError = false
                    )
                }
                is ActivityUiEvent.NavigateBack -> {
                    onNavigateBack()
                }
            }
        }
    }

    AddExerciseScreenContent(
        selectedType    = uiState.selectedExerciseType,
        durationInput   = uiState.durationInput,
        onTypeSelected  = viewModel::selectExerciseType,
        onDurationChange = viewModel::setDurationInput,
        onConfirm = viewModel::addExercise,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExerciseScreenContent(
    selectedType: ExerciseType?,
    durationInput: String,
    onTypeSelected: (ExerciseType) -> Unit,
    onDurationChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val spacing = LocalSpacing.current

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.add_activity),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = spacing.large)
        ) {
            // Selected activity preview banner
            if (selectedType != null) {
                SelectedActivityBanner(
                    type = selectedType,
                    modifier = Modifier.padding(bottom = spacing.medium)
                )
            } else {
                // Section header when nothing selected
                Text(
                    text = stringResource(R.string.select_activity),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = spacing.small)
                )
            }

            // Activity grid – fills remaining space above bottom controls
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(spacing.small),
                verticalArrangement = Arrangement.spacedBy(spacing.small),
                modifier = Modifier.weight(1f)
            ) {
                items(ExerciseType.values().toList()) { type ->
                    ActivityTypeCard(
                        type = type,
                        isSelected = type == selectedType,
                        onClick = { onTypeSelected(type) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.medium))

            // Duration label + quick-preset chips + text field
            Text(
                text = stringResource(R.string.duration_minutes),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = spacing.small)
            )

            DurationInputRow(
                durationInput = durationInput,
                onDurationChange = onDurationChange
            )

            Spacer(modifier = Modifier.height(spacing.medium))

            // Confirm button – always enabled; validation shown via snackbar
            Button(
                onClick = onConfirm,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(spacing.buttonHeight),
                shape = RoundedCornerShape(spacing.cornerLarge),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(spacing.medium)
                )
                Spacer(modifier = Modifier.width(spacing.small))
                Text(
                    text = stringResource(R.string.add),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(spacing.medium))
        }
    }
}

@Composable
private fun SelectedActivityBanner(
    type: ExerciseType,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    val meta = exerciseMeta[type]
    val gradient = exerciseGradients[(meta?.gradientIndex ?: 0) % exerciseGradients.size]
    val displayName = stringResource(type.nameRes)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(spacing.extraLarge + spacing.large)
            .clip(RoundedCornerShape(spacing.cornerLarge))
            .background(gradient),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            Icon(
                imageVector = meta?.icon ?: Icons.Default.FitnessCenter,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(spacing.large)
            )
            Text(
                text = displayName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun ActivityTypeCard(
    type: ExerciseType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val spacing = LocalSpacing.current
    val meta = exerciseMeta[type]
    val gradient = exerciseGradients[(meta?.gradientIndex ?: 0) % exerciseGradients.size]
    val displayName = stringResource(type.nameRes)

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = tween(150),
        label = "card_scale"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
        animationSpec = tween(150),
        label = "border_color"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.85f)
            .scale(scale)
            .border(
                width = if (isSelected) spacing.extraSmall / 2 else spacing.default,
                color = borderColor,
                shape = RoundedCornerShape(spacing.cornerMedium)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(spacing.cornerMedium),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) spacing.extraSmall else spacing.extraSmall / 4
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(spacing.extraSmall),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Circle icon with gradient when selected, surfaceVariant when not
            Box(
                modifier = Modifier
                    .size(spacing.extraLarge)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) gradient
                        else Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.surfaceVariant,
                                MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = meta?.icon ?: Icons.Default.FitnessCenter,
                    contentDescription = displayName,
                    tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(spacing.medium + spacing.extraSmall)
                )
            }

            Spacer(modifier = Modifier.height(spacing.extraSmall))

            Text(
                text = displayName,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun DurationInputRow(
    durationInput: String,
    onDurationChange: (String) -> Unit
) {
    val spacing = LocalSpacing.current
    val presets = listOf(15, 30, 45, 60)

    Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
        // Quick preset chips
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing.small),
            modifier = Modifier.fillMaxWidth()
        ) {
            presets.forEach { minutes ->
                val isSelected = durationInput == minutes.toString()
                FilterChip(
                    selected = isSelected,
                    onClick = { onDurationChange(minutes.toString()) },
                    label = {
                        Text(
                            text = "${minutes}m",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    shape = RoundedCornerShape(spacing.cornerMedium)
                )
            }
        }

        // Manual text input
        OutlinedTextField(
            value = durationInput,
            onValueChange = onDurationChange,
            placeholder = { Text(stringResource(R.string.duration_minutes)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            trailingIcon = {
                if (durationInput.isNotEmpty()) {
                    Text(
                        text = stringResource(R.string.minutes),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(end = spacing.small)
                    )
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(spacing.cornerMedium),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )
    }
}

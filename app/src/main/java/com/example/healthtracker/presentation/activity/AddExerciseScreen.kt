package com.example.healthtracker.presentation.activity

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.healthtracker.R
import com.example.healthtracker.domain.model.ExerciseType
import com.example.healthtracker.presentation.components.SnackbarController
import com.example.healthtracker.ui.theme.LocalSpacing
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
        onEvent         = viewModel::onEvent,
        onNavigateBack  = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExerciseScreenContent(
    selectedType: ExerciseType?,
    durationInput: String,
    onEvent: (ActivityEvent) -> Unit,
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = spacing.maxFormWidth)
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

                // Activity grid – adaptive layout
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = spacing.minGridCardSize),
                    contentPadding = PaddingValues(spacing.small),
                    horizontalArrangement = Arrangement.spacedBy(spacing.small),
                    verticalArrangement = Arrangement.spacedBy(spacing.small),
                    modifier = Modifier.weight(1f)
                ) {
                    items(ExerciseType.entries) { type ->
                        ActivityTypeCard(
                            type = type,
                            isSelected = type == selectedType,
                            onClick = { onEvent(ActivityEvent.OnExerciseTypeSelected(type)) }
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
                    onDurationChange = { onEvent(ActivityEvent.OnDurationInputChanged(it)) }
                )

                Spacer(modifier = Modifier.height(spacing.medium))

                // Confirm button – always enabled; validation shown via snackbar
                Button(
                    onClick = { onEvent(ActivityEvent.OnAddExercise) },
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
}

@Composable
private fun SelectedActivityBanner(
    type: ExerciseType,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    val meta = exerciseMeta[type]
    val themeGradients = getThemeExerciseGradients()
    val gradient = themeGradients[(meta?.gradientIndex ?: 0) % themeGradients.size]
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
    val themeGradients = getThemeExerciseGradients()
    val gradient = themeGradients[(meta?.gradientIndex ?: 0) % themeGradients.size]
    val displayName = stringResource(type.nameRes)

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.04f else 1f,
        animationSpec = tween(150),
        label = "card_scale"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
        animationSpec = tween(150),
        label = "border_color"
    )
    val containerColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(spacing.maxGridCardHeight)
            .scale(scale)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(spacing.cornerMedium),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(
            width = if (isSelected) spacing.borderWidthSelected else spacing.borderWidthUnselected,
            color = borderColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) spacing.extraSmall / 2 else spacing.default
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(spacing.extraSmall),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(spacing.gridCardIconSize)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) gradient
                        else Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = meta?.icon ?: Icons.Default.FitnessCenter,
                    contentDescription = displayName,
                    tint = if (isSelected) Color.White else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(spacing.medium + spacing.extraSmall)
                )
            }

            Spacer(modifier = Modifier.height(spacing.extraSmall))

            Text(
                text = displayName,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 1,
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

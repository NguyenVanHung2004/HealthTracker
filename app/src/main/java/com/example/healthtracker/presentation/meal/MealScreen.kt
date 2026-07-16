package com.example.healthtracker.presentation.meal

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.healthtracker.R
import com.example.healthtracker.domain.model.FoodItem
import com.example.healthtracker.domain.model.Goal
import com.example.healthtracker.domain.model.MealLog
import com.example.healthtracker.domain.model.MealType
import com.example.healthtracker.presentation.components.SnackbarController
import com.example.healthtracker.ui.theme.*
import org.koin.androidx.compose.koinViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun MealScreen(
    viewModel: MealViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is MealUiEvent.ShowSuccess -> {
                    SnackbarController.showSnackbar(
                        message = context.getString(event.messageId),
                        isError = false
                    )
                }
                is MealUiEvent.ShowError -> {
                    SnackbarController.showSnackbar(
                        message = context.getString(event.messageId),
                        isError = true
                    )
                }
            }
        }
    }

    MealScreenContent(
        uiState = uiState,
        onDateChange = viewModel::setDate,
        onAddFoodClicked = viewModel::openAddFoodDialog,
        onDeleteFoodClicked = viewModel::deleteMealLog,
        onSearchQueryChange = viewModel::setSearchQuery,
        onFoodSelected = viewModel::selectFoodItem,
        onQuantityChange = viewModel::updateQuantityInput,
        onCustomFoodModeChanged = viewModel::setCustomFoodMode,
        onCustomNameChange = viewModel::updateCustomFoodName,
        onCustomCaloriesChange = viewModel::updateCustomCalories,
        onCustomServingChange = viewModel::updateCustomServingInfo,
        onConfirmAddFood = viewModel::addMealLog,
        onDismissDialog = viewModel::closeAddFoodDialog
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealScreenContent(
    uiState: MealUiState,
    onDateChange: (LocalDate) -> Unit,
    onAddFoodClicked: (MealType) -> Unit,
    onDeleteFoodClicked: (MealLog) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onFoodSelected: (FoodItem) -> Unit,
    onQuantityChange: (String) -> Unit,
    onCustomFoodModeChanged: (Boolean) -> Unit,
    onCustomNameChange: (String) -> Unit,
    onCustomCaloriesChange: (String) -> Unit,
    onCustomServingChange: (String) -> Unit,
    onConfirmAddFood: () -> Unit,
    onDismissDialog: () -> Unit
) {
    val spacing = LocalSpacing.current
    val context = LocalContext.current
    var showDatePicker by remember { mutableStateOf(false) }

    val formattedDate = remember(uiState.selectedDate) {
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)
        when {
            uiState.selectedDate.isEqual(today) -> context.getString(R.string.filter_today)
            uiState.selectedDate.isEqual(yesterday) -> context.getString(R.string.filter_yesterday)
            else -> {
                val formatter = DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy", Locale.getDefault())
                uiState.selectedDate.format(formatter)
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = uiState.selectedDate
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val millis = datePickerState.selectedDateMillis
                        if (millis != null) {
                            val date = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                            onDateChange(date)
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
            DatePicker(state = datePickerState)
        }
    }

    if (uiState.isAddFoodDialogVisible) {
        AddFoodDialog(
            uiState = uiState,
            onSearchQueryChange = onSearchQueryChange,
            onFoodSelected = onFoodSelected,
            onQuantityChange = onQuantityChange,
            onCustomFoodModeChanged = onCustomFoodModeChanged,
            onCustomNameChange = onCustomNameChange,
            onCustomCaloriesChange = onCustomCaloriesChange,
            onCustomServingChange = onCustomServingChange,
            onConfirm = onConfirmAddFood,
            onDismiss = onDismissDialog
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.tab_meal),
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Date Selector Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.medium, vertical = spacing.small),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { onDateChange(uiState.selectedDate.minusDays(1)) }) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = null
                    )
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(spacing.cornerSmall))
                        .clickable { showDatePicker = true }
                        .padding(horizontal = spacing.medium, vertical = spacing.extraSmall),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(spacing.medium)
                    )
                    Spacer(modifier = Modifier.width(spacing.small))
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                IconButton(onClick = { onDateChange(uiState.selectedDate.plusDays(1)) }) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null
                    )
                }
            }

            // Calorie Summary Card
            CalorieSummaryCard(
                totalCalories = uiState.totalCalories,
                targetCalories = uiState.targetCalories,
                goal = uiState.goal,
                modifier = Modifier.padding(horizontal = spacing.medium, vertical = spacing.small)
            )

            // Meal Sections List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(
                    start = spacing.medium,
                    end = spacing.medium,
                    top = spacing.small,
                    bottom = spacing.large
                ),
                verticalArrangement = Arrangement.spacedBy(spacing.medium)
            ) {
                MealType.entries.forEach { mealType ->
                    val mealsForType = uiState.loggedMeals.filter { it.mealType == mealType }
                    val mealCalories = mealsForType.sumOf { it.totalCalories }

                    item(key = mealType.name) {
                        MealSectionCard(
                            mealType = mealType,
                            loggedMeals = mealsForType,
                            totalCalories = mealCalories,
                            onAddFood = { onAddFoodClicked(mealType) },
                            onDeleteFood = onDeleteFoodClicked
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CalorieSummaryCard(
    totalCalories: Int,
    targetCalories: Int,
    goal: Goal,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    val progress = if (targetCalories > 0) minOf(1f, totalCalories.toFloat() / targetCalories.toFloat()) else 0f
    val remainingCalories = targetCalories - totalCalories
    
    val isOver = totalCalories > targetCalories
    val isUnder = totalCalories < targetCalories

    val statusColor = when (goal) {
        Goal.LOSE_WEIGHT,
        Goal.MAINTAIN_WEIGHT -> {
            if (isOver) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
        }
        Goal.GAIN_WEIGHT,
        Goal.BUILD_MUSCLE -> {
            if (isUnder) Color(0xFFFFA000) // Orange
            else MaterialTheme.colorScheme.primary // Green/Primary
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(spacing.cornerLarge),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(spacing.medium)
        ) {
            Text(
                text = stringResource(R.string.daily_nutrition),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(spacing.small))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "$totalCalories",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = stringResource(R.string.total_calories_consumed),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (remainingCalories >= 0) "$remainingCalories" else "${-remainingCalories}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = statusColor
                    )
                    Text(
                        text = if (remainingCalories >= 0) "kcal còn lại" else "kcal vượt quá",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.medium))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(spacing.small),
                color = statusColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeCap = StrokeCap.Round
            )

            Spacer(modifier = Modifier.height(spacing.small))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "0 kcal",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "$targetCalories kcal mục tiêu",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun MealSectionCard(
    mealType: MealType,
    loggedMeals: List<MealLog>,
    totalCalories: Int,
    onAddFood: () -> Unit,
    onDeleteFood: (MealLog) -> Unit
) {
    val spacing = LocalSpacing.current
    var isExpanded by remember { mutableStateOf(true) }

    val icon = when (mealType) {
        MealType.BREAKFAST -> Icons.Default.WbSunny
        MealType.LUNCH -> Icons.Default.LightMode
        MealType.DINNER -> Icons.Default.NightlightRound
        MealType.SNACK -> Icons.Default.LocalCafe
    }

    val iconColor = when (mealType) {
        MealType.BREAKFAST -> Color(0xFFFFB300)
        MealType.LUNCH -> Color(0xFFFF5722)
        MealType.DINNER -> Color(0xFF3F51B5)
        MealType.SNACK -> Color(0xFF4CAF50)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(spacing.cornerMedium),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = spacing.extraSmall / 4)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(spacing.medium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(spacing.large + spacing.extraSmall)
                        .clip(RoundedCornerShape(spacing.cornerSmall))
                        .background(iconColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(spacing.medium + spacing.extraSmall)
                    )
                }

                Spacer(modifier = Modifier.width(spacing.medium))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(mealType.nameRes),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(R.string.total_meal_calories, totalCalories),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onAddFood) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = stringResource(R.string.add_food),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(spacing.large)
                    )
                }
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.medium)
                        .padding(bottom = spacing.medium)
                ) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(spacing.small))

                    if (loggedMeals.isEmpty()) {
                        Text(
                            text = stringResource(R.string.no_food_added),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = spacing.medium),
                            textAlign = TextAlign.Center
                        )
                    } else {
                        loggedMeals.forEach { log ->
                            FoodLogItem(
                                mealLog = log,
                                onDelete = { onDeleteFood(log) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FoodLogItem(
    mealLog: MealLog,
    onDelete: () -> Unit
) {
    val spacing = LocalSpacing.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = spacing.small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = mealLog.foodName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${mealLog.quantity} x ${mealLog.servingInfo}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${mealLog.totalCalories} kcal",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(spacing.small))
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete_food),
                    tint = CalorieRed,
                    modifier = Modifier.size(spacing.medium + spacing.extraSmall)
                )
            }
        }
    }
}

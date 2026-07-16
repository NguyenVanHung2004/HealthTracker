package com.example.healthtracker.presentation.meal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.healthtracker.R
import com.example.healthtracker.domain.model.FoodItem
import com.example.healthtracker.ui.theme.LocalSpacing

@Composable
fun AddFoodDialog(
    uiState: MealUiState,
    onSearchQueryChange: (String) -> Unit,
    onFoodSelected: (FoodItem) -> Unit,
    onQuantityChange: (String) -> Unit,
    onCustomFoodModeChanged: (Boolean) -> Unit,
    onCustomNameChange: (String) -> Unit,
    onCustomCaloriesChange: (String) -> Unit,
    onCustomServingChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val spacing = LocalSpacing.current
    val localContext = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(spacing.medium),
            shape = RoundedCornerShape(spacing.cornerLarge),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(spacing.medium)
            ) {
                // Header with close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.add_food) + " - " + stringResource(uiState.selectedMealType.nameRes),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = null)
                    }
                }

                Spacer(modifier = Modifier.height(spacing.small))

                // Mode Selector Segmented Control
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            RoundedCornerShape(spacing.cornerSmall)
                        )
                        .padding(spacing.extraSmall),
                    horizontalArrangement = Arrangement.spacedBy(spacing.extraSmall)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(spacing.buttonHeight - spacing.medium)
                            .background(
                                if (!uiState.isCustomFoodMode) MaterialTheme.colorScheme.primary else Color.Transparent,
                                RoundedCornerShape(spacing.cornerSmall)
                            )
                            .clickable { onCustomFoodModeChanged(false) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.food_list_title),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (!uiState.isCustomFoodMode) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(spacing.buttonHeight - spacing.medium)
                            .background(
                                if (uiState.isCustomFoodMode) MaterialTheme.colorScheme.primary else Color.Transparent,
                                RoundedCornerShape(spacing.cornerSmall)
                            )
                            .clickable { onCustomFoodModeChanged(true) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.food_custom),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (uiState.isCustomFoodMode) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(spacing.medium))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    if (!uiState.isCustomFoodMode) {
                        // Pre-defined food selection mode
                        OutlinedTextField(
                            value = uiState.searchQuery,
                            onValueChange = onSearchQueryChange,
                            placeholder = { Text(stringResource(R.string.search_food)) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            singleLine = true,
                            shape = RoundedCornerShape(spacing.cornerSmall),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(spacing.small))

                        // Selected food display
                        if (uiState.selectedFoodItem != null) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(spacing.cornerSmall),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(
                                        width = spacing.extraSmall / 4,
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = RoundedCornerShape(spacing.cornerSmall)
                                    )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(spacing.medium),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = uiState.selectedFoodItem.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        Text(
                                            text = "${uiState.selectedFoodItem.calories} kcal / ${uiState.selectedFoodItem.servingInfo}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(spacing.medium))

                            // Quantity Selector for selected food
                            Text(
                                text = stringResource(R.string.serving_size),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = spacing.extraSmall)
                            )

                            OutlinedTextField(
                                value = uiState.quantityInput,
                                onValueChange = onQuantityChange,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                leadingIcon = { Icon(Icons.Default.Calculate, contentDescription = null) },
                                singleLine = true,
                                shape = RoundedCornerShape(spacing.cornerSmall),
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            // Food items list to select from
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(spacing.small)
                            ) {
                                items(uiState.filteredFoods, key = { it.id }) { item ->
                                    val isSelected = uiState.selectedFoodItem?.id == item.id
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onFoodSelected(item) },
                                        shape = RoundedCornerShape(spacing.cornerSmall),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(spacing.medium),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(
                                                    text = item.name,
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = stringResource(R.string.food_serving_label, item.servingInfo),
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                            Text(
                                                text = "${item.calories} kcal",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // Custom/Manual Food Entry Mode
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(spacing.medium)
                        ) {
                            item {
                                OutlinedTextField(
                                    value = uiState.customFoodName,
                                    onValueChange = onCustomNameChange,
                                    label = { Text(stringResource(R.string.food_name)) },
                                    singleLine = true,
                                    shape = RoundedCornerShape(spacing.cornerSmall),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            item {
                                OutlinedTextField(
                                    value = uiState.customCalories,
                                    onValueChange = onCustomCaloriesChange,
                                    label = { Text(stringResource(R.string.calories)) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    shape = RoundedCornerShape(spacing.cornerSmall),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            item {
                                OutlinedTextField(
                                    value = uiState.customServingInfo,
                                    onValueChange = onCustomServingChange,
                                    label = { Text(stringResource(R.string.food_serving_hint)) },
                                    singleLine = true,
                                    shape = RoundedCornerShape(spacing.cornerSmall),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            item {
                                OutlinedTextField(
                                    value = uiState.quantityInput,
                                    onValueChange = onQuantityChange,
                                    label = { Text(stringResource(R.string.quantity)) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    shape = RoundedCornerShape(spacing.cornerSmall),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(spacing.medium))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.small)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(spacing.buttonHeight),
                        shape = RoundedCornerShape(spacing.cornerLarge)
                    ) {
                        Text(stringResource(R.string.cancel))
                    }

                    Button(
                        onClick = onConfirm,
                        modifier = Modifier
                            .weight(1f)
                            .height(spacing.buttonHeight),
                        shape = RoundedCornerShape(spacing.cornerLarge),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.add),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

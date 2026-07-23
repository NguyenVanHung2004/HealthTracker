package com.example.healthtracker.presentation.settings.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import com.example.healthtracker.R
import com.example.healthtracker.domain.model.ActivityLevel
import com.example.healthtracker.domain.model.Gender
import com.example.healthtracker.domain.model.Goal
import com.example.healthtracker.presentation.settings.SettingsUiState
import com.example.healthtracker.ui.theme.LocalSpacing
import java.time.Instant
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSection(
    uiState: SettingsUiState,
    onNameChange: (String) -> Unit,
    onDateOfBirthChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
    onHeightChange: (String) -> Unit,
    onGenderChange: (Gender) -> Unit,
    onActivityLevelChange: (ActivityLevel) -> Unit,
    onGoalChange: (Goal) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current

    var personalInfoExpanded by rememberSaveable { mutableStateOf(false) }
    var lifestyleExpanded by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        // ── BMI Card ──
        if (uiState.bmi > 0f) {
            BmiGaugeCard(bmi = uiState.bmi)
        }

        // ── Personal Info Section ──
        SettingsCard(
            title = stringResource(R.string.settings_edit_profile),
            isExpanded = personalInfoExpanded,
            onExpandedChange = { personalInfoExpanded = it }
        ) {
            OutlinedTextField(
                value = uiState.name,
                onValueChange = onNameChange,
                label = { Text(stringResource(R.string.settings_name)) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(spacing.cornerSmall),
                singleLine = true,
                colors = profileTextFieldColors()
            )

            Spacer(modifier = Modifier.height(spacing.small))

            val dateFormatter = remember { java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy") }
            var showDatePicker by remember { mutableStateOf(false) }
            
            if (showDatePicker) {
                var initialMillis: Long? = null
                try {
                    val parsed = java.time.LocalDate.parse(uiState.dateOfBirth, dateFormatter)
                    initialMillis = parsed.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                } catch (_: Exception) {
                    // Ignore
                }
                
                val datePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = initialMillis
                )
                
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val date = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                                onDateOfBirthChange(date.format(dateFormatter))
                            }
                            showDatePicker = false
                        }) {
                            Text(stringResource(R.string.proceed)) // Or confirm
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text(stringResource(R.string.skip))
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true }
            ) {
                OutlinedTextField(
                    value = uiState.dateOfBirth,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.settings_dob)) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                    shape = RoundedCornerShape(spacing.cornerSmall),
                    singleLine = true,
                    colors = profileTextFieldColors()
                )
            }

            Spacer(modifier = Modifier.height(spacing.small))

            // Weight & Height side-by-side
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.medium)
            ) {
                OutlinedTextField(
                    value = uiState.weight,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() || char == '.' }) onWeightChange(it)
                    },
                    label = { 
                        Text(
                            stringResource(R.string.settings_weight),
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Visible,
                            style = MaterialTheme.typography.bodySmall
                        ) 
                    },
                    trailingIcon = {
                        Text(
                            text = stringResource(R.string.unit_kg),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = spacing.small)
                        )
                    },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(spacing.cornerSmall),
                    singleLine = true,
                    colors = profileTextFieldColors()
                )

                OutlinedTextField(
                    value = uiState.height,
                    onValueChange = {
                        if (it.all { char -> char.isDigit() || char == '.' }) onHeightChange(it)
                    },
                    label = { 
                        Text(
                            stringResource(R.string.settings_height),
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Visible,
                            style = MaterialTheme.typography.bodySmall
                        ) 
                    },
                    trailingIcon = {
                        Text(
                            text = stringResource(R.string.unit_cm),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = spacing.small)
                        )
                    },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(spacing.cornerSmall),
                    singleLine = true,
                    colors = profileTextFieldColors()
                )
            }

            Spacer(modifier = Modifier.height(spacing.medium))
            Text(
                text = stringResource(R.string.settings_gender),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = spacing.extraSmall, bottom = spacing.small)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.small)
            ) {
                GenderChip(
                    label = stringResource(R.string.settings_gender_male),
                    emoji = "♂",
                    isSelected = uiState.gender == Gender.MALE,
                    onClick = { onGenderChange(Gender.MALE) },
                    modifier = Modifier.weight(1f)
                )
                GenderChip(
                    label = stringResource(R.string.settings_gender_female),
                    emoji = "♀",
                    isSelected = uiState.gender == Gender.FEMALE,
                    onClick = { onGenderChange(Gender.FEMALE) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // ── Activity Level & Goal (compact) ──
        SettingsCard(
            title = stringResource(R.string.lifestyle_goals),
            isExpanded = lifestyleExpanded,
            onExpandedChange = { lifestyleExpanded = it }
        ) {
            val activityLevels = ActivityLevel.entries
            val goals = Goal.entries

            SettingDropdownItem(
                title = stringResource(R.string.activity_level),
                selectedValue = when (uiState.activityLevel) {
                    ActivityLevel.SEDENTARY -> stringResource(R.string.activity_sedentary)
                    ActivityLevel.LIGHTLY_ACTIVE -> stringResource(R.string.activity_lightly)
                    ActivityLevel.MODERATELY_ACTIVE -> stringResource(R.string.activity_moderately)
                    ActivityLevel.VERY_ACTIVE -> stringResource(R.string.activity_very)
                    ActivityLevel.EXTRA_ACTIVE -> stringResource(R.string.activity_extra)
                },
                items = activityLevels,
                itemText = { level ->
                    when (level) {
                        ActivityLevel.SEDENTARY -> stringResource(R.string.activity_sedentary)
                        ActivityLevel.LIGHTLY_ACTIVE -> stringResource(R.string.activity_lightly)
                        ActivityLevel.MODERATELY_ACTIVE -> stringResource(R.string.activity_moderately)
                        ActivityLevel.VERY_ACTIVE -> stringResource(R.string.activity_very)
                        ActivityLevel.EXTRA_ACTIVE -> stringResource(R.string.activity_extra)
                    }
                },
                onItemSelected = onActivityLevelChange
            )

            Spacer(modifier = Modifier.height(spacing.medium))

            SettingDropdownItem(
                title = stringResource(R.string.goal),
                selectedValue = when (uiState.goal) {
                    Goal.LOSE_WEIGHT -> stringResource(R.string.goal_lose_weight)
                    Goal.MAINTAIN_WEIGHT -> stringResource(R.string.goal_maintain)
                    Goal.GAIN_WEIGHT -> stringResource(R.string.goal_gain_weight)
                    Goal.BUILD_MUSCLE -> stringResource(R.string.goal_build_muscle)
                },
                items = goals,
                itemText = { g ->
                    when (g) {
                        Goal.LOSE_WEIGHT -> stringResource(R.string.goal_lose_weight)
                        Goal.MAINTAIN_WEIGHT -> stringResource(R.string.goal_maintain)
                        Goal.GAIN_WEIGHT -> stringResource(R.string.goal_gain_weight)
                        Goal.BUILD_MUSCLE -> stringResource(R.string.goal_build_muscle)
                    }
                },
                onItemSelected = onGoalChange
            )
        }
    }
}

// ═══════════════════════════════════════════
// Sub-components
// ═══════════════════════════════════════════

@Composable
private fun GenderChip(
    label: String,
    emoji: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        label = "genderBg"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary
        else MaterialTheme.colorScheme.onSurface,
        label = "genderContent"
    )

    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(spacing.cornerSmall),
        color = bgColor,
        tonalElevation = spacing.default,
        shadowElevation = if (isSelected) spacing.elevationSmall else spacing.default
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = spacing.medium, vertical = spacing.paddingVertical),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = emoji,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.width(spacing.small))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = contentColor
            )
        }
    }
}


@Composable
private fun profileTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
    disabledContainerColor = Color.Transparent,
    disabledBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
    disabledTextColor = MaterialTheme.colorScheme.onSurface,
    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    disabledLeadingIconColor = MaterialTheme.colorScheme.primary
)

@Composable
fun <T> SettingDropdownItem(
    title: String,
    selectedValue: String,
    items: List<T>,
    itemText: @Composable (T) -> String,
    onItemSelected: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val spacing = LocalSpacing.current

    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = spacing.extraSmall)
        )
        Box {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true },
                shape = RoundedCornerShape(spacing.cornerSmall),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                border = androidx.compose.foundation.BorderStroke(
                    spacing.borderWidthUnselected, 
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.medium, vertical = spacing.medium),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedValue,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Expand",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                items.forEach { item ->
                    val text = itemText(item)
                    val isSelected = text == selectedValue
                    DropdownMenuItem(
                        text = { 
                            Text(
                                text = text,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            ) 
                        },
                        onClick = {
                            onItemSelected(item)
                            expanded = false
                        },
                        trailingIcon = if (isSelected) {
                            {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(spacing.medium + spacing.extraSmall)
                                )
                            }
                        } else null
                    )
                }
            }
        }
    }
}

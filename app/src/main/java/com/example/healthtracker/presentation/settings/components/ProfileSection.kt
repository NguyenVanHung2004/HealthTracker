package com.example.healthtracker.presentation.settings.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Height
import androidx.compose.material.icons.outlined.MonitorWeight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import com.example.healthtracker.R
import com.example.healthtracker.domain.model.ActivityLevel
import com.example.healthtracker.domain.model.Gender
import com.example.healthtracker.domain.model.Goal
import com.example.healthtracker.presentation.settings.SettingsUiState
import com.example.healthtracker.ui.theme.LocalSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSection(
    uiState: SettingsUiState,
    onNameChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
    onHeightChange: (String) -> Unit,
    onGenderChange: (Gender) -> Unit,
    onActivityLevelChange: (ActivityLevel) -> Unit,
    onGoalChange: (Goal) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current

    SettingsCard(title = stringResource(R.string.settings_edit_profile)) {
        OutlinedTextField(
            value = uiState.name,
            onValueChange = onNameChange,
            label = { Text(stringResource(R.string.settings_name)) },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(spacing.cornerSmall),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(spacing.medium))

        OutlinedTextField(
            value = uiState.weight,
            onValueChange = {
                if (it.all { char -> char.isDigit() || char == '.' }) onWeightChange(it)
            },
            label = { Text(stringResource(R.string.settings_weight)) },
            trailingIcon = {
                Text(
                    stringResource(R.string.unit_kg),
                    modifier = Modifier.padding(end = spacing.cornerSmall),
                    style = MaterialTheme.typography.labelMedium
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Outlined.MonitorWeight,
                    contentDescription = null
                )
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(spacing.cornerSmall),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(spacing.medium))

        OutlinedTextField(
            value = uiState.height,
            onValueChange = {
                if (it.all { char -> char.isDigit() || char == '.' }) onHeightChange(it)
            },
            label = { Text(stringResource(R.string.settings_height)) },
            trailingIcon = {
                Text(
                    stringResource(R.string.unit_cm),
                    modifier = Modifier.padding(end = spacing.cornerSmall),
                    style = MaterialTheme.typography.labelMedium
                )
            },
            leadingIcon = { Icon(Icons.Outlined.Height, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(spacing.cornerSmall),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(spacing.medium))

        Text(
            stringResource(R.string.settings_gender),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(spacing.small))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            SegmentButton(
                text = stringResource(R.string.settings_gender_male),
                isSelected = uiState.gender == Gender.MALE,
                onClick = { onGenderChange(Gender.MALE) },
                modifier = Modifier.weight(1f)
            )
            SegmentButton(
                text = stringResource(R.string.settings_gender_female),
                isSelected = uiState.gender == Gender.FEMALE,
                onClick = { onGenderChange(Gender.FEMALE) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(spacing.medium))

        Text(
            stringResource(R.string.activity_level),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(spacing.small))

        var activityExpanded by remember { mutableStateOf(false) }
        val activityLevels = ActivityLevel.entries
        ExposedDropdownMenuBox(
            expanded = activityExpanded,
            onExpandedChange = { activityExpanded = it }
        ) {
            OutlinedTextField(
                value = when (uiState.activityLevel) {
                    ActivityLevel.SEDENTARY -> stringResource(R.string.activity_sedentary)
                    ActivityLevel.LIGHTLY_ACTIVE -> stringResource(R.string.activity_lightly)
                    ActivityLevel.MODERATELY_ACTIVE -> stringResource(R.string.activity_moderately)
                    ActivityLevel.VERY_ACTIVE -> stringResource(R.string.activity_very)
                    ActivityLevel.EXTRA_ACTIVE -> stringResource(R.string.activity_extra)
                },
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = activityExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true),
                shape = RoundedCornerShape(spacing.cornerSmall),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )
            ExposedDropdownMenu(
                expanded = activityExpanded,
                onDismissRequest = { activityExpanded = false }
            ) {
                activityLevels.forEach { level ->
                    val text = when (level) {
                        ActivityLevel.SEDENTARY -> stringResource(R.string.activity_sedentary)
                        ActivityLevel.LIGHTLY_ACTIVE -> stringResource(R.string.activity_lightly)
                        ActivityLevel.MODERATELY_ACTIVE -> stringResource(R.string.activity_moderately)
                        ActivityLevel.VERY_ACTIVE -> stringResource(R.string.activity_very)
                        ActivityLevel.EXTRA_ACTIVE -> stringResource(R.string.activity_extra)
                    }
                    DropdownMenuItem(
                        text = { Text(text) },
                        onClick = {
                            onActivityLevelChange(level)
                            activityExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(spacing.medium))

        Text(
            stringResource(R.string.goal),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(spacing.small))

        var goalExpanded by remember { mutableStateOf(false) }
        val goals = Goal.entries
        ExposedDropdownMenuBox(
            expanded = goalExpanded,
            onExpandedChange = { goalExpanded = it }
        ) {
            OutlinedTextField(
                value = when (uiState.goal) {
                    Goal.LOSE_WEIGHT -> stringResource(R.string.goal_lose_weight)
                    Goal.MAINTAIN_WEIGHT -> stringResource(R.string.goal_maintain)
                    Goal.GAIN_WEIGHT -> stringResource(R.string.goal_gain_weight)
                    Goal.BUILD_MUSCLE -> stringResource(R.string.goal_build_muscle)
                },
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = goalExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true),
                shape = RoundedCornerShape(spacing.cornerSmall),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )
            ExposedDropdownMenu(
                expanded = goalExpanded,
                onDismissRequest = { goalExpanded = false }
            ) {
                goals.forEach { goal ->
                    val text = when (goal) {
                        Goal.LOSE_WEIGHT -> stringResource(R.string.goal_lose_weight)
                        Goal.MAINTAIN_WEIGHT -> stringResource(R.string.goal_maintain)
                        Goal.GAIN_WEIGHT -> stringResource(R.string.goal_gain_weight)
                        Goal.BUILD_MUSCLE -> stringResource(R.string.goal_build_muscle)
                    }
                    DropdownMenuItem(
                        text = { Text(text) },
                        onClick = {
                            onGoalChange(goal)
                            goalExpanded = false
                        }
                    )
                }
            }
        }
    }
}

package com.example.healthtracker.presentation.onboarding.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import com.example.healthtracker.R
import com.example.healthtracker.domain.model.ActivityLevel
import com.example.healthtracker.domain.model.Gender
import com.example.healthtracker.domain.model.Goal
import com.example.healthtracker.presentation.onboarding.OnboardingEvent
import com.example.healthtracker.presentation.onboarding.OnboardingUiState
import com.example.healthtracker.ui.theme.LocalSpacing
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun StepDot(step: Int, currentStep: Int) {
    val spacing = LocalSpacing.current
    val isPast = currentStep > step
    val isCurrent = currentStep == step
    
    Box(
        modifier = Modifier
            .size(spacing.avatarSizeSmall)
            .background(if (isPast || isCurrent) MaterialTheme.colorScheme.primary else Color.Transparent, CircleShape)
            .border(width = spacing.borderWidthUnselected, color = if (isPast || isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant, shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (isPast) {
            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(spacing.medium + spacing.extraSmall))
        } else {
            Text(text = step.toString(), color = if (isCurrent) Color.White else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step1Profile(
    uiState: OnboardingUiState,
    onEvent: (OnboardingEvent) -> Unit
) {
    val spacing = LocalSpacing.current
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
        Text(
            text = stringResource(R.string.step_1_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            modifier = Modifier.padding(bottom = spacing.large)
        )
        
        SectionTitle(stringResource(R.string.full_name))
        Spacer(modifier = Modifier.height(spacing.small))
        SimpleTextField(
            value = uiState.name,
            onValueChange = { onEvent(OnboardingEvent.OnNameChanged(it)) },
            hint = stringResource(R.string.name_hint)
        )
        
        Spacer(modifier = Modifier.height(spacing.large))
        SectionTitle(stringResource(R.string.biological_sex))
        Spacer(modifier = Modifier.height(spacing.medium))
        Column(verticalArrangement = Arrangement.spacedBy(spacing.paddingVertical)) {
            PillSelection(
                text = stringResource(R.string.gender_male),
                isSelected = uiState.gender == Gender.MALE,
                onClick = { onEvent(OnboardingEvent.OnGenderChanged(Gender.MALE)) }
            )
            PillSelection(
                text = stringResource(R.string.gender_female),
                isSelected = uiState.gender == Gender.FEMALE,
                onClick = { onEvent(OnboardingEvent.OnGenderChanged(Gender.FEMALE)) }
            )
            PillSelection(
                text = stringResource(R.string.prefer_not_to_say),
                isSelected = uiState.gender == Gender.OTHER,
                onClick = { onEvent(OnboardingEvent.OnGenderChanged(Gender.OTHER)) }
            )
        }

        Spacer(modifier = Modifier.height(spacing.large))
        SectionTitle(stringResource(R.string.settings_dob))
        Spacer(modifier = Modifier.height(spacing.medium))
        
        val dateFormatter = remember { DateTimeFormatter.ofPattern("dd/MM/yyyy") }
        uiState.dateOfBirth?.format(dateFormatter) ?: ""
        var showDatePicker by remember { mutableStateOf(false) }
        
        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = uiState.dateOfBirth
                    ?.atStartOfDay(ZoneId.systemDefault())
                    ?.toInstant()
                    ?.toEpochMilli()
            )
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val date = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                            onEvent(OnboardingEvent.OnDateOfBirthChanged(date))
                        }
                        showDatePicker = false
                    }) {
                        Text(stringResource(R.string.proceed))
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
                value = uiState.dateOfBirth?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) ?: "",
                onValueChange = {},
                readOnly = true,
                placeholder = { Text(stringResource(R.string.dob_hint), color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)) },
                trailingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = "Date of birth") },
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onBackground,
                    disabledBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                    disabledLabelColor = MaterialTheme.colorScheme.onBackground
                ),
                shape = RoundedCornerShape(spacing.cornerSmall)
            )
        }
        
        if (uiState.dateOfBirth != null) {
            Spacer(modifier = Modifier.height(spacing.small))
            Text(
                text = "${stringResource(R.string.age_years)}: ${uiState.age} ${stringResource(R.string.unit_age)}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun Step2Body(
    uiState: OnboardingUiState,
    onEvent: (OnboardingEvent) -> Unit
) {
    val spacing = LocalSpacing.current
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
        SectionTitle(stringResource(R.string.weight_title))
        Spacer(modifier = Modifier.height(spacing.medium))
        NumberInputField(
            value = uiState.weight,
            onValueChange = { onEvent(OnboardingEvent.OnWeightChanged(it)) },
            hint = stringResource(R.string.hint_weight),
            suffix = stringResource(R.string.unit_kg)
        )
        Spacer(modifier = Modifier.height(spacing.large))
        SectionTitle(stringResource(R.string.height_title))
        Spacer(modifier = Modifier.height(spacing.medium))
        NumberInputField(
            value = uiState.height,
            onValueChange = { onEvent(OnboardingEvent.OnHeightChanged(it)) },
            hint = stringResource(R.string.hint_height),
            suffix = stringResource(R.string.unit_cm)
        )
    }
}

@Composable
fun NumberInputField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    suffix: String,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = { raw ->
            if (raw.all { it.isDigit() || it == '.' || it == ',' }) onValueChange(raw)
        },
        placeholder = { Text(hint, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)) },
        trailingIcon = {
            Text(
                text = suffix,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = spacing.paddingVertical)
            )
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        shape = RoundedCornerShape(spacing.cornerSmall),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
        )
    )
}

@Composable
fun Step3Activity(
    uiState: OnboardingUiState,
    onEvent: (OnboardingEvent) -> Unit
) {
    val spacing = LocalSpacing.current
    
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
        SectionTitle(stringResource(R.string.activity_level))
        Spacer(modifier = Modifier.height(spacing.medium))
        
        ActivityLevel.entries.forEach { level ->
            val levelTextId = when (level) {
                ActivityLevel.SEDENTARY -> R.string.activity_sedentary
                ActivityLevel.LIGHTLY_ACTIVE -> R.string.activity_lightly
                ActivityLevel.MODERATELY_ACTIVE -> R.string.activity_moderately
                ActivityLevel.VERY_ACTIVE -> R.string.activity_very
                ActivityLevel.EXTRA_ACTIVE -> R.string.activity_extra
            }
            PillSelection(
                text = stringResource(levelTextId),
                isSelected = level == uiState.activityLevel,
                onClick = { onEvent(OnboardingEvent.OnActivityLevelChanged(level)) },
                modifier = Modifier.padding(bottom = spacing.paddingVertical)
            )
        }
    }
}

@Composable
fun Step4Goals(
    uiState: OnboardingUiState,
    onEvent: (OnboardingEvent) -> Unit
) {
    val spacing = LocalSpacing.current
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
        SectionTitle(stringResource(R.string.step_4_title))
        Spacer(modifier = Modifier.height(spacing.medium))
        Goal.entries.forEach { g ->
            val goalTextId = when (g) {
                Goal.LOSE_WEIGHT -> R.string.goal_lose_weight
                Goal.MAINTAIN_WEIGHT -> R.string.goal_maintain
                Goal.GAIN_WEIGHT -> R.string.goal_gain_weight
                Goal.BUILD_MUSCLE -> R.string.goal_build_muscle
            }
            PillSelection(
                text = stringResource(goalTextId),
                isSelected = g == uiState.goal,
                onClick = { onEvent(OnboardingEvent.OnGoalChanged(g)) },
                modifier = Modifier.padding(bottom = spacing.paddingVertical)
            )
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
fun SimpleTextField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(hint, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)) },
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledIndicatorColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
            disabledTextColor = MaterialTheme.colorScheme.onBackground
        ),
        keyboardOptions = keyboardOptions
    )
}

@Composable
fun PillSelection(text: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val spacing = LocalSpacing.current
    val bgColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
    val textColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .background(bgColor)
            .border(width = spacing.borderWidthUnselected, color = borderColor, shape = CircleShape)
            .clickable { onClick() }
            .padding(vertical = spacing.medium),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = textColor, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun ResultSection(bmi: Float, tdee: Int, targetCalories: Int, goal: Goal, name: String) {
    val spacing = LocalSpacing.current
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${stringResource(R.string.awesome_results)}\n${name.ifBlank { "User" }}",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(spacing.small))
        Text(
            text = stringResource(R.string.result_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = spacing.medium)
        )
        Spacer(modifier = Modifier.height(spacing.large))
        Icon(
            Icons.Default.CheckCircle,
            contentDescription = "Success",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(spacing.avatarSizeLarge).padding(bottom = spacing.large)
        )
        Text(text = stringResource(R.string.bmi_result, bmi), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(spacing.medium))
        
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = spacing.medium),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(spacing.medium).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = stringResource(R.string.result_tdee, tdee), style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(spacing.small))
                Text(text = stringResource(R.string.result_target, targetCalories), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                
                val advice = when (goal) {
                    Goal.LOSE_WEIGHT -> stringResource(R.string.advice_lose_weight)
                    Goal.MAINTAIN_WEIGHT -> stringResource(R.string.advice_maintain_weight)
                    Goal.GAIN_WEIGHT -> stringResource(R.string.advice_gain_weight)
                    Goal.BUILD_MUSCLE -> stringResource(R.string.advice_build_muscle)
                }
                Spacer(modifier = Modifier.height(spacing.small))
                Text(text = advice, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
            }
        }
    }
}

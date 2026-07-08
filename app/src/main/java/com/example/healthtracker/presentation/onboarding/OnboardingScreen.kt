package com.example.healthtracker.presentation.onboarding

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.example.healthtracker.R
import com.example.healthtracker.domain.model.ActivityLevel
import com.example.healthtracker.domain.model.Gender
import com.example.healthtracker.domain.model.Goal
import com.example.healthtracker.ui.theme.LocalSpacing
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel
import java.time.Instant
import java.time.ZoneId

@Composable
fun OnboardingRoute(
    onNavigateToDashboard: () -> Unit,
    viewModel: OnboardingViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(viewModel.uiEvent) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is OnboardingUiEvent.NavigateToDashboard -> onNavigateToDashboard()
                is OnboardingUiEvent.ShowError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    OnboardingScreen(
        uiState = uiState,
        onNameChange = viewModel::updateName,
        onDobChange = viewModel::updateDob,
        onGenderChange = viewModel::updateGender,
        onWeightChange = viewModel::updateWeight,
        onHeightChange = viewModel::updateHeight,
        onActivityLevelChange = viewModel::updateActivityLevel,
        onGoalChange = viewModel::updateGoal,
        onNextClick = viewModel::nextStep,
        onBackClick = viewModel::previousStep
    )
}

@Composable
fun OnboardingScreen(
    uiState: OnboardingUiState,
    onNameChange: (String) -> Unit,
    onDobChange: (String) -> Unit,
    onGenderChange: (Gender) -> Unit,
    onWeightChange: (String) -> Unit,
    onHeightChange: (String) -> Unit,
    onActivityLevelChange: (ActivityLevel) -> Unit,
    onGoalChange: (Goal) -> Unit,
    onNextClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val spacing = LocalSpacing.current

    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(spacing.medium),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (uiState.currentStep > 1) {
                    TextButton(onClick = onBackClick) {
                        Text(stringResource(R.string.back))
                    }
                } else {
                    Spacer(modifier = Modifier.width(spacing.medium))
                }
                Button(onClick = onNextClick) {
                    Text(
                        if (uiState.currentStep == 4) stringResource(R.string.finish) 
                        else stringResource(R.string.next)
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(spacing.medium)
        ) {
            when (uiState.currentStep) {
                1 -> BasicInfoStep(
                    name = uiState.name,
                    dob = uiState.dobString,
                    gender = uiState.gender,
                    onNameChange = onNameChange,
                    onDobChange = onDobChange,
                    onGenderChange = onGenderChange
                )
                2 -> BodyInfoStep(
                    weight = uiState.weightString,
                    height = uiState.heightString,
                    onWeightChange = onWeightChange,
                    onHeightChange = onHeightChange
                )
                3 -> GoalInfoStep(
                    activityLevel = uiState.activityLevel,
                    goal = uiState.goal,
                    onActivityLevelChange = onActivityLevelChange,
                    onGoalChange = onGoalChange
                )
                4 -> ResultStep(
                    bmi = uiState.calculatedBmi,
                    tdee = uiState.calculatedTdee
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicInfoStep(
    name: String,
    dob: String,
    gender: Gender,
    onNameChange: (String) -> Unit,
    onDobChange: (String) -> Unit,
    onGenderChange: (Gender) -> Unit
) {
    val spacing = LocalSpacing.current
    var showDatePicker by remember { mutableStateOf(false) }
    Text(
        text = stringResource(R.string.onboarding_title_step1),
        style = MaterialTheme.typography.headlineMedium
    )
    Spacer(modifier = Modifier.height(spacing.medium))
    OutlinedTextField(
        value = name,
        onValueChange = onNameChange,
        label = { Text(stringResource(R.string.name_hint)) },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(spacing.medium))
    Box(modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true }) {
        OutlinedTextField(
            value = dob,
            onValueChange = {},
            label = { Text(stringResource(R.string.dob_hint)) },
            readOnly = true,
            enabled = false,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Instant.ofEpochMilli(millis).atZone(ZoneId.of("UTC")).toLocalDate()
                        val formattedDate = String.format("%02d/%02d/%04d", date.dayOfMonth, date.monthValue, date.year)
                        onDobChange(formattedDate)
                    }
                    showDatePicker = false
                }) {
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
    Spacer(modifier = Modifier.height(spacing.medium))
    Row {
        RadioButton(
            selected = gender == Gender.MALE,
            onClick = { onGenderChange(Gender.MALE) }
        )
        Text(stringResource(R.string.gender_male), modifier = Modifier.padding(start = spacing.small, top = spacing.medium))
        Spacer(modifier = Modifier.width(spacing.large))
        RadioButton(
            selected = gender == Gender.FEMALE,
            onClick = { onGenderChange(Gender.FEMALE) }
        )
        Text(stringResource(R.string.gender_female), modifier = Modifier.padding(start = spacing.small, top = spacing.medium))
    }
}

@Composable
fun BodyInfoStep(
    weight: String,
    height: String,
    onWeightChange: (String) -> Unit,
    onHeightChange: (String) -> Unit
) {
    val spacing = LocalSpacing.current
    Text(
        text = stringResource(R.string.onboarding_title_step2),
        style = MaterialTheme.typography.headlineMedium
    )
    Spacer(modifier = Modifier.height(spacing.medium))
    OutlinedTextField(
        value = weight,
        onValueChange = onWeightChange,
        label = { Text(stringResource(R.string.weight_hint)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(spacing.medium))
    OutlinedTextField(
        value = height,
        onValueChange = onHeightChange,
        label = { Text(stringResource(R.string.height_hint)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun GoalInfoStep(
    activityLevel: ActivityLevel,
    goal: Goal,
    onActivityLevelChange: (ActivityLevel) -> Unit,
    onGoalChange: (Goal) -> Unit
) {
    val spacing = LocalSpacing.current
    Text(
        text = stringResource(R.string.onboarding_title_step3),
        style = MaterialTheme.typography.headlineMedium
    )
    Spacer(modifier = Modifier.height(spacing.medium))
    Text(stringResource(R.string.activity_level), style = MaterialTheme.typography.titleMedium)
    ActivityLevel.entries.forEach { level ->
        Row(modifier = Modifier.fillMaxWidth()) {
            RadioButton(
                selected = level == activityLevel,
                onClick = { onActivityLevelChange(level) }
            )
            Text(level.name, modifier = Modifier.padding(top = spacing.medium))
        }
    }
    
    Spacer(modifier = Modifier.height(spacing.medium))
    Text(stringResource(R.string.goal), style = MaterialTheme.typography.titleMedium)
    Goal.entries.forEach { g ->
        Row(modifier = Modifier.fillMaxWidth()) {
            RadioButton(
                selected = g == goal,
                onClick = { onGoalChange(g) }
            )
            Text(g.name, modifier = Modifier.padding(top = spacing.medium))
        }
    }
}

@Composable
fun ResultStep(
    bmi: Float,
    tdee: Int
) {
    val spacing = LocalSpacing.current
    Text(
        text = stringResource(R.string.onboarding_title_step4),
        style = MaterialTheme.typography.headlineMedium
    )
    Spacer(modifier = Modifier.height(spacing.large))
    Text(
        text = stringResource(R.string.bmi_result, bmi),
        style = MaterialTheme.typography.headlineSmall
    )
    Spacer(modifier = Modifier.height(spacing.medium))
    Text(
        text = stringResource(R.string.tdee_result, tdee),
        style = MaterialTheme.typography.headlineSmall
    )
}

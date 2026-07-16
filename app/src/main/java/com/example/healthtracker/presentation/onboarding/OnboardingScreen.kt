package com.example.healthtracker.presentation.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthtracker.R
import com.example.healthtracker.domain.model.ActivityLevel
import com.example.healthtracker.domain.model.Gender
import com.example.healthtracker.domain.model.Goal
import com.example.healthtracker.presentation.components.SnackbarController
import com.example.healthtracker.ui.theme.LocalSpacing
import org.koin.androidx.compose.koinViewModel
import java.time.format.DateTimeFormatter
import androidx.compose.ui.platform.LocalContext
import java.time.Instant
import java.time.ZoneId
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingRoute(
    viewModel: OnboardingViewModel = koinViewModel(),
    onNavigateToDashboard: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is OnboardingUiEvent.NavigateToDashboard -> onNavigateToDashboard()
                is OnboardingUiEvent.ShowError -> { 
                    SnackbarController.showSnackbar(
                        message = context.getString(event.messageId),
                        isError = true
                    )
                }
            }
        }
    }

    OnboardingScreen(
        uiState = uiState,
        onNameChange = viewModel::updateName,
        onDateOfBirthChange = viewModel::updateDateOfBirth,
        onGenderChange = viewModel::updateGender,
        onWeightChange = viewModel::updateWeight,
        onHeightChange = viewModel::updateHeight,
        onActivityLevelChange = viewModel::updateActivityLevel,
        onGoalChange = viewModel::updateGoal,
        onNextClick = viewModel::nextStep,
        onBackClick = viewModel::previousStep,
        onSkipClick = onNavigateToDashboard
    )
}

@Composable
fun OnboardingScreen(
    uiState: OnboardingUiState,
    onNameChange: (String) -> Unit,
    onDateOfBirthChange: (java.time.LocalDate) -> Unit,
    onGenderChange: (Gender) -> Unit,
    onWeightChange: (String) -> Unit,
    onHeightChange: (String) -> Unit,
    onActivityLevelChange: (ActivityLevel) -> Unit,
    onGoalChange: (Goal) -> Unit,
    onNextClick: () -> Unit,
    onBackClick: () -> Unit,
    onSkipClick: () -> Unit
) {
    val spacing = LocalSpacing.current
    val backgroundColor = MaterialTheme.colorScheme.background

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .systemBarsPadding()
                    .padding(top = spacing.medium),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = spacing.medium),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (uiState.currentStep > 1) {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground)
                        }
                    } else {
                        Spacer(modifier = Modifier.size(48.dp))
                    }
                    
                    TextButton(onClick = onSkipClick) {
                        Text(stringResource(R.string.skip), color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
                
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = spacing.medium)) {
                    StepDot(step = 1, currentStep = uiState.currentStep)
                    HorizontalDivider(modifier = Modifier.width(32.dp).padding(horizontal = 0.dp), color = if (uiState.currentStep >= 2) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant, thickness = 2.dp)
                    StepDot(step = 2, currentStep = uiState.currentStep)
                    HorizontalDivider(modifier = Modifier.width(32.dp).padding(horizontal = 0.dp), color = if (uiState.currentStep >= 3) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant, thickness = 2.dp)
                    StepDot(step = 3, currentStep = uiState.currentStep)
                    HorizontalDivider(modifier = Modifier.width(32.dp).padding(horizontal = 0.dp), color = if (uiState.currentStep >= 4) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant, thickness = 2.dp)
                    StepDot(step = 4, currentStep = uiState.currentStep)
                }
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(spacing.large)
            ) {
                Button(
                    onClick = onNextClick,
                    modifier = Modifier
                        .height(56.dp)
                        .fillMaxWidth(),
                    shape = CircleShape,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Brush.horizontalGradient(colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (uiState.currentStep == 5) stringResource(R.string.start_journey) else stringResource(R.string.proceed),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(backgroundColor)
        ) {
            AnimatedContent(
                targetState = uiState.currentStep,
                transitionSpec = {
                    if (targetState > initialState) {
                        (slideInHorizontally(animationSpec = tween(500)) { width -> width } + fadeIn(tween(500))) togetherWith
                                (slideOutHorizontally(animationSpec = tween(500)) { width -> -width } + fadeOut(tween(500)))
                    } else {
                        (slideInHorizontally(animationSpec = tween(500)) { width -> -width } + fadeIn(tween(500))) togetherWith
                                (slideOutHorizontally(animationSpec = tween(500)) { width -> width } + fadeOut(tween(500)))
                    }
                }, label = "onboarding_transition"
            ) { targetStep ->
                Surface(
                    modifier = Modifier.fillMaxSize().padding(top = 16.dp),
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = spacing.large, vertical = spacing.large),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        when (targetStep) {
                            1 -> Step1Profile(uiState, onNameChange, onDateOfBirthChange, onGenderChange)
                            2 -> Step2Body(uiState, onWeightChange, onHeightChange)
                            3 -> Step3Activity(uiState, onActivityLevelChange)
                            4 -> Step4Goals(uiState, onGoalChange)
                            else -> ResultSection(bmi = uiState.calculatedBmi, tdee = uiState.calculatedTdee, targetCalories = uiState.targetCalories, goal = uiState.goal, name = uiState.name)
                        }
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun StepDot(step: Int, currentStep: Int) {
    val isPast = currentStep > step
    val isCurrent = currentStep == step
    
    Box(
        modifier = Modifier
            .size(32.dp)
            .background(if (isPast || isCurrent) MaterialTheme.colorScheme.primary else Color.Transparent, CircleShape)
            .border(width = 1.dp, color = if (isPast || isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant, shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (isPast) {
            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
        } else {
            Text(text = step.toString(), color = if (isCurrent) Color.White else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f), fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step1Profile(
    uiState: OnboardingUiState,
    onNameChange: (String) -> Unit,
    onDateOfBirthChange: (java.time.LocalDate) -> Unit,
    onGenderChange: (Gender) -> Unit
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
        SimpleTextField(value = uiState.name, onValueChange = onNameChange, hint = stringResource(R.string.name_hint))
        
        Spacer(modifier = Modifier.height(spacing.large))
        SectionTitle(stringResource(R.string.biological_sex))
        Spacer(modifier = Modifier.height(spacing.medium))
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            PillSelection(text = stringResource(R.string.gender_male), isSelected = uiState.gender == Gender.MALE, onClick = { onGenderChange(Gender.MALE) })
            PillSelection(text = stringResource(R.string.gender_female), isSelected = uiState.gender == Gender.FEMALE, onClick = { onGenderChange(Gender.FEMALE) })
            PillSelection(text = stringResource(R.string.prefer_not_to_say), isSelected = uiState.gender == Gender.OTHER, onClick = { onGenderChange(Gender.OTHER) })
        }

        Spacer(modifier = Modifier.height(spacing.large))
        SectionTitle(stringResource(R.string.settings_dob))
        Spacer(modifier = Modifier.height(spacing.medium))
        
        val context = LocalContext.current
        val dateFormatter = remember { DateTimeFormatter.ofPattern("dd/MM/yyyy") }
        val dobText = uiState.dateOfBirth?.format(dateFormatter) ?: ""
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
                            onDateOfBirthChange(date)
                        }
                        showDatePicker = false
                    }) {
                        Text(stringResource(R.string.proceed)) // or confirm if it exists
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text(stringResource(R.string.skip)) // fallback for cancel if needed
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
                value = dobText,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text(stringResource(R.string.dob_hint), color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)) },
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onBackground,
                    disabledBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                    disabledLabelColor = MaterialTheme.colorScheme.onBackground
                ),
                shape = RoundedCornerShape(12.dp)
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
fun Step2Body(uiState: OnboardingUiState, onWeightChange: (String) -> Unit, onHeightChange: (String) -> Unit) {
    val spacing = LocalSpacing.current
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
        SectionTitle(stringResource(R.string.weight_title))
        Spacer(modifier = Modifier.height(spacing.medium))
        NumberInputField(
            value = uiState.weight,
            onValueChange = onWeightChange,
            hint = stringResource(R.string.hint_weight),
            suffix = stringResource(R.string.unit_kg)
        )
        Spacer(modifier = Modifier.height(spacing.large))
        SectionTitle(stringResource(R.string.height_title))
        Spacer(modifier = Modifier.height(spacing.medium))
        NumberInputField(
            value = uiState.height,
            onValueChange = onHeightChange,
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
                modifier = Modifier.padding(end = 12.dp)
            )
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
        )
    )
}


@Composable
fun Step3Activity(uiState: OnboardingUiState, onActivityLevelChange: (ActivityLevel) -> Unit) {
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
                onClick = { onActivityLevelChange(level) },
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }
    }
}

@Composable
fun Step4Goals(uiState: OnboardingUiState, onGoalChange: (Goal) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
        SectionTitle(stringResource(R.string.step_4_title))
        Spacer(modifier = Modifier.height(16.dp))
        Goal.entries.forEach { g ->
            val goalTextId = when (g) {
                Goal.LOSE_WEIGHT -> R.string.goal_lose_weight
                Goal.MAINTAIN_WEIGHT -> R.string.goal_maintain
                Goal.GAIN_WEIGHT -> R.string.goal_gain_weight
                Goal.BUILD_MUSCLE -> R.string.goal_build_muscle
            }
            PillSelection(text = stringResource(goalTextId), isSelected = g == uiState.goal, onClick = { onGoalChange(g) }, modifier = Modifier.padding(bottom = 12.dp))
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

@OptIn(ExperimentalMaterial3Api::class)
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
    val bgColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
    val textColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .background(bgColor)
            .border(width = 1.dp, color = borderColor, shape = CircleShape)
            .clickable { onClick() }
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = textColor, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun ResultSection(bmi: Float, tdee: Int, targetCalories: Int, goal: Goal, name: String) {
    val spacing = com.example.healthtracker.ui.theme.LocalSpacing.current
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
            modifier = Modifier.size(80.dp).padding(bottom = spacing.large)
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

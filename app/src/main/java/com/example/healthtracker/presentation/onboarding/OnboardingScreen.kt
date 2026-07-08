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
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthtracker.R
import com.example.healthtracker.domain.model.ActivityLevel
import com.example.healthtracker.domain.model.Gender
import com.example.healthtracker.domain.model.Goal
import com.example.healthtracker.ui.theme.LocalSpacing
import com.example.healthtracker.ui.theme.OrangeGradientEnd
import com.example.healthtracker.ui.theme.OrangeGradientStart
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt

@Composable
fun OnboardingRoute(
    viewModel: OnboardingViewModel = koinViewModel(),
    onNavigateToDashboard: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is OnboardingUiEvent.NavigateToDashboard -> onNavigateToDashboard()
                is OnboardingUiEvent.ShowError -> { /* Show Snackbar */ }
            }
        }
    }

    OnboardingScreen(
        uiState = uiState,
        onNameChange = viewModel::updateName,
        onAgeChange = viewModel::updateAge,
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
    onAgeChange: (Int) -> Unit,
    onGenderChange: (Gender) -> Unit,
    onWeightChange: (Float) -> Unit,
    onHeightChange: (Float) -> Unit,
    onActivityLevelChange: (ActivityLevel) -> Unit,
    onGoalChange: (Goal) -> Unit,
    onNextClick: () -> Unit,
    onBackClick: () -> Unit,
    onSkipClick: () -> Unit
) {
    val spacing = LocalSpacing.current
    val backgroundColor = Color(0xFFF5F5F5)

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
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground)
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
                    .background(Color.White)
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
                            .background(Brush.horizontalGradient(colors = listOf(OrangeGradientStart, OrangeGradientEnd))),
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
                    color = Color.White,
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
                            1 -> Step1Profile(uiState, onNameChange, onAgeChange, onGenderChange)
                            2 -> Step2Body(uiState, onWeightChange, onHeightChange)
                            3 -> Step3Activity(uiState, onActivityLevelChange)
                            4 -> Step4Goals(uiState, onGoalChange)
                            else -> ResultSection(bmi = uiState.calculatedBmi, tdee = uiState.calculatedTdee, name = uiState.name)
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
            Text(text = step.toString(), color = if (isCurrent) Color.White else MaterialTheme.colorScheme.surfaceVariant, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun Step1Profile(uiState: OnboardingUiState, onNameChange: (String) -> Unit, onAgeChange: (Int) -> Unit, onGenderChange: (Gender) -> Unit) {
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
        SectionTitle(stringResource(R.string.age_years))
        Spacer(modifier = Modifier.height(spacing.medium))
        NumberInputField(
            value = if (uiState.age > 0) uiState.age.toString() else "",
            onValueChange = { it.toIntOrNull()?.let(onAgeChange) },
            hint = stringResource(R.string.hint_age),
            suffix = stringResource(R.string.unit_age)
        )
    }
}


@Composable
fun Step2Body(uiState: OnboardingUiState, onWeightChange: (Float) -> Unit, onHeightChange: (Float) -> Unit) {
    val spacing = LocalSpacing.current
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
        SectionTitle(stringResource(R.string.weight_title))
        Spacer(modifier = Modifier.height(spacing.medium))
        NumberInputField(
            value = if (uiState.weight > 0f) String.format("%.1f", uiState.weight) else "",
            onValueChange = { it.toFloatOrNull()?.let(onWeightChange) },
            hint = stringResource(R.string.hint_weight),
            suffix = stringResource(R.string.unit_kg)
        )
        Spacer(modifier = Modifier.height(spacing.large))
        SectionTitle(stringResource(R.string.height_title))
        Spacer(modifier = Modifier.height(spacing.medium))
        NumberInputField(
            value = if (uiState.height > 0f) uiState.height.toInt().toString() else "",
            onValueChange = { it.toFloatOrNull()?.let(onHeightChange) },
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
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = { raw ->
            if (raw.all { it.isDigit() || it == '.' }) onValueChange(raw)
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
    val levels = ActivityLevel.entries
    val currentIndex = levels.indexOf(uiState.activityLevel)
    val spacing = LocalSpacing.current
    
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
        SectionTitle(stringResource(R.string.activity_level))
        Spacer(modifier = Modifier.height(32.dp))
        Slider(
            value = currentIndex.toFloat(),
            onValueChange = { onActivityLevelChange(levels[it.toInt()]) },
            valueRange = 0f..(levels.size - 1).toFloat(),
            steps = levels.size - 2,
            colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary, activeTrackColor = MaterialTheme.colorScheme.primary, inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant)
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(stringResource(R.string.activity_sedentary_short), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            Text(stringResource(R.string.activity_moderate_short), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            Text(stringResource(R.string.activity_active_short), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
        }
        
        Spacer(modifier = Modifier.height(spacing.large))
        SectionTitle(stringResource(R.string.weekly_activity_goal))
        Spacer(modifier = Modifier.height(32.dp))
        var goalIndex by remember { mutableStateOf(2f) }
        Slider(
            value = goalIndex,
            onValueChange = { goalIndex = it },
            valueRange = 0f..4f,
            steps = 3,
            colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary, activeTrackColor = MaterialTheme.colorScheme.primary, inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant)
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            listOf("<1Hr", "1-3Hr", "3-5Hr", "5-7Hr", ">7Hr").forEach {
                Text(it, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
            }
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
    val bgColor = if (isSelected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
    val textColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
    
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
fun ResultSection(bmi: Float, tdee: Int, name: String) {
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
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.result_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Icon(
            Icons.Default.CheckCircle,
            contentDescription = "Success",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(80.dp).padding(bottom = 24.dp)
        )
        Text(text = "BMI: ${String.format("%.1f", bmi)}", style = MaterialTheme.typography.titleLarge)
        Text(text = "Calo: $tdee kcal", style = MaterialTheme.typography.titleLarge)
    }
}

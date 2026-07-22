package com.example.healthtracker.presentation.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthtracker.R
import com.example.healthtracker.presentation.components.SnackbarController
import com.example.healthtracker.presentation.onboarding.components.*
import com.example.healthtracker.ui.theme.LocalSpacing
import org.koin.androidx.compose.koinViewModel

@Composable
fun OnboardingRoute(
    viewModel: OnboardingViewModel = koinViewModel(),
    onNavigateToDashboard: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
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
        onEvent = viewModel::onEvent
    )
}

@Composable
fun OnboardingScreen(
    uiState: OnboardingUiState,
    onEvent: (OnboardingEvent) -> Unit
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
                        IconButton(onClick = { onEvent(OnboardingEvent.OnBackClicked) }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground)
                        }
                    } else {
                        Spacer(modifier = Modifier.size(48.dp))
                    }
                    
                    TextButton(onClick = { onEvent(OnboardingEvent.OnSkipClicked) }) {
                        Text(stringResource(R.string.skip), color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
                
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = spacing.medium)) {
                    StepDot(step = 1, currentStep = uiState.currentStep)
                    HorizontalDivider(modifier = Modifier.width(32.dp), color = if (uiState.currentStep >= 2) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant, thickness = 2.dp)
                    StepDot(step = 2, currentStep = uiState.currentStep)
                    HorizontalDivider(modifier = Modifier.width(32.dp), color = if (uiState.currentStep >= 3) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant, thickness = 2.dp)
                    StepDot(step = 3, currentStep = uiState.currentStep)
                    HorizontalDivider(modifier = Modifier.width(32.dp), color = if (uiState.currentStep >= 4) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant, thickness = 2.dp)
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
                    onClick = { onEvent(OnboardingEvent.OnNextClicked) },
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
                            1 -> Step1Profile(uiState, onEvent)
                            2 -> Step2Body(uiState, onEvent)
                            3 -> Step3Activity(uiState, onEvent)
                            4 -> Step4Goals(uiState, onEvent)
                            else -> ResultSection(bmi = uiState.calculatedBmi, tdee = uiState.calculatedTdee, targetCalories = uiState.targetCalories, goal = uiState.goal, name = uiState.name)
                        }
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        }
    }
}

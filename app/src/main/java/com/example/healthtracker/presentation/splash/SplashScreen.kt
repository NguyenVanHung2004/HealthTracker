package com.example.healthtracker.presentation.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.healthtracker.R
import kotlinx.coroutines.delay
import com.example.healthtracker.ui.theme.LocalSpacing

@Composable
fun SplashScreen(
    isOnboardingCompleted: Boolean,
    onNavigateToOnboarding: () -> Unit,
    onNavigateToDashboard: () -> Unit
) {
    val spacing = LocalSpacing.current
    var startAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        startAnimation = true
        if (isOnboardingCompleted) {
            delay(1000)
            onNavigateToDashboard()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Skip Button
        if (!isOnboardingCompleted) {
            TextButton(
                onClick = onNavigateToDashboard,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(spacing.medium)
                    .systemBarsPadding()
            ) {
                Text(
                    text = "SKIP >",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Logo - matches splash_background.xml: 160dp centered
        Image(
            painter = painterResource(id = R.drawable.ic_splash_logo),
            contentDescription = "FitHealth Logo",
            modifier = Modifier
                .size(160.dp)
                .align(Alignment.Center)
        )

        // Bottom section for new users
        if (!isOnboardingCompleted) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .systemBarsPadding()
                    .padding(horizontal = spacing.extraLarge)
                    .padding(bottom = spacing.extraLarge),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Welcome to FitHealth, where your journey to fitness and well-being begins now!",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(spacing.extraLarge))

                Button(
                    onClick = onNavigateToOnboarding,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(spacing.buttonHeight),
                    shape = CircleShape,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.secondary
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "GET STARTED",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

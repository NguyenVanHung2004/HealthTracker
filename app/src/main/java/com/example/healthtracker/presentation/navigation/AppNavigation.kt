package com.example.healthtracker.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.healthtracker.presentation.home.HomeContainer
import com.example.healthtracker.presentation.onboarding.OnboardingRoute
import com.example.healthtracker.presentation.splash.SplashScreen

@Composable
fun AppNavigation(
    isOnboardingCompleted: Boolean?
) {
    val navController = rememberNavController()

    if (isOnboardingCompleted != null) {
        val startDestination = "splash"
        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
            composable("splash") {
                SplashScreen(
                    isOnboardingCompleted = isOnboardingCompleted == true,
                    onNavigateToOnboarding = {
                        navController.navigate("onboarding") {
                            popUpTo("splash") { inclusive = true }
                        }
                    },
                    onNavigateToDashboard = {
                        navController.navigate("main") {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
                )
            }
            composable("onboarding") {
                OnboardingRoute(
                    onNavigateToDashboard = {
                        navController.navigate("main") {
                            popUpTo("onboarding") { inclusive = true }
                        }
                    }
                )
            }
            composable("main") {
                HomeContainer()
            }
        }
    }
}

package com.example.healthtracker

import android.content.res.Configuration
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.healthtracker.data.local.preferences.UserPreferences
import com.example.healthtracker.presentation.main.MainScreen
import com.example.healthtracker.presentation.onboarding.OnboardingRoute
import com.example.healthtracker.ui.theme.HealthTrackerTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val userPreferences: UserPreferences by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themePref by userPreferences.themePreference.collectAsState(initial = "system")
            val languagePref by userPreferences.languagePreference.collectAsState(initial = "vi")

            val darkTheme = when (themePref) {
                "light" -> false
                "dark" -> true
                else -> isSystemInDarkTheme()
            }
            HealthTrackerTheme(darkTheme = darkTheme) {
                val locale = Locale(languagePref)
                val configuration = Configuration(LocalConfiguration.current).apply {
                    setLocale(locale)
                }
                val context = LocalContext.current.createConfigurationContext(configuration)

                CompositionLocalProvider(
                    LocalContext provides context,
                    LocalConfiguration provides configuration
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = "onboarding"
                    ) {
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
                            MainScreen(navController)
                        }
                    }
                }
            }
        }
    }
}
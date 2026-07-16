package com.example.healthtracker

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.os.LocaleList
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.healthtracker.data.local.preferences.UserPreferences
import com.example.healthtracker.presentation.components.CustomSnackbar
import com.example.healthtracker.presentation.components.GlobalLoadingOverlay
import com.example.healthtracker.presentation.components.SnackbarController
import com.example.healthtracker.presentation.main.MainScreen
import com.example.healthtracker.presentation.onboarding.OnboardingRoute
import com.example.healthtracker.presentation.splash.SplashScreen
import com.example.healthtracker.ui.theme.HealthTrackerTheme
import com.example.healthtracker.ui.theme.LocalSpacing
import org.koin.android.ext.android.inject
import java.util.Locale

class MainActivity : ComponentActivity() {
    private val userPreferences: UserPreferences by inject()

    companion object {
        private var appliedLanguage: String? = null
    }

    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences(UserPreferences.SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        val langTag = prefs.getString(UserPreferences.LANGUAGE_PREF_KEY, null)

        if (langTag != null) {
            val locale = Locale.forLanguageTag(langTag)
            Locale.setDefault(locale)
            val config = Configuration(newBase.resources.configuration).apply {
                setLocale(locale)
            }
            appliedLanguage = locale.language // remember what we applied
            super.attachBaseContext(newBase.createConfigurationContext(config))
        } else {
            super.attachBaseContext(newBase)
        }
    }
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val prefs = getSharedPreferences(UserPreferences.SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        val langTag = prefs.getString(UserPreferences.LANGUAGE_PREF_KEY, null) ?: return
        val savedLocale = Locale.forLanguageTag(langTag)
        val currentLang = newConfig.locales[0].language
        if (currentLang != savedLocale.language) {
            val config = Configuration(newConfig).apply { setLocale(savedLocale) }
            val ctx = createConfigurationContext(config)
            resources.updateConfiguration(ctx.resources.configuration, ctx.resources.displayMetrics)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isOnboardingCompleted by userPreferences.isOnboardingCompleted.collectAsState(initial = null)
            splashScreen.setKeepOnScreenCondition { isOnboardingCompleted == null }
            splashScreen.setOnExitAnimationListener { it.remove() }

            val themePref by userPreferences.themePreference.collectAsState(initial = "system")
    
            val languagePref by userPreferences.languagePreference
                .collectAsState(initial = appliedLanguage ?: "vi")
            val fontSizePref by userPreferences.fontSizePreference.collectAsState(initial = "medium")

            val darkTheme = when (themePref) {
                "light" -> false
                "dark" -> true
                "green_light" -> false
                "green_dark" -> true
                "blue_light" -> false
                "blue_dark" -> true
                else -> isSystemInDarkTheme()
            }
            LaunchedEffect(languagePref) {
                val newLanguage = Locale.forLanguageTag(languagePref).language
                if (appliedLanguage != null && appliedLanguage != newLanguage) {
                    appliedLanguage = newLanguage
                    recreate()
                }
            }

            HealthTrackerTheme(themePref = themePref, darkTheme = darkTheme, fontSizePref = fontSizePref) {
                CompositionLocalProvider(
                    LocalActivityResultRegistryOwner provides this
                ) {
                    if (isOnboardingCompleted != null) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            val navController = rememberNavController()
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
                                    MainScreen()
                                }
                            }

                            // Stacked Toast Overlay
                            val activeMessages = SnackbarController.activeMessages
                            Column(
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .statusBarsPadding()
                                    .padding(top = LocalSpacing.current.medium)
                                    .fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                activeMessages.forEach { msg ->
                                    androidx.compose.runtime.key(msg.id) {
                                        var visible by remember { mutableStateOf(false) }
                                        LaunchedEffect(Unit) { visible = true }

                                        AnimatedVisibility(
                                            visible = visible,
                                            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                                            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
                                        ) {
                                            CustomSnackbar(
                                                message = msg.message,
                                                isError = msg.isError
                                            )
                                        }
                                    }
                                }
                            }

                            // Global Loading Overlay
                            GlobalLoadingOverlay()
                        }
                    }
                }
            }
        }
    }
}
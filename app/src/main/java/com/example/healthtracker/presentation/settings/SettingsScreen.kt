package com.example.healthtracker.presentation.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.healthtracker.R
import com.example.healthtracker.presentation.components.SnackbarController
import com.example.healthtracker.presentation.settings.components.PreferencesSection
import com.example.healthtracker.presentation.settings.components.ProfileSection
import com.example.healthtracker.ui.theme.LocalSpacing
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val themePreference by viewModel.themePreference.collectAsStateWithLifecycle()
    val languagePreference by viewModel.languagePreference.collectAsStateWithLifecycle()
    val fontSizePreference by viewModel.fontSizePreference.collectAsStateWithLifecycle()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is SettingsUiEvent.ShowSnackbar -> {
                    SnackbarController.showSnackbar(
                        message = context.getString(event.messageId),
                        isError = event.isError
                    )
                }
            }
        }
    }

    SettingsContent(
        uiState = uiState,
        themePreference = themePreference,
        languagePreference = languagePreference,
        fontSizePreference = fontSizePreference,
        notificationsEnabled = notificationsEnabled,
        onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(
    uiState: SettingsUiState,
    themePreference: String,
    languagePreference: String,
    fontSizePreference: String,
    notificationsEnabled: Boolean,
    onEvent: (SettingsEvent) -> Unit
) {
    val spacing = LocalSpacing.current

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onEvent(SettingsEvent.OnNotificationsToggled(true))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.tab_setting),
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.background,
                tonalElevation = spacing.small,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { onEvent(SettingsEvent.OnSaveProfile) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.medium, vertical = spacing.small)
                        .height(spacing.buttonHeight),
                    shape = RoundedCornerShape(spacing.cornerMedium),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = spacing.small,
                        pressedElevation = spacing.extraSmall / 2
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(spacing.small))
                    Text(
                        stringResource(R.string.settings_save_btn),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = spacing.medium, vertical = spacing.small),
                verticalArrangement = Arrangement.spacedBy(spacing.large)
            ) {
                ProfileSection(
                    uiState = uiState,
                    onNameChange = { onEvent(SettingsEvent.OnNameChanged(it)) },
                    onDateOfBirthChange = { onEvent(SettingsEvent.OnDateOfBirthChanged(it)) },
                    onWeightChange = { onEvent(SettingsEvent.OnWeightChanged(it)) },
                    onHeightChange = { onEvent(SettingsEvent.OnHeightChanged(it)) },
                    onGenderChange = { onEvent(SettingsEvent.OnGenderChanged(it)) },
                    onActivityLevelChange = { onEvent(SettingsEvent.OnActivityLevelChanged(it)) },
                    onGoalChange = { onEvent(SettingsEvent.OnGoalChanged(it)) }
                )
                PreferencesSection(
                    themePreference = themePreference,
                    languagePreference = languagePreference,
                    fontSizePreference = fontSizePreference,
                    notificationsEnabled = notificationsEnabled,
                    onThemeChange = { onEvent(SettingsEvent.OnThemeChanged(it)) },
                    onLanguageChange = { onEvent(SettingsEvent.OnLanguageChanged(it)) },
                    onFontSizeChange = { onEvent(SettingsEvent.OnFontSizeChanged(it)) },
                    onNotificationsChange = { onEvent(SettingsEvent.OnNotificationsToggled(it)) },
                    onLaunchPermissions = {
                        permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                    },
                    onTestNotification = { onEvent(SettingsEvent.OnTestNotification) }
                )

                Spacer(modifier = Modifier.height(spacing.extraLarge))
            }
        }
    }
}

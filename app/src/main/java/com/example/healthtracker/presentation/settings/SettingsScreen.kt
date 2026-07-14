package com.example.healthtracker.presentation.settings

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SettingsSuggest
import androidx.compose.material.icons.outlined.MonitorWeight
import androidx.compose.material.icons.outlined.Height
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import com.example.healthtracker.R
import com.example.healthtracker.domain.model.Gender
import com.example.healthtracker.domain.model.ActivityLevel
import com.example.healthtracker.domain.model.Goal
import com.example.healthtracker.presentation.components.CustomSnackbar
import com.example.healthtracker.presentation.components.CustomSnackbarVisuals
import com.example.healthtracker.presentation.components.SnackbarController
import com.example.healthtracker.presentation.settings.components.*
import com.example.healthtracker.ui.theme.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.border
import androidx.compose.foundation.Canvas
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Save
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val themePreference by viewModel.themePreference.collectAsState()
    val languagePreference by viewModel.languagePreference.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.snackbarEvent.collect { messageId ->
            SnackbarController.showSnackbar(
                message = context.getString(messageId),
                isError = false
            )
        }
    }

    SettingsContent(
        uiState = uiState,
        themePreference = themePreference,
        languagePreference = languagePreference,
        onThemeChange = viewModel::updateTheme,
        onLanguageChange = viewModel::updateLanguage,
        onNameChange = viewModel::onNameChange,
        onDateOfBirthChange = viewModel::onDateOfBirthChange,
        onWeightChange = viewModel::onWeightChange,
        onHeightChange = viewModel::onHeightChange,
        onGenderChange = viewModel::onGenderChange,
        onActivityLevelChange = viewModel::onActivityLevelChange,
        onGoalChange = viewModel::onGoalChange,
        onSaveProfile = viewModel::saveProfile
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(
    uiState: SettingsUiState,
    themePreference: String,
    languagePreference: String,
    onThemeChange: (String) -> Unit,
    onLanguageChange: (String) -> Unit,
    onNameChange: (String) -> Unit,
    onDateOfBirthChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
    onHeightChange: (String) -> Unit,
    onGenderChange: (Gender) -> Unit,
    onActivityLevelChange: (ActivityLevel) -> Unit,
    onGoalChange: (Goal) -> Unit,
    onSaveProfile: () -> Unit
) {
    val spacing = LocalSpacing.current

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
                    onClick = onSaveProfile,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.medium, vertical = spacing.small)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 6.dp,
                        pressedElevation = 2.dp
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(spacing.small))
                    Text(
                        stringResource(R.string.settings_save_btn),
                        style = MaterialTheme.typography.titleMedium,
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
            return@Scaffold
        }

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
                onNameChange = onNameChange,
                onDateOfBirthChange = onDateOfBirthChange,
                onWeightChange = onWeightChange,
                onHeightChange = onHeightChange,
                onGenderChange = onGenderChange,
                onActivityLevelChange = onActivityLevelChange,
                onGoalChange = onGoalChange
            )
            PreferencesSection(
                themePreference = themePreference,
                languagePreference = languagePreference,
                onThemeChange = onThemeChange,
                onLanguageChange = onLanguageChange
            )

            Spacer(modifier = Modifier.height(spacing.extraLarge))
        }
    }
}

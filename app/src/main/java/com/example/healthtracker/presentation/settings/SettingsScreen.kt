package com.example.healthtracker.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.healthtracker.R
import com.example.healthtracker.domain.model.ActivityLevel
import com.example.healthtracker.domain.model.Gender
import com.example.healthtracker.domain.model.Goal
import com.example.healthtracker.presentation.onboarding.NumberInputField
import com.example.healthtracker.presentation.onboarding.PillSelection
import com.example.healthtracker.presentation.onboarding.SimpleTextField
import com.example.healthtracker.ui.theme.LocalSpacing
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val themePreference by viewModel.themePreference.collectAsState()
    val languagePreference by viewModel.languagePreference.collectAsState()

    SettingsContent(
        uiState = uiState,
        themePreference = themePreference,
        languagePreference = languagePreference,
        onThemeChange = viewModel::updateTheme,
        onLanguageChange = viewModel::updateLanguage,
        onNameChange = viewModel::onNameChange,
        onWeightChange = viewModel::onWeightChange,
        onHeightChange = viewModel::onHeightChange,
        onGenderChange = viewModel::onGenderChange,
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
    onWeightChange: (String) -> Unit,
    onHeightChange: (String) -> Unit,
    onGenderChange: (Gender) -> Unit,
    onSaveProfile: () -> Unit
) {
    val spacing = LocalSpacing.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.tab_setting)) }
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(innerPadding))
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.large)
        ) {
            // Preferences Section
            SectionHeader(stringResource(R.string.settings_preferences))
            
            // Theme Selection
            Text(stringResource(R.string.settings_theme), fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.small)) {
                PillSelection(stringResource(R.string.settings_theme_system), themePreference == "system", onClick = { onThemeChange("system") }, modifier = Modifier.weight(1f))
                PillSelection(stringResource(R.string.settings_theme_light), themePreference == "light", onClick = { onThemeChange("light") }, modifier = Modifier.weight(1f))
                PillSelection(stringResource(R.string.settings_theme_dark), themePreference == "dark", onClick = { onThemeChange("dark") }, modifier = Modifier.weight(1f))
            }

            // Language Selection
            Text(stringResource(R.string.settings_language), fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.small)) {
                PillSelection("Tiếng Việt", languagePreference == "vi", onClick = { onLanguageChange("vi") }, modifier = Modifier.weight(1f))
                PillSelection("English", languagePreference == "en", onClick = { onLanguageChange("en") }, modifier = Modifier.weight(1f))
            }

            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

            // Profile Section
            SectionHeader(stringResource(R.string.settings_edit_profile))
            
            SimpleTextField(
                value = uiState.name,
                onValueChange = onNameChange,
                hint = stringResource(R.string.settings_name),
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(spacing.medium), modifier = Modifier.fillMaxWidth()) {
                NumberInputField(
                    value = uiState.weight,
                    onValueChange = onWeightChange,
                    hint = stringResource(R.string.settings_weight),
                    suffix = stringResource(R.string.unit_kg),
                    modifier = Modifier.weight(1f)
                )
                NumberInputField(
                    value = uiState.height,
                    onValueChange = onHeightChange,
                    hint = stringResource(R.string.settings_height),
                    suffix = stringResource(R.string.unit_cm),
                    modifier = Modifier.weight(1f)
                )
            }

            // Gender
            Text(stringResource(R.string.settings_gender), fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.small)) {
                PillSelection(stringResource(R.string.settings_gender_male), uiState.gender == Gender.MALE, onClick = { onGenderChange(Gender.MALE) }, modifier = Modifier.weight(1f))
                PillSelection(stringResource(R.string.settings_gender_female), uiState.gender == Gender.FEMALE, onClick = { onGenderChange(Gender.FEMALE) }, modifier = Modifier.weight(1f))
            }

            Button(
                onClick = onSaveProfile,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.settings_save_btn))
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    val spacing = LocalSpacing.current
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = spacing.small)
    )
}

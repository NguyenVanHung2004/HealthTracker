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
import com.example.healthtracker.presentation.components.CustomSnackbar
import com.example.healthtracker.presentation.components.CustomSnackbarVisuals
import com.example.healthtracker.presentation.components.SnackbarController
import com.example.healthtracker.ui.theme.LocalSpacing
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
                        .padding(spacing.medium)
                        .height(spacing.buttonHeight),
                    shape = RoundedCornerShape(spacing.cornerMedium)
                ) {
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

            // App Preferences Card
            SettingsCard(title = stringResource(R.string.settings_preferences)) {
                // Theme
                SettingsSegmentedControl(
                    title = stringResource(R.string.settings_theme),
                    icon = Icons.Default.SettingsSuggest,
                    items = listOf(
                        stringResource(R.string.settings_theme_system) to "system",
                        stringResource(R.string.settings_theme_light) to "light",
                        stringResource(R.string.settings_theme_dark) to "dark"
                    ),
                    selectedValue = themePreference,
                    onValueChange = onThemeChange
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = spacing.small),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )

                // Language
                SettingsSegmentedControl(
                    title = stringResource(R.string.settings_language),
                    icon = Icons.Default.Language,
                    items = listOf(
                        "Tiếng Việt" to "vi",
                        "English" to "en"
                    ),
                    selectedValue = languagePreference,
                    onValueChange = onLanguageChange
                )
            }

            // Profile Card
            SettingsCard(title = stringResource(R.string.settings_edit_profile)) {
                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = onNameChange,
                    label = { Text(stringResource(R.string.settings_name)) },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(spacing.cornerSmall),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(spacing.medium))

                Row(horizontalArrangement = Arrangement.spacedBy(spacing.medium)) {
                    OutlinedTextField(
                        value = uiState.weight,
                        onValueChange = {
                            if (it.all { char -> char.isDigit() || char == '.' }) onWeightChange(
                                it
                            )
                        },
                        label = { Text(stringResource(R.string.settings_weight)) },
                        trailingIcon = {
                            Text(
                                stringResource(R.string.unit_kg),
                                modifier = Modifier.padding(end = spacing.cornerSmall),
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.MonitorWeight,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(spacing.cornerSmall),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = uiState.height,
                        onValueChange = {
                            if (it.all { char -> char.isDigit() || char == '.' }) onHeightChange(
                                it
                            )
                        },
                        label = { Text(stringResource(R.string.settings_height)) },
                        trailingIcon = {
                            Text(
                                stringResource(R.string.unit_cm),
                                modifier = Modifier.padding(end = spacing.cornerSmall),
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        leadingIcon = { Icon(Icons.Outlined.Height, contentDescription = null) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(spacing.cornerSmall),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(spacing.medium))

                Text(
                    stringResource(R.string.settings_gender),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(spacing.small))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.small)
                ) {
                    SegmentButton(
                        text = stringResource(R.string.settings_gender_male),
                        isSelected = uiState.gender == Gender.MALE,
                        onClick = { onGenderChange(Gender.MALE) },
                        modifier = Modifier.weight(1f)
                    )
                    SegmentButton(
                        text = stringResource(R.string.settings_gender_female),
                        isSelected = uiState.gender == Gender.FEMALE,
                        onClick = { onGenderChange(Gender.FEMALE) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Add some spacing at the bottom so content isn't hidden by the floating button bar
            Spacer(modifier = Modifier.height(spacing.extraLarge))
        }
    }
}

@Composable
fun SettingsCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    val spacing = LocalSpacing.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(spacing.cornerLarge),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = spacing.extraSmall / 2)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.medium)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = spacing.medium)
            )
            content()
        }
    }
}

@Composable
fun SettingsSegmentedControl(
    title: String,
    icon: ImageVector,
    items: List<Pair<String, String>>,
    selectedValue: String,
    onValueChange: (String) -> Unit
) {
    val spacing = LocalSpacing.current
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = spacing.small)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(spacing.cornerLarge)
            )
            Spacer(modifier = Modifier.width(spacing.small))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    RoundedCornerShape(spacing.cornerSmall)
                )
                .padding(spacing.extraSmall),
            horizontalArrangement = Arrangement.spacedBy(spacing.extraSmall)
        ) {
            items.forEach { (label, value) ->
                SegmentButton(
                    text = label,
                    isSelected = value == selectedValue,
                    onClick = { onValueChange(value) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun SegmentButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
        label = "bgColor"
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
        label = "textColor"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(spacing.small))
            .background(bgColor)
            .clickable { onClick() }
            .padding(vertical = spacing.paddingVertical),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

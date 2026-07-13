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
import com.example.healthtracker.ui.theme.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.border
import androidx.compose.foundation.Canvas
import androidx.compose.material.icons.filled.Check
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
                // Theme Options Grid
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = spacing.small)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SettingsSuggest,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(spacing.cornerLarge)
                        )
                        Spacer(modifier = Modifier.width(spacing.small))
                        Text(
                            text = stringResource(R.string.settings_theme),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    val themeOptions = listOf(
                        ThemeOption("system", R.string.settings_theme_system, MaterialTheme.colorScheme.primary, BackgroundLight, SurfaceDark),
                        ThemeOption("light", R.string.settings_theme_light, PrimaryOrange, BackgroundLight, PrimaryOrangeLight),
                        ThemeOption("dark", R.string.settings_theme_dark, PrimaryOrange, SurfaceDark, PrimaryOrangeDark),
                        ThemeOption("green_light", R.string.settings_theme_green_light, PrimaryGreen, BackgroundLight, PrimaryGreenLight),
                        ThemeOption("green_dark", R.string.settings_theme_green_dark, PrimaryGreen, SurfaceDark, PrimaryGreenDark),
                        ThemeOption("blue_light", R.string.settings_theme_blue_light, PrimaryBlue, BackgroundLight, PrimaryBlueLight),
                        ThemeOption("blue_dark", R.string.settings_theme_blue_dark, PrimaryBlue, SurfaceDark, PrimaryBlueDark)
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(spacing.small)
                    ) {
                        themeOptions.chunked(3).forEach { rowItems ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(spacing.medium),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                rowItems.forEach { option ->
                                    ThemeGridItem(
                                        label = stringResource(option.labelRes),
                                        isSelected = themePreference == option.key,
                                        primaryColor = option.primaryColor,
                                        lightBg = option.lightBg,
                                        darkBg = option.darkBg,
                                        onClick = { onThemeChange(option.key) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                if (rowItems.size < 3) {
                                    repeat(3 - rowItems.size) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }

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
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(spacing.cornerSmall),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(spacing.medium))

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
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(spacing.cornerSmall),
                    singleLine = true
                )

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

                Spacer(modifier = Modifier.height(spacing.medium))

                Text(
                    stringResource(R.string.activity_level),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(spacing.small))

                var activityExpanded by remember { mutableStateOf(false) }
                val activityLevels = ActivityLevel.entries
                ExposedDropdownMenuBox(
                    expanded = activityExpanded,
                    onExpandedChange = { activityExpanded = it }
                ) {
                    OutlinedTextField(
                        value = when (uiState.activityLevel) {
                            ActivityLevel.SEDENTARY -> stringResource(R.string.activity_sedentary)
                            ActivityLevel.LIGHTLY_ACTIVE -> stringResource(R.string.activity_lightly)
                            ActivityLevel.MODERATELY_ACTIVE -> stringResource(R.string.activity_moderately)
                            ActivityLevel.VERY_ACTIVE -> stringResource(R.string.activity_very)
                            ActivityLevel.EXTRA_ACTIVE -> stringResource(R.string.activity_extra)
                        },
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = activityExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true),
                        shape = RoundedCornerShape(spacing.cornerSmall),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = activityExpanded,
                        onDismissRequest = { activityExpanded = false }
                    ) {
                        activityLevels.forEach { level ->
                            val text = when (level) {
                                ActivityLevel.SEDENTARY -> stringResource(R.string.activity_sedentary)
                                ActivityLevel.LIGHTLY_ACTIVE -> stringResource(R.string.activity_lightly)
                                ActivityLevel.MODERATELY_ACTIVE -> stringResource(R.string.activity_moderately)
                                ActivityLevel.VERY_ACTIVE -> stringResource(R.string.activity_very)
                                ActivityLevel.EXTRA_ACTIVE -> stringResource(R.string.activity_extra)
                            }
                            DropdownMenuItem(
                                text = { Text(text) },
                                onClick = {
                                    onActivityLevelChange(level)
                                    activityExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(spacing.medium))

                Text(
                    stringResource(R.string.goal),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(spacing.small))

                var goalExpanded by remember { mutableStateOf(false) }
                val goals = Goal.entries
                ExposedDropdownMenuBox(
                    expanded = goalExpanded,
                    onExpandedChange = { goalExpanded = it }
                ) {
                    OutlinedTextField(
                        value = when (uiState.goal) {
                            Goal.LOSE_WEIGHT -> stringResource(R.string.goal_lose_weight)
                            Goal.MAINTAIN_WEIGHT -> stringResource(R.string.goal_maintain)
                            Goal.GAIN_WEIGHT -> stringResource(R.string.goal_gain_weight)
                            Goal.BUILD_MUSCLE -> stringResource(R.string.goal_build_muscle)
                        },
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = goalExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true),
                        shape = RoundedCornerShape(spacing.cornerSmall),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = goalExpanded,
                        onDismissRequest = { goalExpanded = false }
                    ) {
                        goals.forEach { goal ->
                            val text = when (goal) {
                                Goal.LOSE_WEIGHT -> stringResource(R.string.goal_lose_weight)
                                Goal.MAINTAIN_WEIGHT -> stringResource(R.string.goal_maintain)
                                Goal.GAIN_WEIGHT -> stringResource(R.string.goal_gain_weight)
                                Goal.BUILD_MUSCLE -> stringResource(R.string.goal_build_muscle)
                            }
                            DropdownMenuItem(
                                text = { Text(text) },
                                onClick = {
                                    onGoalChange(goal)
                                    goalExpanded = false
                                }
                            )
                        }
                    }
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

@Composable
fun ThemeGridItem(
    label: String,
    isSelected: Boolean,
    primaryColor: Color,
    lightBg: Color,
    darkBg: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
    val borderThickness = if (isSelected) 2.dp else 1.dp

    Column(
        modifier = modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Preview Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .clip(RoundedCornerShape(spacing.cornerMedium))
                .background(lightBg)
                .border(borderThickness, borderColor, RoundedCornerShape(spacing.cornerMedium))
        ) {
            // Draw diagonal split for dark theme view
            if (lightBg != darkBg) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val path = androidx.compose.ui.graphics.Path().apply {
                        moveTo(size.width, 0f)
                        lineTo(size.width, size.height)
                        lineTo(0f, size.height)
                        close()
                    }
                    drawPath(path = path, color = darkBg)
                }
            }

            // Primary color accent circle in the center
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.Center)
                    .clip(CircleShape)
                    .background(primaryColor)
                    .border(2.dp, if (isSelected) MaterialTheme.colorScheme.primary else Color.White, CircleShape)
            )

            // Checkmark overlay if selected
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(spacing.extraSmall))

        // Label
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            maxLines = 1
        )
    }
}

data class ThemeOption(
    val key: String,
    val labelRes: Int,
    val primaryColor: Color,
    val lightBg: Color,
    val darkBg: Color
)

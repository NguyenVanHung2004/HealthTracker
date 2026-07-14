package com.example.healthtracker.presentation.settings.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.SettingsSuggest
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.healthtracker.R
import com.example.healthtracker.ui.theme.*

@Composable
fun PreferencesSection(
    themePreference: String,
    languagePreference: String,
    fontSizePreference: String,
    onThemeChange: (String) -> Unit,
    onLanguageChange: (String) -> Unit,
    onFontSizeChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    SettingsCard(
        title = stringResource(R.string.settings_preferences),
        isExpanded = isExpanded,
        onExpandedChange = { isExpanded = it }
    ) {
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

        HorizontalDivider(
            modifier = Modifier.padding(vertical = spacing.small),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )

        // Font Size
        SettingsSegmentedControl(
            title = stringResource(R.string.settings_font_size),
            icon = Icons.Default.TextFields,
            items = listOf(
                stringResource(R.string.font_size_small) to "small",
                stringResource(R.string.font_size_medium) to "medium",
                stringResource(R.string.font_size_large) to "large"
            ),
            selectedValue = fontSizePreference,
            onValueChange = onFontSizeChange
        )
    }
}

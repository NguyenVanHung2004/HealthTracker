package com.example.healthtracker.presentation.settings.components

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.SettingsSuggest
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Notifications
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
import androidx.core.content.ContextCompat
import com.example.healthtracker.R
import com.example.healthtracker.domain.alarm.ReminderType
import com.example.healthtracker.ui.theme.*
import java.util.Locale

@Composable
fun PreferencesSection(
    themePreference: String,
    languagePreference: String,
    fontSizePreference: String,
    notificationsEnabled: Boolean,
    onThemeChange: (String) -> Unit,
    onLanguageChange: (String) -> Unit,
    onFontSizeChange: (String) -> Unit,
    onNotificationsChange: (Boolean) -> Unit,
    onLaunchPermissions: () -> Unit,
    onTestNotification: () -> Unit
) {
    val spacing = LocalSpacing.current
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    val context = androidx.compose.ui.platform.LocalContext.current

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
                ThemeOption(
                    "system",
                    R.string.settings_theme_system,
                    MaterialTheme.colorScheme.primary,
                    BackgroundLight,
                    SurfaceDark
                ),
                ThemeOption(
                    "light",
                    R.string.settings_theme_light,
                    PrimaryOrange,
                    BackgroundLight,
                    PrimaryOrangeLight
                ),
                ThemeOption(
                    "dark",
                    R.string.settings_theme_dark,
                    PrimaryOrange,
                    SurfaceDark,
                    PrimaryOrangeDark
                ),
                ThemeOption(
                    "green_light",
                    R.string.settings_theme_green_light,
                    PrimaryGreen,
                    BackgroundLight,
                    PrimaryGreenLight
                ),
                ThemeOption(
                    "green_dark",
                    R.string.settings_theme_green_dark,
                    PrimaryGreen,
                    SurfaceDark,
                    PrimaryGreenDark
                ),
                ThemeOption(
                    "blue_light",
                    R.string.settings_theme_blue_light,
                    PrimaryBlue,
                    BackgroundLight,
                    PrimaryBlueLight
                ),
                ThemeOption(
                    "blue_dark",
                    R.string.settings_theme_blue_dark,
                    PrimaryBlue,
                    SurfaceDark,
                    PrimaryBlueDark
                )
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

        HorizontalDivider(
            modifier = Modifier.padding(vertical = spacing.small),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )

        // Daily Reminders

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = spacing.small),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = spacing.small)
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(spacing.cornerLarge)
                )
                Spacer(modifier = Modifier.width(spacing.small))
                val reminderTimes = ReminderType.entries.joinToString(", ") {
                    String.format(
                        Locale.getDefault(),
                        "%02d:%02d",
                        it.defaultHour,
                        it.defaultMinute
                    )
                }
                Text(
                    text = stringResource(R.string.settings_daily_reminders) + " ($reminderTimes)",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Switch(
                checked = notificationsEnabled,
                onCheckedChange = { isChecked ->
                    if (isChecked && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val hasPermission = ContextCompat.checkSelfPermission(
                            context,
                            POST_NOTIFICATIONS
                        ) == PERMISSION_GRANTED
                        if (!hasPermission) {
                            onLaunchPermissions()
                        } else {
                            onNotificationsChange(true)
                        }
                    } else {
                        onNotificationsChange(isChecked)
                    }
                }
            )
        }

        if (notificationsEnabled) {
            TextButton(
                onClick = onTestNotification,
                modifier = Modifier.padding(start = spacing.extraLarge)
            ) {
                Text(
                    stringResource(R.string.settings_test_notification),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

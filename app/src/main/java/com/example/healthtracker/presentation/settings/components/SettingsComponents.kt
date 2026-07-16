package com.example.healthtracker.presentation.settings.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.healthtracker.R
import com.example.healthtracker.ui.theme.BackgroundLight
import com.example.healthtracker.ui.theme.LocalSpacing
import com.example.healthtracker.ui.theme.PrimaryOrange
import com.example.healthtracker.ui.theme.*

@Composable
fun SettingsCard(
    title: String,
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
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
        Column(modifier = Modifier.fillMaxWidth()) {
            // Clickable header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onExpandedChange(!isExpanded) }
                    .padding(spacing.medium)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Collapsible content
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.medium)
                        .padding(bottom = spacing.medium)
                ) {
                    content()
                }
            }
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

@Composable
fun rememberThemeOptions(): List<ThemeOption> {
    val systemPrimary = MaterialTheme.colorScheme.primary
    return remember(systemPrimary) {
        listOf(
            ThemeOption(
                "system",
                R.string.settings_theme_system,
                systemPrimary,
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
            ),
            ThemeOption(
                "purple_light",
                R.string.settings_theme_purple_light,
                PrimaryPurple,
                BackgroundLight,
                PrimaryPurpleLight
            ),
            ThemeOption(
                "purple_dark",
                R.string.settings_theme_purple_dark,
                PrimaryPurple,
                SurfaceDark,
                PrimaryPurpleDark
            ),
            ThemeOption(
                "rose_light",
                R.string.settings_theme_rose_light,
                PrimaryRose,
                BackgroundLight,
                PrimaryRoseLight
            ),
            ThemeOption(
                "rose_dark",
                R.string.settings_theme_rose_dark,
                PrimaryRose,
                SurfaceDark,
                PrimaryRoseDark
            ),
            ThemeOption(
                "teal_light",
                R.string.settings_theme_teal_light,
                PrimaryTeal,
                BackgroundLight,
                PrimaryTealLight
            ),
            ThemeOption(
                "teal_dark",
                R.string.settings_theme_teal_dark,
                PrimaryTeal,
                SurfaceDark,
                PrimaryTealDark
            )
        )
    }
}

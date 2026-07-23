package com.example.healthtracker.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.healthtracker.ui.theme.LocalSpacing
import com.example.healthtracker.ui.theme.SuccessGreen

class CustomSnackbarVisuals(
    override val message: String,
    val isError: Boolean = false,
    override val actionLabel: String? = null,
    override val withDismissAction: Boolean = false,
    override val duration: SnackbarDuration = SnackbarDuration.Short
) : SnackbarVisuals

@Composable
fun CustomSnackbar(
    message: String,
    isError: Boolean,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current

    val containerColor = if (isError) MaterialTheme.colorScheme.errorContainer else SuccessGreen
    val contentColor = if (isError) MaterialTheme.colorScheme.onErrorContainer else Color.White
    val icon = if (isError) Icons.Default.Error else Icons.Default.CheckCircle

    Card(
        shape = RoundedCornerShape(spacing.cornerSmall),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = spacing.extraSmall + spacing.extraSmall / 2),
        modifier = modifier
            .padding(horizontal = spacing.medium, vertical = spacing.small)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(spacing.medium)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = contentColor,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

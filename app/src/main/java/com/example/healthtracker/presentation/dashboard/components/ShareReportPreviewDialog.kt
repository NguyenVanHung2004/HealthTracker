package com.example.healthtracker.presentation.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.healthtracker.R
import com.example.healthtracker.presentation.dashboard.DashboardUiState
import com.example.healthtracker.ui.theme.LocalSpacing

@Composable
fun ShareReportPreviewDialog(
    uiState: DashboardUiState,
    onDismiss: () -> Unit,
    onConfirmShare: () -> Unit,
    onSavePdf: () -> Unit
) {
    val spacing = LocalSpacing.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .wrapContentWidth()
                .padding(spacing.medium),
            shape = RoundedCornerShape(spacing.cornerLarge),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = spacing.small)
        ) {
            Column(
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(spacing.medium)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(spacing.small))

                // Card Preview
                Box(
                    modifier = Modifier
                        .wrapContentWidth()
                        .heightIn(max = spacing.maxFormWidth),
                    contentAlignment = Alignment.Center
                ) {
                    WeeklyReportCard(uiState = uiState)
                }

                Spacer(modifier = Modifier.height(spacing.medium))

                // Action Buttons Column
                Column(
                    modifier = Modifier.width(spacing.reportCardWidth),
                    verticalArrangement = Arrangement.spacedBy(spacing.small)
                ) {
                    // Primary action button: Share Now
                    Button(
                        onClick = onConfirmShare,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(spacing.buttonHeight),
                        shape = RoundedCornerShape(spacing.cornerMedium),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.size(spacing.medium)
                        )
                        Spacer(modifier = Modifier.width(spacing.small))
                        Text(
                            text = stringResource(R.string.share_now),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Secondary action buttons row: Save PDF & Close
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing.small)
                    ) {
                        OutlinedButton(
                            onClick = onSavePdf,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(spacing.cornerMedium),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = null,
                                modifier = Modifier.size(spacing.medium)
                            )
                            Spacer(modifier = Modifier.width(spacing.extraSmall))
                            Text(
                                text = stringResource(R.string.save_pdf),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(spacing.cornerMedium)
                        ) {
                            Text(
                                text = stringResource(R.string.close),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

package com.example.healthtracker.presentation.settings

import androidx.annotation.StringRes

sealed class SettingsUiEvent {
    data class ShowSnackbar(@StringRes val messageId: Int, val isError: Boolean = false) : SettingsUiEvent()
}

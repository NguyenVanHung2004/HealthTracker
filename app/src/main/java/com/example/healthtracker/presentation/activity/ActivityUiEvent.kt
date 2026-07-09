package com.example.healthtracker.presentation.activity

import androidx.annotation.StringRes

sealed class ActivityUiEvent {
    data class ShowError(@StringRes val messageId: Int) : ActivityUiEvent()
    data class ShowSuccess(@StringRes val messageId: Int) : ActivityUiEvent()
    object NavigateBack : ActivityUiEvent()
}

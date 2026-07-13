package com.example.healthtracker.presentation.meal

import androidx.annotation.StringRes

sealed class MealUiEvent {
    data class ShowError(@StringRes val messageId: Int) : MealUiEvent()
    data class ShowSuccess(@StringRes val messageId: Int) : MealUiEvent()
}

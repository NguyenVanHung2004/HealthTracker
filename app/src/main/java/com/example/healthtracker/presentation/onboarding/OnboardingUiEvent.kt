package com.example.healthtracker.presentation.onboarding

import androidx.annotation.StringRes

sealed class OnboardingUiEvent {
    data class ShowError(@StringRes val messageId: Int) : OnboardingUiEvent()
    object NavigateToDashboard : OnboardingUiEvent()
}

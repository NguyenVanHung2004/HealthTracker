package com.example.healthtracker.presentation.onboarding

sealed class OnboardingUiEvent {
    data class ShowError(val message: String) : OnboardingUiEvent()
    object NavigateToDashboard : OnboardingUiEvent()
}

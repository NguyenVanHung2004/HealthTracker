package com.example.healthtracker.presentation.onboarding

import com.example.healthtracker.domain.model.ActivityLevel
import com.example.healthtracker.domain.model.Gender
import com.example.healthtracker.domain.model.Goal
import java.time.LocalDate

sealed interface OnboardingEvent {
    data class OnNameChanged(val name: String) : OnboardingEvent
    data class OnDateOfBirthChanged(val dob: LocalDate) : OnboardingEvent
    data class OnGenderChanged(val gender: Gender) : OnboardingEvent
    data class OnWeightChanged(val weight: String) : OnboardingEvent
    data class OnHeightChanged(val height: String) : OnboardingEvent
    data class OnActivityLevelChanged(val level: ActivityLevel) : OnboardingEvent
    data class OnGoalChanged(val goal: Goal) : OnboardingEvent
    object OnNextClicked : OnboardingEvent
    object OnBackClicked : OnboardingEvent
    object OnSkipClicked : OnboardingEvent
}

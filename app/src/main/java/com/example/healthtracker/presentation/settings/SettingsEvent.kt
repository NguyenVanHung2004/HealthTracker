package com.example.healthtracker.presentation.settings

import com.example.healthtracker.domain.model.ActivityLevel
import com.example.healthtracker.domain.model.Gender
import com.example.healthtracker.domain.model.Goal

sealed interface SettingsEvent {
    data class OnNameChanged(val name: String) : SettingsEvent
    data class OnWeightChanged(val weight: String) : SettingsEvent
    data class OnHeightChanged(val height: String) : SettingsEvent
    data class OnDateOfBirthChanged(val dob: String) : SettingsEvent
    data class OnGenderChanged(val gender: Gender) : SettingsEvent
    data class OnActivityLevelChanged(val level: ActivityLevel) : SettingsEvent
    data class OnGoalChanged(val goal: Goal) : SettingsEvent
    data class OnThemeChanged(val theme: String) : SettingsEvent
    data class OnLanguageChanged(val language: String) : SettingsEvent
    data class OnFontSizeChanged(val size: String) : SettingsEvent
    data class OnNotificationsToggled(val enabled: Boolean) : SettingsEvent
    object OnSaveProfile : SettingsEvent
    object OnTestNotification : SettingsEvent
}

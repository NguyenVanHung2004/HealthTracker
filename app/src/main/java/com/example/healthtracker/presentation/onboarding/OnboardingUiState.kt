package com.example.healthtracker.presentation.onboarding

import com.example.healthtracker.domain.model.ActivityLevel
import com.example.healthtracker.domain.model.Gender
import com.example.healthtracker.domain.model.Goal

data class OnboardingUiState(
    val name: String = "",
    val dobString: String = "", // dd/MM/yyyy
    val gender: Gender = Gender.MALE,
    val weightString: String = "",
    val heightString: String = "",
    val activityLevel: ActivityLevel = ActivityLevel.SEDENTARY,
    val goal: Goal = Goal.MAINTAIN_WEIGHT,
    val calculatedBmi: Float = 0f,
    val calculatedTdee: Int = 0,
    val currentStep: Int = 1 // 1: Basic Info, 2: Body, 3: Goal, 4: Result
)

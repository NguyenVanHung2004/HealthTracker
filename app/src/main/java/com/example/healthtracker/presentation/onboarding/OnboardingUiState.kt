package com.example.healthtracker.presentation.onboarding

import com.example.healthtracker.domain.model.ActivityLevel
import com.example.healthtracker.domain.model.Gender
import com.example.healthtracker.domain.model.Goal

data class OnboardingUiState(
    val name: String = "",
    val age: Int = 24,
    val gender: Gender = Gender.MALE,
    val weight: Float = 60f,
    val height: Float = 170f,
    val activityLevel: ActivityLevel = ActivityLevel.SEDENTARY,
    val goal: Goal = Goal.MAINTAIN_WEIGHT,
    val calculatedBmi: Float = 0f,
    val calculatedTdee: Int = 0,
    val currentStep: Int = 1 // 1: Basic Info, 2: Body, 3: Goal, 4: Result
)

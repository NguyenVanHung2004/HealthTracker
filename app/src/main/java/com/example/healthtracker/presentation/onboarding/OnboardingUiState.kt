package com.example.healthtracker.presentation.onboarding

import com.example.healthtracker.domain.model.ActivityLevel
import com.example.healthtracker.domain.model.Gender
import com.example.healthtracker.domain.model.Goal
import java.time.LocalDate

data class OnboardingUiState(
    val name: String = "",
    val dateOfBirth: LocalDate? = null,
    val age: Int = 0,
    val gender: Gender = Gender.MALE,
    val weight: String = "60",
    val height: String = "170",
    val activityLevel: ActivityLevel = ActivityLevel.SEDENTARY,
    val goal: Goal = Goal.MAINTAIN_WEIGHT,
    val calculatedBmi: Float = 0f,
    val calculatedTdee: Int = 0,
    val targetCalories: Int = 0,
    val currentStep: Int = 1 // 1: Basic Info, 2: Body, 3: Goal, 4: Result
)

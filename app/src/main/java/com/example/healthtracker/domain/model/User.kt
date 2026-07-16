package com.example.healthtracker.domain.model

import java.time.LocalDate

data class User(
    val name: String,
    val dateOfBirth: LocalDate,
    val gender: Gender,
    val weightKg: Float,
    val heightCm: Float,
    val activityLevel: ActivityLevel,
    val goal: Goal,
    val tdee: Int = 0,
    val targetCalories: Int = 0,
    val bmi: Float = 0f
)

package com.example.healthtracker.domain.model

import java.time.LocalDate

data class ExerciseLog(
    val id: Int = 0,
    val type: ExerciseType,
    val durationMinutes: Int,
    val caloriesBurned: Int,
    val date: LocalDate
)

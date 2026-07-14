package com.example.healthtracker.domain.model

import java.time.LocalDate

data class MealLog(
    val id: String = java.util.UUID.randomUUID().toString(),
    val date: LocalDate,
    val mealType: MealType,
    val foodName: String,
    val caloriesPerServing: Int,
    val servingInfo: String,
    val quantity: Double,
    val totalCalories: Int = (caloriesPerServing * quantity).toInt()
)

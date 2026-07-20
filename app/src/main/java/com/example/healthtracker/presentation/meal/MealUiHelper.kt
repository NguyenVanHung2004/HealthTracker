package com.example.healthtracker.presentation.meal

import com.example.healthtracker.R
import com.example.healthtracker.domain.model.MealType

val MealType.nameRes: Int
    get() = when (this) {
        MealType.BREAKFAST -> R.string.meal_breakfast
        MealType.LUNCH -> R.string.meal_lunch
        MealType.DINNER -> R.string.meal_dinner
        MealType.SNACK -> R.string.meal_snack
    }

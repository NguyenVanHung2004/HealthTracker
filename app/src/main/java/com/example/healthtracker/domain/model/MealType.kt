package com.example.healthtracker.domain.model

import androidx.annotation.StringRes
import com.example.healthtracker.R

enum class MealType(@StringRes val nameRes: Int) {
    BREAKFAST(R.string.meal_breakfast),
    LUNCH(R.string.meal_lunch),
    DINNER(R.string.meal_dinner),
    SNACK(R.string.meal_snack)
}

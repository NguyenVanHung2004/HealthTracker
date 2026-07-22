package com.example.healthtracker.domain.usecase

import com.example.healthtracker.domain.model.ActivityLevel
import com.example.healthtracker.domain.model.Goal

data class TDEEResult(val maintenance: Int, val target: Int)

class CalculateTDEEUseCase {
    operator fun invoke(bmr: Double, activityLevel: ActivityLevel, goal: Goal): TDEEResult {
        val tdee = bmr * activityLevel.factor
        val finalCalories = when (goal) {
            Goal.LOSE_WEIGHT -> tdee - 500
            Goal.MAINTAIN_WEIGHT -> tdee
            Goal.GAIN_WEIGHT -> tdee + 500
            Goal.BUILD_MUSCLE -> tdee + 250
        }
        return TDEEResult(tdee.toInt(), finalCalories.toInt())
    }
}

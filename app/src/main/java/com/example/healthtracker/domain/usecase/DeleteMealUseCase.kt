package com.example.healthtracker.domain.usecase

import com.example.healthtracker.domain.model.MealLog
import com.example.healthtracker.domain.repository.MealRepository

class DeleteMealUseCase(
    private val mealRepository: MealRepository
) {
    suspend operator fun invoke(mealLog: MealLog) = mealRepository.deleteMeal(mealLog)
}

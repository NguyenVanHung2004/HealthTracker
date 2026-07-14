package com.example.healthtracker.domain.usecase

import com.example.healthtracker.domain.model.MealLog
import com.example.healthtracker.domain.repository.MealRepository

class AddMealUseCase(
    private val mealRepository: MealRepository
) {
    suspend operator fun invoke(mealLog: MealLog) = mealRepository.insertMeal(mealLog)
}

package com.example.healthtracker.domain.usecase

import com.example.healthtracker.domain.model.FoodItem
import com.example.healthtracker.domain.repository.MealRepository

class SeedFoodItemsUseCase(
    private val mealRepository: MealRepository
) {
    suspend operator fun invoke(sampleFoods: List<FoodItem>) {
        if (mealRepository.getFoodItemCount() == 0) {
            mealRepository.insertFoodItems(sampleFoods)
        }
    }
}

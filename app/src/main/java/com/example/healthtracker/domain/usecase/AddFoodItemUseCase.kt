package com.example.healthtracker.domain.usecase

import com.example.healthtracker.domain.model.FoodItem
import com.example.healthtracker.domain.repository.MealRepository

class AddFoodItemUseCase(
    private val mealRepository: MealRepository
) {
    suspend operator fun invoke(foodItem: FoodItem) {
        mealRepository.insertFoodItems(listOf(foodItem))
    }
}

package com.example.healthtracker.domain.usecase

import com.example.healthtracker.domain.model.FoodItem
import com.example.healthtracker.domain.repository.MealRepository
import kotlinx.coroutines.flow.Flow

class GetAllFoodItemsUseCase(
    private val mealRepository: MealRepository
) {
    operator fun invoke(): Flow<List<FoodItem>> = mealRepository.getAllFoodItems()
}

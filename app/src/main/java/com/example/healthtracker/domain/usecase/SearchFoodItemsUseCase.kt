package com.example.healthtracker.domain.usecase

import com.example.healthtracker.domain.model.FoodItem
import com.example.healthtracker.domain.repository.MealRepository
import kotlinx.coroutines.flow.Flow

class SearchFoodItemsUseCase(
    private val mealRepository: MealRepository
) {
    operator fun invoke(query: String): Flow<List<FoodItem>> = mealRepository.searchFoodItems(query)
}

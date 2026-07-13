package com.example.healthtracker.domain.repository

import com.example.healthtracker.domain.model.FoodItem
import com.example.healthtracker.domain.model.MealLog
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface MealRepository {
    suspend fun insertMeal(meal: MealLog)
    suspend fun deleteMeal(meal: MealLog)
    fun getMealsByDate(date: LocalDate): Flow<List<MealLog>>
    fun getMealsByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<MealLog>>

    // Pre-defined food items
    fun getAllFoodItems(): Flow<List<FoodItem>>
    fun searchFoodItems(query: String): Flow<List<FoodItem>>
    suspend fun insertFoodItems(items: List<FoodItem>)
    suspend fun getFoodItemCount(): Int
}

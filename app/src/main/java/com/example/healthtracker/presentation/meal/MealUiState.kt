package com.example.healthtracker.presentation.meal

import com.example.healthtracker.domain.model.FoodItem
import com.example.healthtracker.domain.model.MealLog
import com.example.healthtracker.domain.model.MealType
import java.time.LocalDate

data class MealUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val loggedMeals: List<MealLog> = emptyList(),
    val totalCalories: Int = 0,
    val targetCalories: Int = 2000, // standard default
    val searchQuery: String = "",
    val availableFoods: List<FoodItem> = emptyList(),
    val filteredFoods: List<FoodItem> = emptyList(),
    
    // Add Food dialog state
    val isAddFoodDialogVisible: Boolean = false,
    val selectedMealType: MealType = MealType.BREAKFAST,
    val selectedFoodItem: FoodItem? = null,
    val quantityInput: String = "1",
    val customFoodName: String = "",
    val customCalories: String = "",
    val customServingInfo: String = "100g",
    val isCustomFoodMode: Boolean = false
)

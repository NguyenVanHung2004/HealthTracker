package com.example.healthtracker.presentation.meal

import com.example.healthtracker.domain.model.FoodItem
import com.example.healthtracker.domain.model.MealLog
import com.example.healthtracker.domain.model.MealType
import java.time.LocalDate

sealed interface MealEvent {
    data class OnDateChanged(val date: LocalDate) : MealEvent
    data class OnAddFoodClicked(val mealType: MealType) : MealEvent
    data class OnDeleteMealLog(val mealLog: MealLog) : MealEvent
    data class OnSearchQueryChanged(val query: String) : MealEvent
    data class OnFoodSelected(val foodItem: FoodItem) : MealEvent
    data class OnQuantityInputChanged(val quantity: String) : MealEvent
    data class OnCustomFoodModeToggled(val enabled: Boolean) : MealEvent
    data class OnCustomFoodNameChanged(val name: String) : MealEvent
    data class OnCustomCaloriesChanged(val calories: String) : MealEvent
    data class OnCustomServingInfoChanged(val servingInfo: String) : MealEvent
    object OnConfirmAddFood : MealEvent
    object OnDismissDialog : MealEvent
}

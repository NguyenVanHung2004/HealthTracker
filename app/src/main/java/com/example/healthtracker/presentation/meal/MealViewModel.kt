package com.example.healthtracker.presentation.meal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthtracker.R
import com.example.healthtracker.domain.model.FoodItem
import com.example.healthtracker.domain.model.MealLog
import com.example.healthtracker.domain.model.MealType
import com.example.healthtracker.domain.usecase.GetUserUseCase
import com.example.healthtracker.domain.usecase.SeedFoodItemsUseCase
import com.example.healthtracker.domain.usecase.GetAllFoodItemsUseCase
import com.example.healthtracker.domain.usecase.GetMealsByDateUseCase
import com.example.healthtracker.domain.usecase.SearchFoodItemsUseCase
import com.example.healthtracker.domain.usecase.AddMealUseCase
import com.example.healthtracker.domain.usecase.DeleteMealUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.example.healthtracker.data.local.FoodItemSeedData
import java.time.LocalDate
import com.example.healthtracker.presentation.components.LoadingController
import kotlinx.coroutines.delay

class MealViewModel(
    private val getUserUseCase: GetUserUseCase,
    private val seedFoodItemsUseCase: SeedFoodItemsUseCase,
    private val getAllFoodItemsUseCase: GetAllFoodItemsUseCase,
    private val getMealsByDateUseCase: GetMealsByDateUseCase,
    private val searchFoodItemsUseCase: SearchFoodItemsUseCase,
    private val addMealUseCase: AddMealUseCase,
    private val deleteMealUseCase: DeleteMealUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MealUiState())
    val uiState: StateFlow<MealUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<MealUiEvent>()
    val uiEvent: SharedFlow<MealUiEvent> = _uiEvent.asSharedFlow()

    init {
        val today = LocalDate.now()
        loadUserData()
        
        viewModelScope.launch {
            // Seed food items in database if empty
            seedFoodItemsUseCase(FoodItemSeedData.sampleFoods)
        }

        observeFoodItems()
        observeLogsForDate(today)
    }

    private fun loadUserData() {
        viewModelScope.launch {
            getUserUseCase().collect { user ->
                val target = if (user != null && user.targetCalories > 0) user.targetCalories else 2000
                val goal = user?.goal ?: com.example.healthtracker.domain.model.Goal.MAINTAIN_WEIGHT
                _uiState.update { it.copy(targetCalories = target, goal = goal) }
            }
        }
    }

    private var observeFoodsJob: Job? = null

    private fun observeFoodItems() {
        observeFoodsJob?.cancel()
        observeFoodsJob = viewModelScope.launch {
            getAllFoodItemsUseCase().collect { foods ->
                _uiState.update { state ->
                    state.copy(
                        availableFoods = foods,
                        filteredFoods = if (state.searchQuery.isBlank()) foods else state.filteredFoods
                    )
                }
            }
        }
    }

    fun setDate(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date) }
        observeLogsForDate(date)
    }

    private var observeLogsJob: Job? = null
    private fun observeLogsForDate(date: LocalDate) {
        observeLogsJob?.cancel()
        observeLogsJob = viewModelScope.launch {
            getMealsByDateUseCase(date).collect { logs ->
                val total = logs.sumOf { it.totalCalories }
                _uiState.update { state ->
                    state.copy(
                        loggedMeals = logs,
                        totalCalories = total
                    )
                }
            }
        }
    }

    private var searchJob: Job? = null

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            if (query.isBlank()) {
                _uiState.update { it.copy(filteredFoods = it.availableFoods) }
            } else {
                searchFoodItemsUseCase(query).collect { filtered ->
                    _uiState.update { it.copy(filteredFoods = filtered) }
                }
            }
        }
    }

    fun openAddFoodDialog(mealType: MealType) {
        _uiState.update {
            it.copy(
                isAddFoodDialogVisible = true,
                selectedMealType = mealType,
                searchQuery = "",
                filteredFoods = it.availableFoods,
                selectedFoodItem = null,
                quantityInput = "1",
                customFoodName = "",
                customCalories = "",
                customServingInfo = "100g",
                isCustomFoodMode = false
            )
        }
    }

    fun closeAddFoodDialog() {
        _uiState.update { it.copy(isAddFoodDialogVisible = false) }
    }

    fun selectFoodItem(foodItem: FoodItem) {
        _uiState.update {
            it.copy(
                selectedFoodItem = foodItem,
                quantityInput = "1"
            )
        }
    }

    fun setCustomFoodMode(enabled: Boolean) {
        _uiState.update {
            it.copy(
                isCustomFoodMode = enabled,
                selectedFoodItem = null,
                searchQuery = ""
            )
        }
    }

    fun updateQuantityInput(quantity: String) {
        if (quantity.isEmpty() || quantity.all { it.isDigit() || it == '.' }) {
            _uiState.update { it.copy(quantityInput = quantity) }
        }
    }

    fun updateCustomFoodName(name: String) {
        _uiState.update { it.copy(customFoodName = name) }
    }

    fun updateCustomCalories(calories: String) {
        if (calories.isEmpty() || calories.all { it.isDigit() }) {
            _uiState.update { it.copy(customCalories = calories) }
        }
    }

    fun updateCustomServingInfo(info: String) {
        _uiState.update { it.copy(customServingInfo = info) }
    }

    fun addMealLog() {
        val state = _uiState.value
        val date = state.selectedDate
        val quantity = state.quantityInput.toDoubleOrNull() ?: 1.0

        val newLog = if (state.isCustomFoodMode) {
            if (state.customFoodName.isBlank()) {
                emitEvent(MealUiEvent.ShowError(R.string.toast_enter_food_name))
                return
            }
            val calories = state.customCalories.toIntOrNull()
            if (calories == null || calories <= 0) {
                emitEvent(MealUiEvent.ShowError(R.string.toast_enter_calories))
                return
            }
            MealLog(
                date = date,
                mealType = state.selectedMealType,
                foodName = state.customFoodName,
                caloriesPerServing = calories,
                servingInfo = state.customServingInfo,
                quantity = quantity
            )
        } else {
            val selected = state.selectedFoodItem
            if (selected == null) {
                emitEvent(MealUiEvent.ShowError(R.string.toast_enter_food_name))
                return
            }
            MealLog(
                date = date,
                mealType = state.selectedMealType,
                foodName = selected.name,
                caloriesPerServing = selected.calories,
                servingInfo = selected.servingInfo,
                quantity = quantity
            )
        }

        viewModelScope.launch {
            try {
                LoadingController.withLoading {
                    delay(600)
                    addMealUseCase(newLog)
                }
                emitEvent(MealUiEvent.ShowSuccess(R.string.toast_food_added))
                closeAddFoodDialog()
            } catch (e: Exception) {
                emitEvent(MealUiEvent.ShowError(R.string.error_occurred))
            }
        }
    }

    fun deleteMealLog(mealLog: MealLog) {
        viewModelScope.launch {
            try {
                LoadingController.withLoading {
                    delay(500)
                    deleteMealUseCase(mealLog)
                }
                emitEvent(MealUiEvent.ShowSuccess(R.string.toast_food_deleted))
            } catch (e: Exception) {
                emitEvent(MealUiEvent.ShowError(R.string.error_occurred))
            }
        }
    }

    private fun emitEvent(event: MealUiEvent) {
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }
}

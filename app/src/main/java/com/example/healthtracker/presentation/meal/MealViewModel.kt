package com.example.healthtracker.presentation.meal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthtracker.R
import com.example.healthtracker.data.local.FoodItemSeedData
import com.example.healthtracker.domain.model.FoodItem
import com.example.healthtracker.domain.model.Goal
import com.example.healthtracker.domain.model.MealLog
import com.example.healthtracker.domain.model.MealType
import com.example.healthtracker.domain.usecase.AddFoodItemUseCase
import com.example.healthtracker.domain.usecase.AddMealUseCase
import com.example.healthtracker.domain.usecase.DeleteMealUseCase
import com.example.healthtracker.domain.usecase.GetAllFoodItemsUseCase
import com.example.healthtracker.domain.usecase.GetMealsByDateUseCase
import com.example.healthtracker.domain.usecase.GetUserUseCase
import com.example.healthtracker.domain.usecase.SearchFoodItemsUseCase
import com.example.healthtracker.domain.usecase.SeedFoodItemsUseCase
import com.example.healthtracker.presentation.components.LoadingController
import com.example.healthtracker.presentation.widget.WidgetUpdater
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.coroutines.cancellation.CancellationException

class MealViewModel(
    private val getUserUseCase: GetUserUseCase,
    private val seedFoodItemsUseCase: SeedFoodItemsUseCase,
    private val getAllFoodItemsUseCase: GetAllFoodItemsUseCase,
    private val getMealsByDateUseCase: GetMealsByDateUseCase,
    private val searchFoodItemsUseCase: SearchFoodItemsUseCase,
    private val addMealUseCase: AddMealUseCase,
    private val deleteMealUseCase: DeleteMealUseCase,
    private val addFoodItemUseCase: AddFoodItemUseCase,
    private val widgetUpdater: WidgetUpdater
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    private val _dialogState = MutableStateFlow(MealDialogState())

    private val _uiEvent = MutableSharedFlow<MealUiEvent>()
    val uiEvent: SharedFlow<MealUiEvent> = _uiEvent.asSharedFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _mealsForDateFlow = _selectedDate.flatMapLatest { date ->
        getMealsByDateUseCase(date)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _filteredFoodsFlow = _dialogState.map { it.searchQuery }.distinctUntilChanged().flatMapLatest { query ->
        if (query.isBlank()) {
            getAllFoodItemsUseCase()
        } else {
            searchFoodItemsUseCase(query)
        }
    }

    private val _foodsFlow = combine(getAllFoodItemsUseCase(), _filteredFoodsFlow) { all, filtered ->
        all to filtered
    }

    val uiState: StateFlow<MealUiState> = combine(
        getUserUseCase(),
        _selectedDate,
        _mealsForDateFlow,
        _foodsFlow,
        _dialogState
    ) { user, date, logs, (allFoods, filteredFoods), dialog ->
        val target = if (user != null && user.targetCalories > 0) user.targetCalories else 2000
        val goal = user?.goal ?: Goal.MAINTAIN_WEIGHT
        val totalCals = logs.sumOf { it.totalCalories }

        MealUiState(
            selectedDate = date,
            targetCalories = target,
            goal = goal,
            loggedMeals = logs,
            totalCalories = totalCals,
            availableFoods = allFoods,
            filteredFoods = filteredFoods,
            searchQuery = dialog.searchQuery,
            isAddFoodDialogVisible = dialog.isVisible,
            selectedMealType = dialog.selectedMealType,
            selectedFoodItem = dialog.selectedFoodItem,
            quantityInput = dialog.quantityInput,
            customFoodName = dialog.customFoodName,
            customCalories = dialog.customCalories,
            customServingInfo = dialog.customServingInfo,
            isCustomFoodMode = dialog.isCustomFoodMode
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MealUiState()
    )

    init {
        viewModelScope.launch {
            seedFoodItemsUseCase(FoodItemSeedData.sampleFoods)
        }
    }

    fun onEvent(event: MealEvent) {
        when (event) {
            is MealEvent.OnDateChanged -> setDate(event.date)
            is MealEvent.OnAddFoodClicked -> openAddFoodDialog(event.mealType)
            is MealEvent.OnDeleteMealLog -> deleteMealLog(event.mealLog)
            is MealEvent.OnSearchQueryChanged -> setSearchQuery(event.query)
            is MealEvent.OnFoodSelected -> selectFoodItem(event.foodItem)
            is MealEvent.OnQuantityInputChanged -> updateQuantityInput(event.quantity)
            is MealEvent.OnCustomFoodModeToggled -> setCustomFoodMode(event.enabled)
            is MealEvent.OnCustomFoodNameChanged -> updateCustomFoodName(event.name)
            is MealEvent.OnCustomCaloriesChanged -> updateCustomCalories(event.calories)
            is MealEvent.OnCustomServingInfoChanged -> updateCustomServingInfo(event.servingInfo)
            is MealEvent.OnConfirmAddFood -> addMealLog()
            is MealEvent.OnDismissDialog -> closeAddFoodDialog()
        }
    }

    fun setDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun setSearchQuery(query: String) {
        _dialogState.update { it.copy(searchQuery = query) }
    }

    fun openAddFoodDialog(mealType: MealType) {
        _dialogState.update {
            MealDialogState(
                isVisible = true,
                selectedMealType = mealType
            )
        }
    }

    fun closeAddFoodDialog() {
        _dialogState.update { it.copy(isVisible = false) }
    }

    fun selectFoodItem(foodItem: FoodItem) {
        _dialogState.update {
            it.copy(
                selectedFoodItem = foodItem,
                quantityInput = "1"
            )
        }
    }

    fun setCustomFoodMode(enabled: Boolean) {
        _dialogState.update {
            it.copy(
                isCustomFoodMode = enabled,
                selectedFoodItem = null,
                searchQuery = ""
            )
        }
    }

    fun updateQuantityInput(quantity: String) {
        if (quantity.isEmpty() || quantity.all { it.isDigit() || it == '.' }) {
            _dialogState.update { it.copy(quantityInput = quantity) }
        }
    }

    fun updateCustomFoodName(name: String) {
        _dialogState.update { it.copy(customFoodName = name) }
    }

    fun updateCustomCalories(calories: String) {
        if (calories.isEmpty() || calories.all { it.isDigit() }) {
            _dialogState.update { it.copy(customCalories = calories) }
        }
    }

    fun updateCustomServingInfo(info: String) {
        _dialogState.update { it.copy(customServingInfo = info) }
    }

    fun addMealLog() {
        val state = uiState.value
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
            
            viewModelScope.launch {
                try {
                    addFoodItemUseCase(
                        FoodItem(
                            name = state.customFoodName,
                            calories = calories,
                            servingInfo = state.customServingInfo
                        )
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
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
                    addMealUseCase(newLog)
                }
                widgetUpdater.updateWidget()
                emitEvent(MealUiEvent.ShowSuccess(R.string.toast_food_added))
                closeAddFoodDialog()
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                emitEvent(MealUiEvent.ShowError(R.string.error_occurred))
            }
        }
    }

    fun deleteMealLog(mealLog: MealLog) {
        viewModelScope.launch {
            try {
                LoadingController.withLoading {
                    deleteMealUseCase(mealLog)
                }
                widgetUpdater.updateWidget()
                emitEvent(MealUiEvent.ShowSuccess(R.string.toast_food_deleted))
            } catch (e: Exception) {
                if (e is CancellationException) throw e
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

private data class MealDialogState(
    val isVisible: Boolean = false,
    val selectedMealType: MealType = MealType.BREAKFAST,
    val selectedFoodItem: FoodItem? = null,
    val quantityInput: String = "1",
    val searchQuery: String = "",
    val customFoodName: String = "",
    val customCalories: String = "",
    val customServingInfo: String = "100g",
    val isCustomFoodMode: Boolean = false
)

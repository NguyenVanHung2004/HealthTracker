package com.example.healthtracker.presentation.meal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthtracker.R
import com.example.healthtracker.domain.model.FoodItem
import com.example.healthtracker.domain.model.MealLog
import com.example.healthtracker.domain.model.MealType
import com.example.healthtracker.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class MealViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MealUiState())
    val uiState: StateFlow<MealUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<MealUiEvent>()
    val uiEvent: SharedFlow<MealUiEvent> = _uiEvent.asSharedFlow()

    // Mock database for logged meals in-memory
    private val mealDatabase = mutableMapOf<LocalDate, MutableList<MealLog>>()

    // Pre-defined sample food items list (34 items)
    private val sampleFoods = listOf(
        FoodItem(name = "Cơm trắng", calories = 130, servingInfo = "100g"),
        FoodItem(name = "Phở bò (tô nhỏ)", calories = 350, servingInfo = "1 tô"),
        FoodItem(name = "Phở gà", calories = 300, servingInfo = "1 tô"),
        FoodItem(name = "Trứng gà luộc", calories = 78, servingInfo = "1 quả"),
        FoodItem(name = "Bánh mì kẹp thịt", calories = 400, servingInfo = "1 cái"),
        FoodItem(name = "Bánh mì không", calories = 265, servingInfo = "1 cái"),
        FoodItem(name = "Ức gà áp chảo", calories = 165, servingInfo = "100g"),
        FoodItem(name = "Thịt heo luộc", calories = 240, servingInfo = "100g"),
        FoodItem(name = "Thịt bò nướng", calories = 250, servingInfo = "100g"),
        FoodItem(name = "Cá hồi áp chảo", calories = 200, servingInfo = "100g"),
        FoodItem(name = "Bún chả", calories = 450, servingInfo = "1 phần"),
        FoodItem(name = "Bún bò Huế", calories = 480, servingInfo = "1 tô"),
        FoodItem(name = "Xôi xéo", calories = 400, servingInfo = "1 gói"),
        FoodItem(name = "Bánh cuốn", calories = 320, servingInfo = "1 đĩa"),
        FoodItem(name = "Chuối chín", calories = 90, servingInfo = "1 quả"),
        FoodItem(name = "Táo", calories = 52, servingInfo = "1 quả"),
        FoodItem(name = "Sữa tươi không đường", calories = 62, servingInfo = "100ml"),
        FoodItem(name = "Sữa chua ít đường", calories = 80, servingInfo = "1 hộp"),
        FoodItem(name = "Rau muống luộc", calories = 40, servingInfo = "1 đĩa"),
        FoodItem(name = "Đậu hũ sốt cà chua", calories = 200, servingInfo = "1 đĩa"),
        FoodItem(name = "Quả bơ", calories = 160, servingInfo = "1 quả"),
        FoodItem(name = "Hạt điều", calories = 170, servingInfo = "30g"),
        FoodItem(name = "Hạt hạnh nhân", calories = 180, servingInfo = "30g"),
        FoodItem(name = "Khoai lang luộc", calories = 86, servingInfo = "100g"),
        FoodItem(name = "Ngô ngọt luộc", calories = 150, servingInfo = "1 bắp"),
        FoodItem(name = "Bánh ngọt", calories = 300, servingInfo = "1 cái"),
        FoodItem(name = "Nước cam ép", calories = 110, servingInfo = "1 ly"),
        FoodItem(name = "Cafe sữa đá", calories = 150, servingInfo = "1 ly"),
        FoodItem(name = "Trà sữa", calories = 350, servingInfo = "1 ly"),
        FoodItem(name = "Đùi gà rán", calories = 240, servingInfo = "1 cái"),
        FoodItem(name = "Pizza", calories = 280, servingInfo = "1 miếng"),
        FoodItem(name = "Mì ăn liền", calories = 350, servingInfo = "1 gói"),
        FoodItem(name = "Cháo thịt băm", calories = 250, servingInfo = "1 tô"),
        FoodItem(name = "Canh bí đao sườn heo", calories = 120, servingInfo = "1 tô")
    )

    init {
        // Pre-populate mock logs for today
        val today = LocalDate.now()
        val initialLogs = mutableListOf(
            MealLog(
                date = today,
                mealType = MealType.BREAKFAST,
                foodName = "Cơm trắng",
                caloriesPerServing = 130,
                servingInfo = "100g",
                quantity = 1.5
            ),
            MealLog(
                date = today,
                mealType = MealType.BREAKFAST,
                foodName = "Trứng gà luộc",
                caloriesPerServing = 78,
                servingInfo = "1 quả",
                quantity = 2.0
            ),
            MealLog(
                date = today,
                mealType = MealType.LUNCH,
                foodName = "Phở bò (tô nhỏ)",
                caloriesPerServing = 350,
                servingInfo = "1 tô",
                quantity = 1.0
            ),
            MealLog(
                date = today,
                mealType = MealType.DINNER,
                foodName = "Ức gà áp chảo",
                caloriesPerServing = 165,
                servingInfo = "100g",
                quantity = 1.2
            ),
            MealLog(
                date = today,
                mealType = MealType.DINNER,
                foodName = "Rau muống luộc",
                caloriesPerServing = 40,
                servingInfo = "1 đĩa",
                quantity = 1.0
            )
        )
        mealDatabase[today] = initialLogs

        _uiState.update {
            it.copy(
                availableFoods = sampleFoods,
                filteredFoods = sampleFoods
            )
        }

        loadUserData()
        loadLogsForDate(today)
    }

    private fun loadUserData() {
        viewModelScope.launch {
            userRepository.getUser().collect { user ->
                val target = if (user != null && user.targetCalories > 0) user.targetCalories else 2000
                _uiState.update { it.copy(targetCalories = target) }
            }
        }
    }

    fun setDate(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date) }
        loadLogsForDate(date)
    }

    private fun loadLogsForDate(date: LocalDate) {
        val logs = mealDatabase[date] ?: emptyList<MealLog>()
        val total = logs.sumOf { it.totalCalories }
        _uiState.update {
            it.copy(
                loggedMeals = logs,
                totalCalories = total
            )
        }
    }

    fun setSearchQuery(query: String) {
        val filtered = if (query.isBlank()) {
            sampleFoods
        } else {
            sampleFoods.filter { it.name.contains(query, ignoreCase = true) }
        }
        _uiState.update {
            it.copy(
                searchQuery = query,
                filteredFoods = filtered
            )
        }
    }

    fun openAddFoodDialog(mealType: MealType) {
        _uiState.update {
            it.copy(
                isAddFoodDialogVisible = true,
                selectedMealType = mealType,
                searchQuery = "",
                filteredFoods = sampleFoods,
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

        val logs = mealDatabase[date] ?: mutableListOf()
        logs.add(newLog)
        mealDatabase[date] = logs

        loadLogsForDate(date)
        emitEvent(MealUiEvent.ShowSuccess(R.string.toast_food_added))
        closeAddFoodDialog()
    }

    fun deleteMealLog(mealLog: MealLog) {
        val date = mealLog.date
        val logs = mealDatabase[date]
        if (logs != null) {
            logs.remove(mealLog)
            mealDatabase[date] = logs
            loadLogsForDate(date)
            emitEvent(MealUiEvent.ShowSuccess(R.string.toast_food_deleted))
        }
    }

    private fun emitEvent(event: MealUiEvent) {
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }
}

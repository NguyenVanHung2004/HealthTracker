package com.example.healthtracker.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthtracker.domain.model.DomainAdviceType
import com.example.healthtracker.domain.usecase.GetDashboardDataUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val getDashboardDataUseCase: GetDashboardDataUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()
    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            getDashboardDataUseCase().collect { data ->
                val adviceType = when (data.adviceType) {
                    DomainAdviceType.UNDER_TARGET -> AdviceType.UNDER_TARGET
                    DomainAdviceType.TARGET_MET -> AdviceType.TARGET_MET
                    DomainAdviceType.OVER_TARGET -> AdviceType.OVER_TARGET
                }

                val last7Days = data.last7DaysCalories.map { daily ->
                    BarChartData(
                        dayLabel = daily.dayLabel,
                        calories = daily.calories
                    )
                }

                val weeklyTrend = data.weeklyTrend.map { trend ->
                    LineChartPoint(
                        label = trend.label,
                        caloriesConsumed = trend.caloriesConsumed,
                        caloriesBurned = trend.caloriesBurned
                    )
                }

                _uiState.update {
                    it.copy(
                        selectedDate = data.selectedDate,
                        goal = data.goal,
                        tdee = data.tdee,
                        targetCalories = data.targetCalories,
                        consumedCaloriesToday = data.consumedCaloriesToday,
                        burnedCaloriesToday = data.burnedCaloriesToday,
                        remainingCaloriesToday = data.remainingCaloriesToday,
                        isRemainingExceeded = data.isRemainingExceeded,
                        adviceType = adviceType,
                        adviceDiffCalories = data.adviceDiffCalories,
                        last7DaysCalories = last7Days,
                        weeklyTrend = weeklyTrend,
                        avgCaloriesConsumed = data.avgCaloriesConsumed,
                        avgCaloriesBurned = data.avgCaloriesBurned,
                        daysTargetMet = data.daysTargetMet,
                        todayMeals = data.todayMeals,
                        todayExercises = data.todayExercises,
                        isLoading = false
                    )
                }
            }
        }
    }
}

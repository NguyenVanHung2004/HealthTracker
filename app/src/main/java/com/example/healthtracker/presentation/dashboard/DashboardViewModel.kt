package com.example.healthtracker.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthtracker.domain.model.DomainAdviceType
import com.example.healthtracker.domain.usecase.GetDashboardDataUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class DashboardViewModel(
    getDashboardDataUseCase: GetDashboardDataUseCase
) : ViewModel() {

    val uiState: StateFlow<DashboardUiState> = getDashboardDataUseCase()
        .map { data ->
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

            DashboardUiState(
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
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DashboardUiState(isLoading = true)
        )
}

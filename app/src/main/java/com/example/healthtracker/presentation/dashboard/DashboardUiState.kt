package com.example.healthtracker.presentation.dashboard

import com.example.healthtracker.domain.model.ExerciseLog
import com.example.healthtracker.domain.model.MealLog
import java.time.LocalDate

data class BarChartData(
    val dayLabel: String,
    val calories: Float
)

data class LineChartPoint(
    val label: String,
    val caloriesConsumed: Float,
    val caloriesBurned: Float
)

enum class AdviceType {
    UNDER_TARGET,
    TARGET_MET,
    OVER_TARGET
}

data class DashboardUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val targetCalories: Int = 2000,
    val consumedCaloriesToday: Int = 0,
    val burnedCaloriesToday: Int = 0,
    val remainingCaloriesToday: Int = 2000,
    val isRemainingExceeded: Boolean = false,
    val adviceType: AdviceType = AdviceType.UNDER_TARGET,
    val adviceDiffCalories: Int = 0,
    
    // Charts data
    val last7DaysCalories: List<BarChartData> = emptyList(),
    val weeklyTrend: List<LineChartPoint> = emptyList(),
    
    // Stats
    val avgCaloriesConsumed: Float = 0f,
    val avgCaloriesBurned: Float = 0f,
    val daysTargetMet: Int = 0,
    
    // Today's logs
    val todayMeals: List<MealLog> = emptyList(),
    val todayExercises: List<ExerciseLog> = emptyList(),
    
    val isLoading: Boolean = true
)

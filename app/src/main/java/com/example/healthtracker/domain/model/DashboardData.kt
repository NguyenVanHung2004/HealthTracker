package com.example.healthtracker.domain.model

import java.time.LocalDate

enum class DomainAdviceType {
    UNDER_TARGET,
    TARGET_MET,
    OVER_TARGET
}

data class DailyCalorieData(
    val dayLabel: String,
    val calories: Float
)

data class WeeklyCalorieTrendPoint(
    val label: String,
    val caloriesConsumed: Float,
    val caloriesBurned: Float
)

data class DashboardData(
    val selectedDate: LocalDate,
    val targetCalories: Int,
    val consumedCaloriesToday: Int,
    val burnedCaloriesToday: Int,
    val remainingCaloriesToday: Int,
    val isRemainingExceeded: Boolean,
    val adviceType: DomainAdviceType,
    val adviceDiffCalories: Int,
    val last7DaysCalories: List<DailyCalorieData>,
    val weeklyTrend: List<WeeklyCalorieTrendPoint>,
    val avgCaloriesConsumed: Float,
    val avgCaloriesBurned: Float,
    val daysTargetMet: Int,
    val todayMeals: List<MealLog>,
    val todayExercises: List<ExerciseLog>
)

package com.example.healthtracker.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthtracker.domain.model.Goal
import com.example.healthtracker.domain.repository.ExerciseRepository
import com.example.healthtracker.domain.repository.MealRepository
import com.example.healthtracker.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class DashboardViewModel(
    private val userRepository: UserRepository,
    private val mealRepository: MealRepository,
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        val today = LocalDate.now()
        val startDate = today.minusDays(6)

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            combine(
                userRepository.getUser(),
                mealRepository.getMealsByDate(today),
                exerciseRepository.getExercisesByDate(today),
                mealRepository.getMealsByDateRange(startDate, today),
                exerciseRepository.getExercisesByDateRange(startDate, today)
            ) { user, todayMeals, todayExercises, rangeMeals, rangeExercises ->
                
                val target = if (user != null && user.targetCalories > 0) user.targetCalories else 2000
                val goal = user?.goal ?: Goal.MAINTAIN_WEIGHT

                val consumedToday = todayMeals.sumOf { it.totalCalories }
                val burnedToday = todayExercises.sumOf { it.caloriesBurned }

                // Standard Formula for remaining calories: Target - Consumed + Burned
                val remaining = target - consumedToday + burnedToday
                val isExceeded = remaining < 0
                val absRemaining = kotlin.math.abs(remaining)

                // Advice Logic based on Goal and current status
                val (adviceType, adviceDiff) = when {
                    isExceeded -> {
                        AdviceType.OVER_TARGET to absRemaining
                    }
                    absRemaining <= 100 -> {
                        AdviceType.TARGET_MET to 0
                    }
                    else -> {
                        AdviceType.UNDER_TARGET to absRemaining
                    }
                }

                // 7 days calories nạp vào list (for Bar Chart)
                val last7Days = (0..6).map { today.minusDays(6 - it.toLong()) }
                val barChartList = last7Days.map { date ->
                    val dayName = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                    val dayMeals = rangeMeals.filter { it.date == date }
                    val cal = dayMeals.sumOf { it.totalCalories }.toFloat()
                    BarChartData(dayLabel = dayName, calories = cal)
                }

                // Weekly Calorie Trend (for Line Chart)
                val lineChartList = last7Days.map { date ->
                    val dayName = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                    val dayMeals = rangeMeals.filter { it.date == date }
                    val dayExercises = rangeExercises.filter { it.date == date }
                    val consumed = dayMeals.sumOf { it.totalCalories }.toFloat()
                    val burned = dayExercises.sumOf { it.caloriesBurned }.toFloat()
                    LineChartPoint(label = dayName, caloriesConsumed = consumed, caloriesBurned = burned)
                }

                // Stats Calculation
                val avgConsumed = if (rangeMeals.isNotEmpty()) {
                    rangeMeals.sumOf { it.totalCalories }.toFloat() / 7f
                } else 0f

                val avgBurned = if (rangeExercises.isNotEmpty()) {
                    rangeExercises.sumOf { it.caloriesBurned }.toFloat() / 7f
                } else 0f

                var daysMet = 0
                last7Days.forEach { date ->
                    val dayMeals = rangeMeals.filter { it.date == date }
                    val dayConsumed = dayMeals.sumOf { it.totalCalories }
                    if (dayConsumed > 0) {
                        val dayDiff = target - dayConsumed
                        val met = when (goal) {
                            Goal.LOSE_WEIGHT -> dayConsumed <= target && dayConsumed >= target - 500
                            Goal.MAINTAIN_WEIGHT -> kotlin.math.abs(dayDiff) <= 200
                            Goal.GAIN_WEIGHT, Goal.BUILD_MUSCLE -> dayConsumed >= target
                        }
                        if (met) {
                            daysMet++
                        }
                    }
                }

                DashboardUiState(
                    selectedDate = today,
                    targetCalories = target,
                    consumedCaloriesToday = consumedToday,
                    burnedCaloriesToday = burnedToday,
                    remainingCaloriesToday = absRemaining,
                    isRemainingExceeded = isExceeded,
                    adviceType = adviceType,
                    adviceDiffCalories = adviceDiff,
                    last7DaysCalories = barChartList,
                    weeklyTrend = lineChartList,
                    avgCaloriesConsumed = avgConsumed,
                    avgCaloriesBurned = avgBurned,
                    daysTargetMet = daysMet,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
}

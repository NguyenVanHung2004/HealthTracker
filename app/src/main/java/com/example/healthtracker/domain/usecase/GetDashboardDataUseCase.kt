package com.example.healthtracker.domain.usecase

import com.example.healthtracker.domain.model.DashboardData
import com.example.healthtracker.domain.model.DailyCalorieData
import com.example.healthtracker.domain.model.WeeklyCalorieTrendPoint
import com.example.healthtracker.domain.model.DomainAdviceType
import com.example.healthtracker.domain.model.Goal
import com.example.healthtracker.domain.repository.ExerciseRepository
import com.example.healthtracker.domain.repository.MealRepository
import com.example.healthtracker.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class GetDashboardDataUseCase(
    private val userRepository: UserRepository,
    private val mealRepository: MealRepository,
    private val exerciseRepository: ExerciseRepository
) {
    operator fun invoke(): Flow<DashboardData> {
        val today = LocalDate.now()
        val startDate = today.minusDays(6)

        return combine(
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

            val remaining = target - consumedToday + burnedToday
            val isExceeded = remaining < 0
            val absRemaining = kotlin.math.abs(remaining)

            val (adviceType, adviceDiff) = when {
                isExceeded -> {
                    DomainAdviceType.OVER_TARGET to absRemaining
                }
                absRemaining <= 100 -> {
                    DomainAdviceType.TARGET_MET to 0
                }
                else -> {
                    DomainAdviceType.UNDER_TARGET to absRemaining
                }
            }

            val last7Days = (0..6).map { today.minusDays(6 - it.toLong()) }
            val barChartList = last7Days.map { date ->
                val dayName = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                val dayMeals = rangeMeals.filter { it.date == date }
                val cal = dayMeals.sumOf { it.totalCalories }.toFloat()
                DailyCalorieData(dayLabel = dayName, calories = cal)
            }

            val lineChartList = last7Days.map { date ->
                val dayName = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                val dayMeals = rangeMeals.filter { it.date == date }
                val dayExercises = rangeExercises.filter { it.date == date }
                val consumed = dayMeals.sumOf { it.totalCalories }.toFloat()
                val burned = dayExercises.sumOf { it.caloriesBurned }.toFloat()
                WeeklyCalorieTrendPoint(label = dayName, caloriesConsumed = consumed, caloriesBurned = burned)
            }

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

            DashboardData(
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
                todayMeals = todayMeals,
                todayExercises = todayExercises
            )
        }
    }
}

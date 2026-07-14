package com.example.healthtracker.domain.usecase

import com.example.healthtracker.domain.model.MealLog
import com.example.healthtracker.domain.repository.MealRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class GetMealsByDateUseCase(
    private val mealRepository: MealRepository
) {
    operator fun invoke(date: LocalDate): Flow<List<MealLog>> = mealRepository.getMealsByDate(date)
}

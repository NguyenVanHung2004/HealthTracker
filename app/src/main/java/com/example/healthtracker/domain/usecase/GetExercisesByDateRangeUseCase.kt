package com.example.healthtracker.domain.usecase

import com.example.healthtracker.domain.model.ExerciseLog
import com.example.healthtracker.domain.repository.ExerciseRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class GetExercisesByDateRangeUseCase(
    private val exerciseRepository: ExerciseRepository
) {
    operator fun invoke(startDate: LocalDate, endDate: LocalDate): Flow<List<ExerciseLog>> {
        return exerciseRepository.getExercisesByDateRange(startDate, endDate)
    }
}

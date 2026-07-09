package com.example.healthtracker.domain.usecase

import com.example.healthtracker.domain.model.ExerciseLog
import com.example.healthtracker.domain.repository.ExerciseRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class GetExercisesByDateUseCase(
    private val exerciseRepository: ExerciseRepository
) {
    operator fun invoke(date: LocalDate): Flow<List<ExerciseLog>> {
        return exerciseRepository.getExercisesByDate(date)
    }
}

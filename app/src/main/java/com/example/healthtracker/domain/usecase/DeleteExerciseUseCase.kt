package com.example.healthtracker.domain.usecase

import com.example.healthtracker.domain.model.ExerciseLog
import com.example.healthtracker.domain.repository.ExerciseRepository

class DeleteExerciseUseCase(
    private val exerciseRepository: ExerciseRepository
) {
    suspend operator fun invoke(exerciseLog: ExerciseLog) {
        exerciseRepository.deleteExercise(exerciseLog)
    }
}

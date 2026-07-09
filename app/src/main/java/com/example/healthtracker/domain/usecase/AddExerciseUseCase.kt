package com.example.healthtracker.domain.usecase

import com.example.healthtracker.domain.model.ExerciseLog
import com.example.healthtracker.domain.model.ExerciseType
import com.example.healthtracker.domain.model.User
import com.example.healthtracker.domain.repository.ExerciseRepository
import com.example.healthtracker.domain.repository.UserRepository
import java.time.LocalDate

import kotlinx.coroutines.flow.firstOrNull

class AddExerciseUseCase(
    private val exerciseRepository: ExerciseRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        type: ExerciseType,
        durationMinutes: Int,
        date: LocalDate = LocalDate.now()
    ) {
        val weight = userRepository.getUser().firstOrNull()?.weightKg ?: 60f

        // Calo = MET * weight(kg) * duration(hours)
        val durationHours = durationMinutes / 60.0
        val caloriesBurned = (type.met * weight * durationHours).toInt()

        val exerciseLog = ExerciseLog(
            type = type,
            durationMinutes = durationMinutes,
            caloriesBurned = caloriesBurned,
            date = date
        )
        exerciseRepository.addExercise(exerciseLog)
    }
}

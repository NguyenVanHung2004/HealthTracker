package com.example.healthtracker.domain.repository

import com.example.healthtracker.domain.model.ExerciseLog
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface ExerciseRepository {
    suspend fun addExercise(exerciseLog: ExerciseLog)
    suspend fun deleteExercise(exerciseLog: ExerciseLog)
    fun getExercisesByDate(date: LocalDate): Flow<List<ExerciseLog>>
}

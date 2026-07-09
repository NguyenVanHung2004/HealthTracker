package com.example.healthtracker.data.repository

import com.example.healthtracker.data.local.dao.ExerciseDao
import com.example.healthtracker.data.local.entity.ExerciseEntity
import com.example.healthtracker.domain.model.ExerciseLog
import com.example.healthtracker.domain.repository.ExerciseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class ExerciseRepositoryImpl(
    private val dao: ExerciseDao
) : ExerciseRepository {

    override suspend fun addExercise(exerciseLog: ExerciseLog) {
        dao.insertExercise(exerciseLog.toEntity())
    }

    override suspend fun deleteExercise(exerciseLog: ExerciseLog) {
        dao.deleteExercise(exerciseLog.toEntity())
    }

    override fun getExercisesByDate(date: LocalDate): Flow<List<ExerciseLog>> {
        return dao.getExercisesByDate(date.toString()).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
}

fun ExerciseLog.toEntity(): ExerciseEntity {
    return ExerciseEntity(
        id = this.id,
        type = this.type,
        durationMinutes = this.durationMinutes,
        caloriesBurned = this.caloriesBurned,
        date = this.date.toString()
    )
}

fun ExerciseEntity.toDomainModel(): ExerciseLog {
    return ExerciseLog(
        id = this.id,
        type = this.type,
        durationMinutes = this.durationMinutes,
        caloriesBurned = this.caloriesBurned,
        date = LocalDate.parse(this.date)
    )
}

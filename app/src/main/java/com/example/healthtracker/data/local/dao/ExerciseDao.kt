package com.example.healthtracker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.healthtracker.data.local.entity.ExerciseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: ExerciseEntity)

    @Delete
    suspend fun deleteExercise(exercise: ExerciseEntity)

    @Query("SELECT * FROM exercises WHERE date = :date ORDER BY id DESC")
    fun getExercisesByDate(date: String): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercises WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC, id DESC")
    fun getExercisesByDateRange(startDate: String, endDate: String): Flow<List<ExerciseEntity>>
}

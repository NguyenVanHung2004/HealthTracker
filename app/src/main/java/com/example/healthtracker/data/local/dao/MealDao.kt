package com.example.healthtracker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.healthtracker.data.local.entity.MealEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: MealEntity)

    @Delete
    suspend fun deleteMeal(meal: MealEntity)

    @Query("SELECT * FROM meals WHERE date = :date ORDER BY id DESC")
    fun getMealsByDate(date: String): Flow<List<MealEntity>>

    @Query("SELECT * FROM meals WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getMealsByDateRange(startDate: String, endDate: String): Flow<List<MealEntity>>
}

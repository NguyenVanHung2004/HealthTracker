package com.example.healthtracker.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.healthtracker.data.local.entity.FoodItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodItems(items: List<FoodItemEntity>)

    @Query("SELECT * FROM food_items ORDER BY name ASC")
    fun getAllFoodItems(): Flow<List<FoodItemEntity>>

    @Query("SELECT * FROM food_items WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchFoodItems(query: String): Flow<List<FoodItemEntity>>

    @Query("SELECT COUNT(*) FROM food_items")
    suspend fun getFoodItemCount(): Int
}

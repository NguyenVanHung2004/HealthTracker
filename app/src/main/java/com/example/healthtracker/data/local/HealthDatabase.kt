package com.example.healthtracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.healthtracker.data.local.dao.ExerciseDao
import com.example.healthtracker.data.local.dao.UserDao
import com.example.healthtracker.data.local.dao.MealDao
import com.example.healthtracker.data.local.dao.FoodItemDao
import com.example.healthtracker.data.local.entity.ExerciseEntity
import com.example.healthtracker.data.local.entity.UserEntity
import com.example.healthtracker.data.local.entity.MealEntity
import com.example.healthtracker.data.local.entity.FoodItemEntity

@Database(entities = [UserEntity::class, ExerciseEntity::class, MealEntity::class, FoodItemEntity::class], version = 4, exportSchema = false)
abstract class HealthDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun mealDao(): MealDao
    abstract fun foodItemDao(): FoodItemDao
}

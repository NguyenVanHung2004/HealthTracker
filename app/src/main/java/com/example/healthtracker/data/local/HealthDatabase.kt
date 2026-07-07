package com.example.healthtracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.healthtracker.data.local.dao.UserDao
import com.example.healthtracker.data.local.entity.UserEntity

@Database(entities = [UserEntity::class], version = 1, exportSchema = false)
abstract class HealthDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}

package com.example.healthtracker.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.example.healthtracker.data.local.entity.UserEntity

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = 1 LIMIT 1")
    fun getUser(): Flow<UserEntity?>
}

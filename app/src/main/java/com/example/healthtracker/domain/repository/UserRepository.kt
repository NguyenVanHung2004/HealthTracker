package com.example.healthtracker.domain.repository

import com.example.healthtracker.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUser(): Flow<User?>
    suspend fun saveUser(user: User)
}

package com.example.healthtracker.data.repository

import com.example.healthtracker.data.local.dao.UserDao
import com.example.healthtracker.data.local.entity.toEntity
import com.example.healthtracker.domain.model.User
import com.example.healthtracker.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepositoryImpl(
    private val userDao: UserDao
) : UserRepository {
    override fun getUser(): Flow<User?> {
        return userDao.getUser().map { it?.toDomain() }
    }

    override suspend fun saveUser(user: User) {
        userDao.insertUser(user.toEntity())
    }
}

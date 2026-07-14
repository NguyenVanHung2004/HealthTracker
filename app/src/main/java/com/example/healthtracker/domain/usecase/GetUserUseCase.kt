package com.example.healthtracker.domain.usecase

import com.example.healthtracker.domain.model.User
import com.example.healthtracker.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class GetUserUseCase(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<User?> = userRepository.getUser()
}

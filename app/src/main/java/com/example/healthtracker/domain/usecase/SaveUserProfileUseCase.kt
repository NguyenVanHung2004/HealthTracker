package com.example.healthtracker.domain.usecase

import com.example.healthtracker.data.local.preferences.UserPreferences
import com.example.healthtracker.domain.model.User
import com.example.healthtracker.domain.repository.UserRepository

class SaveUserProfileUseCase(
    private val userRepository: UserRepository,
    private val userPreferences: UserPreferences
) {
    suspend operator fun invoke(user: User) {
        userRepository.saveUser(user)
        userPreferences.setOnboardingCompleted(true)
    }
}

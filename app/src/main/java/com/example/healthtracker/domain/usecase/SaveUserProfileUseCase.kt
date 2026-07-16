package com.example.healthtracker.domain.usecase

import com.example.healthtracker.data.local.preferences.UserPreferences
import com.example.healthtracker.domain.model.User
import com.example.healthtracker.domain.repository.UserRepository

class SaveUserProfileUseCase(
    private val userRepository: UserRepository,
    private val userPreferences: UserPreferences,
    private val validateUserProfileUseCase: ValidateUserProfileUseCase
) {
    suspend operator fun invoke(user: User) {
        // Validate name
        val nameResult = validateUserProfileUseCase.validateName(user.name)
        require(nameResult.successful) { "Invalid name" }

        // Validate date of birth
        val dobResult = validateUserProfileUseCase.validateDateOfBirth(user.dateOfBirth)
        require(dobResult.successful) { "Invalid date of birth" }

        // Validate weight
        val weightResult = validateUserProfileUseCase.validateWeight(user.weightKg)
        require(weightResult.successful) { "Invalid weight" }

        // Validate height
        val heightResult = validateUserProfileUseCase.validateHeight(user.heightCm)
        require(heightResult.successful) { "Invalid height" }

        userRepository.saveUser(user)
        userPreferences.setOnboardingCompleted(true)
    }
}

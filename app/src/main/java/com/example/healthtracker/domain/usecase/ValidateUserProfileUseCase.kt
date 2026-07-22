package com.example.healthtracker.domain.usecase

import java.time.LocalDate
import java.time.Period
class ValidateUserProfileUseCase {
    fun validateName(name: String): ValidationResult {
        if (name.isBlank()) {
            return ValidationResult(false, ValidationError.EMPTY_NAME)
        }
        return ValidationResult(true)
    }

    fun validateDateOfBirth(dob: LocalDate?): ValidationResult {
        if (dob == null) {
            return ValidationResult(false, ValidationError.INVALID_DOB)
        }
        if (dob.isAfter(LocalDate.now())) {
            return ValidationResult(false, ValidationError.INVALID_DOB)
        }
        val age = Period.between(dob, LocalDate.now()).years
        if (age <= 0 || age > 120) {
            return ValidationResult(false, ValidationError.INVALID_DOB)
        }
        return ValidationResult(true)
    }

    fun validateWeight(weight: Float): ValidationResult {
        if (weight <= 0f) {
            return ValidationResult(false, ValidationError.INVALID_WEIGHT)
        }
        return ValidationResult(true)
    }

    fun validateHeight(height: Float): ValidationResult {
        if (height <= 0f) {
            return ValidationResult(false, ValidationError.INVALID_HEIGHT)
        }
        return ValidationResult(true)
    }
}

enum class ValidationError {
    EMPTY_NAME,
    INVALID_DOB,
    INVALID_WEIGHT,
    INVALID_HEIGHT
}

data class ValidationResult(
    val successful: Boolean,
    val error: ValidationError? = null
)

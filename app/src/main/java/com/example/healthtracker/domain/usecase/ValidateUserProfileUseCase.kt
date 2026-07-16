package com.example.healthtracker.domain.usecase

import java.time.LocalDate
import java.time.Period
import com.example.healthtracker.R

class ValidateUserProfileUseCase {
    fun validateName(name: String): ValidationResult {
        if (name.isBlank()) {
            return ValidationResult(false, R.string.error_empty_name)
        }
        return ValidationResult(true)
    }

    fun validateDateOfBirth(dob: LocalDate?): ValidationResult {
        if (dob == null) {
            return ValidationResult(false, R.string.error_invalid_dob)
        }
        if (dob.isAfter(LocalDate.now())) {
            return ValidationResult(false, R.string.error_invalid_dob)
        }
        val age = Period.between(dob, LocalDate.now()).years
        if (age <= 0 || age > 120) {
            return ValidationResult(false, R.string.error_invalid_dob)
        }
        return ValidationResult(true)
    }

    fun validateWeight(weight: Float): ValidationResult {
        if (weight <= 0f) {
            return ValidationResult(false, R.string.error_invalid_weight)
        }
        return ValidationResult(true)
    }

    fun validateHeight(height: Float): ValidationResult {
        if (height <= 0f) {
            return ValidationResult(false, R.string.error_invalid_height)
        }
        return ValidationResult(true)
    }
}

data class ValidationResult(
    val successful: Boolean,
    val errorMessageId: Int? = null
)

package com.example.healthtracker.domain.usecase

import com.example.healthtracker.domain.model.Gender
import java.time.LocalDate
import java.time.Period

class CalculateBMRUseCase {
    operator fun invoke(weightKg: Float, heightCm: Float, gender: Gender, dob: LocalDate): Double {
        val age = Period.between(dob, LocalDate.now()).years
        return if (gender == Gender.MALE) {
            (10 * weightKg) + (6.25 * heightCm) - (5 * age) + 5
        } else {
            (10 * weightKg) + (6.25 * heightCm) - (5 * age) - 161
        }
    }
}

package com.example.healthtracker.domain.usecase

class CalculateBMIUseCase {
    operator fun invoke(weightKg: Float, heightCm: Float): Float {
        if (heightCm == 0f) return 0f
        val heightM = heightCm / 100
        return weightKg / (heightM * heightM)
    }
}

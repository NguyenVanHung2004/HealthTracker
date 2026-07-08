package com.example.healthtracker.domain.model

enum class ActivityLevel(val factor: Double) {
    SEDENTARY(1.2), // Ít vận động
    LIGHTLY_ACTIVE(1.375), // Vận động nhẹ
    MODERATELY_ACTIVE(1.55), // Vận động vừa
    VERY_ACTIVE(1.725), // Vận động nhiều
    EXTRA_ACTIVE(1.9) // Vận động rất nhiều
}

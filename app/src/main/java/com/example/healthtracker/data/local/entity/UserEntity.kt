package com.example.healthtracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.healthtracker.domain.model.ActivityLevel
import com.example.healthtracker.domain.model.Gender
import com.example.healthtracker.domain.model.Goal
import com.example.healthtracker.domain.model.User
import java.time.LocalDate

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val dateOfBirth: String, // Store as ISO String
    val gender: String, // Enum name
    val weightKg: Float,
    val heightCm: Float,
    val activityLevel: String, // Enum name
    val goal: String, // Enum name
    val tdee: Int,
    val targetCalories: Int,
    val bmi: Float
) {
    fun toDomain(): User {
        return User(
            name = name,
            dateOfBirth = LocalDate.parse(dateOfBirth),
            gender = Gender.valueOf(gender),
            weightKg = weightKg,
            heightCm = heightCm,
            activityLevel = ActivityLevel.valueOf(activityLevel),
            goal = Goal.valueOf(goal),
            tdee = tdee,
            targetCalories = targetCalories,
            bmi = bmi
        )
    }
}

fun User.toEntity(): UserEntity {
    return UserEntity(
        name = name,
        dateOfBirth = dateOfBirth.toString(),
        gender = gender.name,
        weightKg = weightKg,
        heightCm = heightCm,
        activityLevel = activityLevel.name,
        goal = goal.name,
        tdee = tdee,
        targetCalories = targetCalories,
        bmi = bmi
    )
}

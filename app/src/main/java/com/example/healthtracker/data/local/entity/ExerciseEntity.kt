package com.example.healthtracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.healthtracker.domain.model.ExerciseType

@Entity(tableName = "exercises")
data class ExerciseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val type: ExerciseType,
    val durationMinutes: Int,
    val caloriesBurned: Int,
    val date: String // stored as ISO String (e.g. 2026-07-09)
)

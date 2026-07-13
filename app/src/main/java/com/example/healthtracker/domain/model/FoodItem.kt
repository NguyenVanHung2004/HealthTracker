package com.example.healthtracker.domain.model

data class FoodItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val calories: Int,
    val servingInfo: String
)

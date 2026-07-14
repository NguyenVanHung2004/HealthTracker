package com.example.healthtracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.healthtracker.domain.model.FoodItem

@Entity(tableName = "food_items")
data class FoodItemEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val calories: Int,
    val servingInfo: String
) {
    fun toDomainModel(): FoodItem {
        return FoodItem(
            id = id,
            name = name,
            calories = calories,
            servingInfo = servingInfo
        )
    }

    companion object {
        fun fromDomainModel(model: FoodItem): FoodItemEntity {
            return FoodItemEntity(
                id = model.id,
                name = model.name,
                calories = model.calories,
                servingInfo = model.servingInfo
            )
        }
    }
}

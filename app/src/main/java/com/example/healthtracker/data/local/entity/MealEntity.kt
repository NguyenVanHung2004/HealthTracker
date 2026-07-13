package com.example.healthtracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.healthtracker.domain.model.MealLog
import com.example.healthtracker.domain.model.MealType
import java.time.LocalDate

@Entity(tableName = "meals")
data class MealEntity(
    @PrimaryKey
    val id: String,
    val date: String, // stored as ISO String (e.g. 2026-07-09)
    val mealType: MealType,
    val foodName: String,
    val caloriesPerServing: Int,
    val servingInfo: String,
    val quantity: Double
) {
    fun toDomainModel(): MealLog {
        return MealLog(
            id = id,
            date = LocalDate.parse(date),
            mealType = mealType,
            foodName = foodName,
            caloriesPerServing = caloriesPerServing,
            servingInfo = servingInfo,
            quantity = quantity
        )
    }

    companion object {
        fun fromDomainModel(model: MealLog): MealEntity {
            return MealEntity(
                id = model.id,
                date = model.date.toString(),
                mealType = model.mealType,
                foodName = model.foodName,
                caloriesPerServing = model.caloriesPerServing,
                servingInfo = model.servingInfo,
                quantity = model.quantity
            )
        }
    }
}

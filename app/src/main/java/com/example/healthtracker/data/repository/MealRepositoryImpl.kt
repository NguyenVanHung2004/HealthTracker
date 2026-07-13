package com.example.healthtracker.data.repository

import com.example.healthtracker.data.local.dao.MealDao
import com.example.healthtracker.data.local.dao.FoodItemDao
import com.example.healthtracker.data.local.entity.MealEntity
import com.example.healthtracker.data.local.entity.FoodItemEntity
import com.example.healthtracker.domain.model.FoodItem
import com.example.healthtracker.domain.model.MealLog
import com.example.healthtracker.domain.repository.MealRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class MealRepositoryImpl(
    private val mealDao: MealDao,
    private val foodItemDao: FoodItemDao
) : MealRepository {

    override suspend fun insertMeal(meal: MealLog) {
        mealDao.insertMeal(MealEntity.fromDomainModel(meal))
    }

    override suspend fun deleteMeal(meal: MealLog) {
        mealDao.deleteMeal(MealEntity.fromDomainModel(meal))
    }

    override fun getMealsByDate(date: LocalDate): Flow<List<MealLog>> {
        return mealDao.getMealsByDate(date.toString()).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getMealsByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<MealLog>> {
        return mealDao.getMealsByDateRange(startDate.toString(), endDate.toString()).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getAllFoodItems(): Flow<List<FoodItem>> {
        return foodItemDao.getAllFoodItems().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun searchFoodItems(query: String): Flow<List<FoodItem>> {
        return foodItemDao.searchFoodItems(query).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun insertFoodItems(items: List<FoodItem>) {
        foodItemDao.insertFoodItems(items.map { FoodItemEntity.fromDomainModel(it) })
    }

    override suspend fun getFoodItemCount(): Int {
        return foodItemDao.getFoodItemCount()
    }
}

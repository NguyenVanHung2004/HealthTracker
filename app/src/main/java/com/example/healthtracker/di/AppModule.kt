package com.example.healthtracker.di

import androidx.room.Room
import com.example.healthtracker.data.alarm.AlarmSchedulerImpl
import com.example.healthtracker.data.local.HealthDatabase
import com.example.healthtracker.data.local.preferences.UserPreferences
import com.example.healthtracker.data.repository.ExerciseRepositoryImpl
import com.example.healthtracker.data.repository.MealRepositoryImpl
import com.example.healthtracker.data.repository.UserRepositoryImpl
import com.example.healthtracker.domain.alarm.AlarmScheduler
import com.example.healthtracker.domain.repository.ExerciseRepository
import com.example.healthtracker.domain.repository.MealRepository
import com.example.healthtracker.domain.repository.UserRepository
import com.example.healthtracker.domain.usecase.AddExerciseUseCase
import com.example.healthtracker.domain.usecase.CalculateBMIUseCase
import com.example.healthtracker.domain.usecase.CalculateBMRUseCase
import com.example.healthtracker.domain.usecase.CalculateTDEEUseCase
import com.example.healthtracker.domain.usecase.DeleteExerciseUseCase
import com.example.healthtracker.domain.usecase.GetExercisesByDateUseCase
import com.example.healthtracker.domain.usecase.GetExercisesByDateRangeUseCase
import com.example.healthtracker.domain.usecase.SaveUserProfileUseCase
import com.example.healthtracker.domain.usecase.GetDashboardDataUseCase
import com.example.healthtracker.domain.usecase.GetUserUseCase
import com.example.healthtracker.domain.usecase.SeedFoodItemsUseCase
import com.example.healthtracker.domain.usecase.GetAllFoodItemsUseCase
import com.example.healthtracker.domain.usecase.GetMealsByDateUseCase
import com.example.healthtracker.domain.usecase.SearchFoodItemsUseCase
import com.example.healthtracker.domain.usecase.AddMealUseCase
import com.example.healthtracker.domain.usecase.DeleteMealUseCase
import com.example.healthtracker.domain.usecase.AddFoodItemUseCase
import com.example.healthtracker.domain.usecase.ValidateUserProfileUseCase
import com.example.healthtracker.presentation.activity.ActivityViewModel
import com.example.healthtracker.presentation.onboarding.OnboardingViewModel
import com.example.healthtracker.presentation.settings.SettingsViewModel
import com.example.healthtracker.presentation.meal.MealViewModel
import com.example.healthtracker.presentation.dashboard.DashboardViewModel
import com.example.healthtracker.presentation.widget.WidgetUpdater
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            HealthDatabase::class.java,
            "health_tracker_db"
        ).fallbackToDestructiveMigration()
         .build()
    }

    single { get<HealthDatabase>().userDao() }
    single { get<HealthDatabase>().exerciseDao() }
    single { get<HealthDatabase>().mealDao() }
    single { get<HealthDatabase>().foodItemDao() }

    single { UserPreferences(androidContext()) }

    single<UserRepository> { UserRepositoryImpl(get()) }
    single<ExerciseRepository> { ExerciseRepositoryImpl(get()) }
    single<MealRepository> { MealRepositoryImpl(get(), get()) }
    single<AlarmScheduler> { AlarmSchedulerImpl(androidContext()) }
    
    single { WidgetUpdater(androidContext(), get()) }

    factory { CalculateBMRUseCase() }
    factory { CalculateTDEEUseCase() }
    factory { CalculateBMIUseCase() }
    factory { SaveUserProfileUseCase(get(), get(), get()) }
    factory { ValidateUserProfileUseCase() }
    factory { AddExerciseUseCase(get(), get()) }
    factory { GetExercisesByDateUseCase(get()) }
    factory { GetExercisesByDateRangeUseCase(get()) }
    factory { DeleteExerciseUseCase(get()) }
    factory { GetDashboardDataUseCase(get(), get(), get()) }
    factory { GetUserUseCase(get()) }
    factory { SeedFoodItemsUseCase(get()) }
    factory { GetAllFoodItemsUseCase(get()) }
    factory { GetMealsByDateUseCase(get()) }
    factory { SearchFoodItemsUseCase(get()) }
    factory { AddMealUseCase(get()) }
    factory { DeleteMealUseCase(get()) }
    factory { AddFoodItemUseCase(get()) }

    viewModel { OnboardingViewModel(get(), get(), get(), get(), get()) }
    viewModel { SettingsViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { ActivityViewModel(get(), get(), get(), get(), get()) }
    viewModel { MealViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { DashboardViewModel(get()) }
}

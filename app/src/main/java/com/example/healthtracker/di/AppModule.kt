package com.example.healthtracker.di

import androidx.room.Room
import com.example.healthtracker.data.local.HealthDatabase
import com.example.healthtracker.data.local.preferences.UserPreferences
import com.example.healthtracker.data.repository.ExerciseRepositoryImpl
import com.example.healthtracker.data.repository.MealRepositoryImpl
import com.example.healthtracker.data.repository.UserRepositoryImpl
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
import com.example.healthtracker.presentation.activity.ActivityViewModel
import com.example.healthtracker.presentation.onboarding.OnboardingViewModel
import com.example.healthtracker.presentation.settings.SettingsViewModel
import com.example.healthtracker.presentation.meal.MealViewModel
import com.example.healthtracker.presentation.dashboard.DashboardViewModel
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

    factory { CalculateBMRUseCase() }
    factory { CalculateTDEEUseCase() }
    factory { CalculateBMIUseCase() }
    factory { SaveUserProfileUseCase(get(), get()) }
    factory { AddExerciseUseCase(get(), get()) }
    factory { GetExercisesByDateUseCase(get()) }
    factory { GetExercisesByDateRangeUseCase(get()) }
    factory { DeleteExerciseUseCase(get()) }

    viewModel { OnboardingViewModel(get(), get(), get(), get()) }
    viewModel { SettingsViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { ActivityViewModel(get(), get(), get(), get()) }
    viewModel { MealViewModel(get(), get()) }
    viewModel { DashboardViewModel(get(), get(), get()) }
}

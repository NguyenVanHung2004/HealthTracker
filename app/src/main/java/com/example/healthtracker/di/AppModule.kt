package com.example.healthtracker.di

import androidx.room.Room
import com.example.healthtracker.data.local.HealthDatabase
import com.example.healthtracker.data.local.preferences.UserPreferences
import com.example.healthtracker.data.repository.UserRepositoryImpl
import com.example.healthtracker.domain.repository.UserRepository
import com.example.healthtracker.domain.usecase.CalculateBMIUseCase
import com.example.healthtracker.domain.usecase.CalculateBMRUseCase
import com.example.healthtracker.domain.usecase.CalculateTDEEUseCase
import com.example.healthtracker.domain.usecase.SaveUserProfileUseCase
import com.example.healthtracker.presentation.onboarding.OnboardingViewModel
import com.example.healthtracker.presentation.settings.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            HealthDatabase::class.java,
            "health_tracker_db"
        ).build()
    }

    single { get<HealthDatabase>().userDao() }

    single { UserPreferences(androidContext()) }

    single<UserRepository> { UserRepositoryImpl(get()) }

    factory { CalculateBMRUseCase() }
    factory { CalculateTDEEUseCase() }
    factory { CalculateBMIUseCase() }
    factory { SaveUserProfileUseCase(get(), get()) }

    viewModel { OnboardingViewModel(get(), get(), get(), get()) }
    viewModel { SettingsViewModel(get(), get(), get(), get(), get(), get()) }
}

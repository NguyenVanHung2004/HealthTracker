package com.example.healthtracker.di

import androidx.room.Room
import com.example.healthtracker.data.local.HealthDatabase
import com.example.healthtracker.data.local.preferences.UserPreferences
import org.koin.android.ext.koin.androidContext
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
}

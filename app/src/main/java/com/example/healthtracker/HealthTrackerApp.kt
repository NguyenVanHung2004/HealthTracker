package com.example.healthtracker

import android.app.Application
import com.example.healthtracker.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class HealthTrackerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@HealthTrackerApp)
            modules(appModule)
        }
    }
}

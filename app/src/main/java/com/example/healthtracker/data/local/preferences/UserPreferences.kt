package com.example.healthtracker.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UserPreferences(
    private val context: Context
) {
    companion object {
        val IS_ONBOARDING_COMPLETED = booleanPreferencesKey("is_onboarding_completed")
    }

    val isOnboardingCompleted: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[IS_ONBOARDING_COMPLETED] ?: false
        }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_ONBOARDING_COMPLETED] = completed
        }
    }
}

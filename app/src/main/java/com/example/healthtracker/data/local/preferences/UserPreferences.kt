package com.example.healthtracker.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
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
        val THEME_PREFERENCE = stringPreferencesKey("theme_preference") // "system", "light", "dark"
        val LANGUAGE_PREFERENCE = stringPreferencesKey("language_preference") // "vi", "en"
        val FONT_SIZE_PREFERENCE = stringPreferencesKey("font_size_preference") // "small", "medium", "large"
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")

        // SharedPreferences constants for synchronous locale access in attachBaseContext()
        const val SHARED_PREFS_NAME = "health_tracker_prefs"
        const val LANGUAGE_PREF_KEY = "language_pref_key"
    }

    val isOnboardingCompleted: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[IS_ONBOARDING_COMPLETED] ?: false
        }

    val themePreference: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[THEME_PREFERENCE] ?: "system"
        }

    val languagePreference: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[LANGUAGE_PREFERENCE] ?: "vi"
        }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun setThemePreference(theme: String) {
        context.dataStore.edit { preferences ->
            preferences[THEME_PREFERENCE] = theme
        }
    }

    suspend fun setLanguagePreference(language: String) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE_PREFERENCE] = language
        }
        // Also write to SharedPreferences for synchronous access in attachBaseContext()
        context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(LANGUAGE_PREF_KEY, language)
            .apply()
    }

    val fontSizePreference: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[FONT_SIZE_PREFERENCE] ?: "medium"
        }

    suspend fun setFontSizePreference(size: String) {
        context.dataStore.edit { preferences ->
            preferences[FONT_SIZE_PREFERENCE] = size
        }
    }

    val notificationsEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[NOTIFICATIONS_ENABLED] ?: false
        }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED] = enabled
        }
    }
}

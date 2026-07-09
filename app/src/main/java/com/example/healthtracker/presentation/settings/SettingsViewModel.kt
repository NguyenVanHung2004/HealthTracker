package com.example.healthtracker.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthtracker.data.local.preferences.UserPreferences
import com.example.healthtracker.domain.model.ActivityLevel
import com.example.healthtracker.domain.model.Gender
import com.example.healthtracker.domain.model.Goal
import com.example.healthtracker.domain.model.User
import com.example.healthtracker.domain.repository.UserRepository
import com.example.healthtracker.domain.usecase.CalculateBMIUseCase
import com.example.healthtracker.domain.usecase.CalculateBMRUseCase
import com.example.healthtracker.domain.usecase.CalculateTDEEUseCase
import com.example.healthtracker.domain.usecase.SaveUserProfileUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class SettingsUiState(
    val user: User? = null,
    val isLoading: Boolean = true,
    val name: String = "",
    val weight: String = "",
    val height: String = "",
    val gender: Gender = Gender.MALE,
    val activityLevel: ActivityLevel = ActivityLevel.SEDENTARY,
    val goal: Goal = Goal.MAINTAIN_WEIGHT
)

class SettingsViewModel(
    private val userRepository: UserRepository,
    private val userPreferences: UserPreferences,
    private val calculateBMRUseCase: CalculateBMRUseCase,
    private val calculateTDEEUseCase: CalculateTDEEUseCase,
    private val calculateBMIUseCase: CalculateBMIUseCase,
    private val saveUserProfileUseCase: SaveUserProfileUseCase
) : ViewModel() {

    val themePreference: StateFlow<String> = userPreferences.themePreference.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        "system"
    )

    val languagePreference: StateFlow<String> = userPreferences.languagePreference.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        "vi"
    )

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            userRepository.getUser().collect { user ->
                if (user != null) {
                    _uiState.update {
                        it.copy(
                            user = user,
                            isLoading = false,
                            name = user.name,
                            weight = user.weightKg.toString(),
                            height = user.heightCm.toInt().toString(),
                            gender = user.gender,
                            activityLevel = user.activityLevel,
                            goal = user.goal
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun onNameChange(name: String) { _uiState.update { it.copy(name = name) } }
    fun onWeightChange(weight: String) { _uiState.update { it.copy(weight = weight) } }
    fun onHeightChange(height: String) { _uiState.update { it.copy(height = height) } }
    fun onGenderChange(gender: Gender) { _uiState.update { it.copy(gender = gender) } }
    fun onActivityLevelChange(level: ActivityLevel) { _uiState.update { it.copy(activityLevel = level) } }
    fun onGoalChange(goal: Goal) { _uiState.update { it.copy(goal = goal) } }

    private val _snackbarEvent = MutableSharedFlow<Int>()
    val snackbarEvent = _snackbarEvent.asSharedFlow()

    fun saveProfile() {
        viewModelScope.launch {
            val currentState = _uiState.value
            val currentUser = currentState.user ?: return@launch
            
            val newWeight = currentState.weight.toFloatOrNull() ?: currentUser.weightKg
            val newHeight = currentState.height.toFloatOrNull() ?: currentUser.heightCm

            val bmr = calculateBMRUseCase(newWeight, newHeight, currentState.gender, currentUser.dateOfBirth)
            val tdee = calculateTDEEUseCase(bmr, currentState.activityLevel, currentState.goal)
            val bmi = calculateBMIUseCase(newWeight, newHeight)

            val updatedUser = currentUser.copy(
                name = currentState.name,
                weightKg = newWeight,
                heightCm = newHeight,
                gender = currentState.gender,
                activityLevel = currentState.activityLevel,
                goal = currentState.goal,
                targetCalories = tdee,
                bmi = bmi
            )

            saveUserProfileUseCase(updatedUser)
            _snackbarEvent.emit(com.example.healthtracker.R.string.profile_saved_successfully)
        }
    }

    fun updateTheme(theme: String) {
        viewModelScope.launch {
            userPreferences.setThemePreference(theme)
        }
    }

    fun updateLanguage(language: String) {
        viewModelScope.launch {
            userPreferences.setLanguagePreference(language)
        }
    }
}

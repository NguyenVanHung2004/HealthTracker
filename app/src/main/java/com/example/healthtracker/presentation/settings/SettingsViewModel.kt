package com.example.healthtracker.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthtracker.R
import com.example.healthtracker.data.local.preferences.UserPreferences
import com.example.healthtracker.domain.model.ActivityLevel
import com.example.healthtracker.domain.model.Gender
import com.example.healthtracker.domain.model.Goal
import com.example.healthtracker.domain.model.User
import com.example.healthtracker.domain.usecase.GetUserUseCase
import com.example.healthtracker.domain.usecase.CalculateBMIUseCase
import com.example.healthtracker.domain.usecase.CalculateBMRUseCase
import com.example.healthtracker.domain.usecase.CalculateTDEEUseCase
import com.example.healthtracker.domain.usecase.SaveUserProfileUseCase
import com.example.healthtracker.domain.usecase.ValidateUserProfileUseCase
import com.example.healthtracker.domain.usecase.ValidationError
import com.example.healthtracker.presentation.widget.WidgetUpdater
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
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import com.example.healthtracker.presentation.components.LoadingController
import kotlinx.coroutines.delay
import com.example.healthtracker.domain.alarm.AlarmScheduler
import kotlinx.coroutines.flow.SharedFlow

data class SettingsUiState(
    val user: User? = null,
    val isLoading: Boolean = true,
    val name: String = "",
    val weight: String = "",
    val height: String = "",
    val dateOfBirth: String = "",
    val bmi: Float = 0f,
    val gender: Gender = Gender.MALE,
    val activityLevel: ActivityLevel = ActivityLevel.SEDENTARY,
    val goal: Goal = Goal.MAINTAIN_WEIGHT
)

class SettingsViewModel(
    private val getUserUseCase: GetUserUseCase,
    private val userPreferences: UserPreferences,
    private val calculateBMRUseCase: CalculateBMRUseCase,
    private val calculateTDEEUseCase: CalculateTDEEUseCase,
    private val calculateBMIUseCase: CalculateBMIUseCase,
    private val saveUserProfileUseCase: SaveUserProfileUseCase,
    private val alarmScheduler: AlarmScheduler,
    private val validateUserProfileUseCase: ValidateUserProfileUseCase,
    private val widgetUpdater: WidgetUpdater
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

    val fontSizePreference: StateFlow<String> = userPreferences.fontSizePreference.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        "medium"
    )

    val notificationsEnabled: StateFlow<Boolean> = userPreferences.notificationsEnabled.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        false
    )

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            getUserUseCase().collect { user ->
                if (user != null) {
                    _uiState.update {
                        it.copy(
                            user = user,
                            isLoading = false,
                            name = user.name,
                            weight = user.weightKg.toString(),
                            height = user.heightCm.toInt().toString(),
                            dateOfBirth = user.dateOfBirth.format(dateFormatter),
                            bmi = user.bmi,
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

    private val _uiEvent = MutableSharedFlow<SettingsUiEvent>()
    val uiEvent: SharedFlow<SettingsUiEvent> = _uiEvent.asSharedFlow()

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.OnNameChanged -> _uiState.update { it.copy(name = event.name) }
            is SettingsEvent.OnWeightChanged -> _uiState.update { it.copy(weight = event.weight) }
            is SettingsEvent.OnHeightChanged -> _uiState.update { it.copy(height = event.height) }
            is SettingsEvent.OnDateOfBirthChanged -> _uiState.update { it.copy(dateOfBirth = event.dob) }
            is SettingsEvent.OnGenderChanged -> _uiState.update { it.copy(gender = event.gender) }
            is SettingsEvent.OnActivityLevelChanged -> _uiState.update { it.copy(activityLevel = event.level) }
            is SettingsEvent.OnGoalChanged -> _uiState.update { it.copy(goal = event.goal) }
            is SettingsEvent.OnThemeChanged -> updateTheme(event.theme)
            is SettingsEvent.OnLanguageChanged -> updateLanguage(event.language)
            is SettingsEvent.OnFontSizeChanged -> updateFontSize(event.size)
            is SettingsEvent.OnNotificationsToggled -> toggleNotifications(event.enabled)
            is SettingsEvent.OnSaveProfile -> saveProfile()
            is SettingsEvent.OnTestNotification -> testNotification()
        }
    }

    private fun emitEvent(event: SettingsUiEvent) {
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }

    private fun saveProfile() {
        viewModelScope.launch {
            val currentState = _uiState.value
            
            // Validate name
            val nameResult = validateUserProfileUseCase.validateName(currentState.name)
            if (!nameResult.successful) {
                nameResult.error?.let { emitEvent(SettingsUiEvent.ShowSnackbar(it.toErrorMessageId(), isError = true)) }
                return@launch
            }

            // Validate dateOfBirth
            val birthDate = try {
                LocalDate.parse(currentState.dateOfBirth, dateFormatter)
            } catch (e: DateTimeParseException) {
                null
            }
            val dobResult = validateUserProfileUseCase.validateDateOfBirth(birthDate)
            if (!dobResult.successful) {
                dobResult.error?.let { emitEvent(SettingsUiEvent.ShowSnackbar(it.toErrorMessageId(), isError = true)) }
                return@launch
            }

            // Validate weight
            val newWeight = currentState.weight.replace(",", ".").toFloatOrNull() ?: -1f
            val weightResult = validateUserProfileUseCase.validateWeight(newWeight)
            if (!weightResult.successful) {
                weightResult.error?.let { emitEvent(SettingsUiEvent.ShowSnackbar(it.toErrorMessageId(), isError = true)) }
                return@launch
            }

            // Validate height
            val newHeight = currentState.height.replace(",", ".").toFloatOrNull() ?: -1f
            val heightResult = validateUserProfileUseCase.validateHeight(newHeight)
            if (!heightResult.successful) {
                heightResult.error?.let { emitEvent(SettingsUiEvent.ShowSnackbar(it.toErrorMessageId(), isError = true)) }
                return@launch
            }

            val bmr = calculateBMRUseCase(newWeight, newHeight, currentState.gender, birthDate!!)
            val tdeeResult = calculateTDEEUseCase(bmr, currentState.activityLevel, currentState.goal)
            val bmi = calculateBMIUseCase(newWeight, newHeight)

            val updatedUser = User(
                name = currentState.name,
                dateOfBirth = birthDate,
                gender = currentState.gender,
                weightKg = newWeight,
                heightCm = newHeight,
                activityLevel = currentState.activityLevel,
                goal = currentState.goal,
                tdee = tdeeResult.maintenance,
                targetCalories = tdeeResult.target,
                bmi = bmi
            )

            try {
                LoadingController.withLoading {
                    delay(1000)
                    saveUserProfileUseCase(updatedUser)
                }
                widgetUpdater.updateWidget()
                emitEvent(SettingsUiEvent.ShowSnackbar(R.string.profile_saved_successfully))
            } catch (e: Exception) {
                emitEvent(SettingsUiEvent.ShowSnackbar(R.string.error_occurred, isError = true))
            }
        }
    }

    private fun updateTheme(theme: String) {
        viewModelScope.launch {
            userPreferences.setThemePreference(theme)
        }
    }

    private fun updateLanguage(language: String) {
        viewModelScope.launch {
            userPreferences.setLanguagePreference(language)
        }
    }

    private fun updateFontSize(size: String) {
        viewModelScope.launch {
            userPreferences.setFontSizePreference(size)
        }
    }

    private fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setNotificationsEnabled(enabled)
            if (enabled) {
                alarmScheduler.scheduleDailyReminders()
                emitEvent(SettingsUiEvent.ShowSnackbar(R.string.toast_notifications_enabled))
            } else {
                alarmScheduler.cancelReminders()
                emitEvent(SettingsUiEvent.ShowSnackbar(R.string.toast_notifications_disabled))
            }
        }
    }

    private fun testNotification() {
        alarmScheduler.testNotification()
        emitEvent(SettingsUiEvent.ShowSnackbar(R.string.toast_test_notification_sent))
    }
}

private fun ValidationError.toErrorMessageId(): Int = when(this) {
    ValidationError.EMPTY_NAME -> R.string.error_empty_name
    ValidationError.INVALID_DOB -> R.string.error_invalid_dob
    ValidationError.INVALID_WEIGHT -> R.string.error_invalid_weight
    ValidationError.INVALID_HEIGHT -> R.string.error_invalid_height
}

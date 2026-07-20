package com.example.healthtracker.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthtracker.domain.model.ActivityLevel
import com.example.healthtracker.domain.model.Gender
import com.example.healthtracker.domain.model.Goal
import com.example.healthtracker.domain.model.User
import com.example.healthtracker.domain.usecase.CalculateBMIUseCase
import com.example.healthtracker.domain.usecase.CalculateBMRUseCase
import com.example.healthtracker.domain.usecase.CalculateTDEEUseCase
import com.example.healthtracker.domain.usecase.SaveUserProfileUseCase
import com.example.healthtracker.domain.usecase.ValidateUserProfileUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.example.healthtracker.R
import com.example.healthtracker.presentation.components.LoadingController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException
import java.time.LocalDate
import androidx.annotation.StringRes



class OnboardingViewModel(
    private val calculateBMRUseCase: CalculateBMRUseCase,
    private val calculateTDEEUseCase: CalculateTDEEUseCase,
    private val calculateBMIUseCase: CalculateBMIUseCase,
    private val saveUserProfileUseCase: SaveUserProfileUseCase,
    private val validateUserProfileUseCase: ValidateUserProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<OnboardingUiEvent>()
    val uiEvent: SharedFlow<OnboardingUiEvent> = _uiEvent.asSharedFlow()

    fun updateName(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun updateDateOfBirth(dob: LocalDate) {
        val age = java.time.Period.between(dob, LocalDate.now()).years
        _uiState.update { it.copy(dateOfBirth = dob, age = age) }
    }

    fun updateGender(gender: Gender) {
        _uiState.update { it.copy(gender = gender) }
    }

    fun updateWeight(weight: String) {
        _uiState.update { it.copy(weight = weight) }
    }

    fun updateHeight(height: String) {
        _uiState.update { it.copy(height = height) }
    }

    fun updateActivityLevel(level: ActivityLevel) {
        _uiState.update { it.copy(activityLevel = level) }
    }

    fun updateGoal(goal: Goal) {
        _uiState.update { it.copy(goal = goal) }
    }

    fun nextStep() {
        when (_uiState.value.currentStep) {
            1 -> {
                val nameResult = validateUserProfileUseCase.validateName(_uiState.value.name)
                if (!nameResult.successful) {
                    nameResult.errorMessageId?.let { emitError(it) }
                    return
                }
                val dobResult = validateUserProfileUseCase.validateDateOfBirth(_uiState.value.dateOfBirth)
                if (!dobResult.successful) {
                    dobResult.errorMessageId?.let { emitError(it) }
                    return
                }
                _uiState.update { it.copy(currentStep = 2) }
            }
            2 -> {
                val weight = _uiState.value.weight.replace(",", ".").toFloatOrNull() ?: 0f
                val height = _uiState.value.height.replace(",", ".").toFloatOrNull() ?: 0f
                val weightResult = validateUserProfileUseCase.validateWeight(weight)
                if (!weightResult.successful) {
                    weightResult.errorMessageId?.let { emitError(it) }
                    return
                }
                val heightResult = validateUserProfileUseCase.validateHeight(height)
                if (!heightResult.successful) {
                    heightResult.errorMessageId?.let { emitError(it) }
                    return
                }
                _uiState.update { it.copy(currentStep = 3) }
            }
            3 -> {
                _uiState.update { it.copy(currentStep = 4) }
            }
            4 -> {
                calculateResults()
                _uiState.update { it.copy(currentStep = 5) }
            }
            5 -> {
                saveUserAndFinish()
            }
            }
    }

    fun previousStep() {
        if (_uiState.value.currentStep > 1) {
            _uiState.update { it.copy(currentStep = it.currentStep - 1) }
        }
    }

    private fun calculateResults() {
        val state = _uiState.value
        val weight = state.weight.replace(",", ".").toFloatOrNull() ?: 0f
        val height = state.height.replace(",", ".").toFloatOrNull() ?: 0f
        val dob = requireNotNull(state.dateOfBirth) { "DateOfBirth must not be null when calculating results" }

        val bmr = calculateBMRUseCase(weight, height, state.gender, dob)
        val tdeeResult = calculateTDEEUseCase(bmr, state.activityLevel, state.goal)
        val bmi = calculateBMIUseCase(weight, height)

        _uiState.update {
            it.copy(
                calculatedTdee = tdeeResult.maintenance,
                targetCalories = tdeeResult.target,
                calculatedBmi = bmi
            )
        }
    }

    private fun saveUserAndFinish() {
        val state = _uiState.value
        val weight = state.weight.replace(",", ".").toFloatOrNull() ?: 0f
        val height = state.height.replace(",", ".").toFloatOrNull() ?: 0f
        val user = User(
            name = state.name,
            dateOfBirth = requireNotNull(state.dateOfBirth) { "DateOfBirth must not be null when saving user" },
            gender = state.gender,
            weightKg = weight,
            heightCm = height,
            activityLevel = state.activityLevel,
            goal = state.goal,
            tdee = state.calculatedTdee,
            targetCalories = state.targetCalories,
            bmi = state.calculatedBmi
        )
        viewModelScope.launch {
            try {
                LoadingController.withLoading {
                    delay(1000)
                    saveUserProfileUseCase(user)
                }
                _uiEvent.emit(OnboardingUiEvent.NavigateToDashboard)
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _uiEvent.emit(OnboardingUiEvent.ShowError(R.string.error_occurred))
            }
        }
    }



    private fun emitError(@StringRes messageId: Int) {
        viewModelScope.launch {
            _uiEvent.emit(OnboardingUiEvent.ShowError(messageId))
        }
    }
}

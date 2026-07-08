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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import androidx.annotation.StringRes
import com.example.healthtracker.R



class OnboardingViewModel(
    private val calculateBMRUseCase: CalculateBMRUseCase,
    private val calculateTDEEUseCase: CalculateTDEEUseCase,
    private val calculateBMIUseCase: CalculateBMIUseCase,
    private val saveUserProfileUseCase: SaveUserProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<OnboardingUiEvent>()
    val uiEvent: SharedFlow<OnboardingUiEvent> = _uiEvent.asSharedFlow()

    fun updateName(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun updateAge(age: Int) {
        _uiState.update { it.copy(age = age) }
    }

    fun updateGender(gender: Gender) {
        _uiState.update { it.copy(gender = gender) }
    }

    fun updateWeight(weight: Float) {
        _uiState.update { it.copy(weight = weight) }
    }

    fun updateHeight(height: Float) {
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
                if (_uiState.value.name.isBlank()) { emitError(R.string.error_empty_name); return }
                _uiState.update { it.copy(currentStep = 2) }
            }
            2 -> {
                val weight = _uiState.value.weight
                val height = _uiState.value.height
                if (weight <= 0f) { emitError(R.string.error_invalid_weight); return }
                if (height <= 0f) { emitError(R.string.error_invalid_height); return }
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
        val weight = state.weight
        val height = state.height
        val dob = LocalDate.now().minusYears(state.age.toLong())

        val bmr = calculateBMRUseCase(weight, height, state.gender, dob)
        val tdee = calculateTDEEUseCase(bmr, state.activityLevel, state.goal)
        val bmi = calculateBMIUseCase(weight, height)

        _uiState.update {
            it.copy(
                calculatedTdee = tdee,
                calculatedBmi = bmi
            )
        }
    }

    private fun saveUserAndFinish() {
        val state = _uiState.value
        val user = User(
            name = state.name,
            dateOfBirth = LocalDate.now().minusYears(state.age.toLong()),
            gender = state.gender,
            weightKg = state.weight,
            heightCm = state.height,
            activityLevel = state.activityLevel,
            goal = state.goal,
            targetCalories = state.calculatedTdee,
            bmi = state.calculatedBmi
        )
        viewModelScope.launch {
            saveUserProfileUseCase(user)
            _uiEvent.emit(OnboardingUiEvent.NavigateToDashboard)
        }
    }



    private fun emitError(@StringRes messageId: Int) {
        viewModelScope.launch {
            _uiEvent.emit(OnboardingUiEvent.ShowError(messageId))
        }
    }
}

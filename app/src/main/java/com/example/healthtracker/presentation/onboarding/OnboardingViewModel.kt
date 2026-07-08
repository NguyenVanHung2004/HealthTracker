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

    fun updateDob(dob: String) {
        _uiState.update { it.copy(dobString = dob) }
    }

    fun updateGender(gender: Gender) {
        _uiState.update { it.copy(gender = gender) }
    }

    fun updateWeight(weight: String) {
        _uiState.update { it.copy(weightString = weight) }
    }

    fun updateHeight(height: String) {
        _uiState.update { it.copy(heightString = height) }
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
                if (_uiState.value.name.isBlank()) {
                    emitError("Vui lòng nhập tên")
                    return
                }
                if (!isValidDob(_uiState.value.dobString)) {
                    emitError("Ngày sinh không hợp lệ (dd/MM/yyyy)")
                    return
                }
                _uiState.update { it.copy(currentStep = 2) }
            }
            2 -> {
                val weight = _uiState.value.weightString.toFloatOrNull()
                val height = _uiState.value.heightString.toFloatOrNull()
                if (weight == null || weight <= 0f) {
                    emitError("Cân nặng không hợp lệ")
                    return
                }
                if (height == null || height <= 0f) {
                    emitError("Chiều cao không hợp lệ")
                    return
                }
                _uiState.update { it.copy(currentStep = 3) }
            }
            3 -> {
                calculateResults()
                _uiState.update { it.copy(currentStep = 4) }
            }
            4 -> {
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
        val weight = state.weightString.toFloat()
        val height = state.heightString.toFloat()
        val dob = parseDob(state.dobString) ?: return

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
            dateOfBirth = parseDob(state.dobString) ?: LocalDate.now(),
            gender = state.gender,
            weightKg = state.weightString.toFloat(),
            heightCm = state.heightString.toFloat(),
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

    private fun isValidDob(dob: String): Boolean {
        return parseDob(dob) != null
    }

    private fun parseDob(dob: String): LocalDate? {
        if (dob.length != 10) return null
        return try {
            val parts = dob.split("/")
            if (parts.size == 3) {
                LocalDate.of(parts[2].toInt(), parts[1].toInt(), parts[0].toInt())
            } else null
        } catch (e: Exception) {
            null
        }
    }

    private fun emitError(message: String) {
        viewModelScope.launch {
            _uiEvent.emit(OnboardingUiEvent.ShowError(message))
        }
    }
}

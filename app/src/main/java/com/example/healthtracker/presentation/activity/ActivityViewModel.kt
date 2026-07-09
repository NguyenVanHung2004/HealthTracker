package com.example.healthtracker.presentation.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthtracker.R
import com.example.healthtracker.domain.model.ExerciseLog
import com.example.healthtracker.domain.model.ExerciseType
import com.example.healthtracker.domain.repository.UserRepository
import com.example.healthtracker.domain.usecase.AddExerciseUseCase
import com.example.healthtracker.domain.usecase.DeleteExerciseUseCase
import com.example.healthtracker.domain.usecase.GetExercisesByDateUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class ActivityUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val exercises: List<ExerciseLog> = emptyList(),
    val totalCaloriesBurned: Int = 0,
    val showAddDialog: Boolean = false,
    val selectedExerciseType: ExerciseType? = null,
    val durationInput: String = "",
    val isUserLoading: Boolean = true
)

class ActivityViewModel(
    private val userRepository: UserRepository,
    private val addExerciseUseCase: AddExerciseUseCase,
    private val getExercisesByDateUseCase: GetExercisesByDateUseCase,
    private val deleteExerciseUseCase: DeleteExerciseUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ActivityUiState())
    val uiState: StateFlow<ActivityUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<ActivityUiEvent>()
    val uiEvent: SharedFlow<ActivityUiEvent> = _uiEvent.asSharedFlow()

    init {
        checkUser()
        loadExercises()
    }

    private fun checkUser() {
        viewModelScope.launch {
            userRepository.getUser().collect { user ->
                _uiState.update { it.copy(isUserLoading = user == null) }
            }
        }
    }

    private fun loadExercises() {
        viewModelScope.launch {
            getExercisesByDateUseCase(_uiState.value.selectedDate).collect { logs ->
                _uiState.update { state ->
                    state.copy(
                        exercises = logs,
                        totalCaloriesBurned = logs.sumOf { it.caloriesBurned }
                    )
                }
            }
        }
    }

    fun setShowAddDialog(show: Boolean) {
        _uiState.update { it.copy(showAddDialog = show, selectedExerciseType = null, durationInput = "") }
    }

    fun selectExerciseType(type: ExerciseType) {
        _uiState.update { it.copy(selectedExerciseType = type) }
    }

    fun setDurationInput(duration: String) {
        if (duration.isEmpty() || duration.all { it.isDigit() }) {
            _uiState.update { it.copy(durationInput = duration) }
        }
    }

    fun addExercise() {
        val state = _uiState.value

        // Validate 
        if (state.selectedExerciseType == null) {
            emitEvent(ActivityUiEvent.ShowError(R.string.toast_select_activity))
            return
        }
        val duration = state.durationInput.toIntOrNull()
        if (duration == null || duration <= 0) {
            emitEvent(ActivityUiEvent.ShowError(R.string.toast_enter_duration))
            return
        }

        viewModelScope.launch {
            addExerciseUseCase(state.selectedExerciseType, duration, state.selectedDate)
            _uiEvent.emit(ActivityUiEvent.ShowSuccess(R.string.toast_activity_added))
            _uiEvent.emit(ActivityUiEvent.NavigateBack)
        }
    }

    fun deleteExercise(exerciseLog: ExerciseLog) {
        viewModelScope.launch {
            deleteExerciseUseCase(exerciseLog)
            _uiEvent.emit(ActivityUiEvent.ShowSuccess(R.string.toast_activity_deleted))
        }
    }

    private fun emitEvent(event: ActivityUiEvent) {
        viewModelScope.launch { _uiEvent.emit(event) }
    }
}

package com.example.healthtracker.presentation.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthtracker.R
import com.example.healthtracker.domain.model.ExerciseLog
import com.example.healthtracker.domain.model.ExerciseType
import com.example.healthtracker.domain.usecase.GetUserUseCase
import com.example.healthtracker.domain.usecase.AddExerciseUseCase
import com.example.healthtracker.domain.usecase.DeleteExerciseUseCase
import com.example.healthtracker.domain.usecase.GetExercisesByDateRangeUseCase
import com.example.healthtracker.presentation.widget.WidgetUpdater
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException
import java.time.LocalDate
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job
import com.example.healthtracker.presentation.components.LoadingController
import java.time.temporal.TemporalAdjusters

enum class DateFilterType {
    TODAY, WEEK, MONTH, CUSTOM
}

data class ActivityUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val filterType: DateFilterType = DateFilterType.TODAY,
    val customStartDate: LocalDate = LocalDate.now(),
    val customEndDate: LocalDate = LocalDate.now(),
    val exercises: List<ExerciseLog> = emptyList(),
    val totalCaloriesBurned: Int = 0,
    val showAddDialog: Boolean = false,
    val selectedExerciseType: ExerciseType? = null,
    val durationInput: String = "",
    val isUserLoading: Boolean = true
)

class ActivityViewModel(
    private val getUserUseCase: GetUserUseCase,
    private val addExerciseUseCase: AddExerciseUseCase,
    private val getExercisesByDateRangeUseCase: GetExercisesByDateRangeUseCase,
    private val deleteExerciseUseCase: DeleteExerciseUseCase,
    private val widgetUpdater: WidgetUpdater
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
            getUserUseCase().collect { user ->
                _uiState.update { it.copy(isUserLoading = user == null) }
            }
        }
    }

    private var loadJob: Job? = null

    fun setFilterType(filterType: DateFilterType) {
        _uiState.update { it.copy(filterType = filterType) }
        loadExercises()
    }

    fun setCustomRange(startDate: LocalDate, endDate: LocalDate) {
        _uiState.update {
            it.copy(
                filterType = DateFilterType.CUSTOM,
                customStartDate = startDate,
                customEndDate = endDate
            )
        }
        loadExercises()
    }

    private fun loadExercises() {
        loadJob?.cancel()
        val today = LocalDate.now()
        val (start, end) = when (_uiState.value.filterType) {
            DateFilterType.TODAY -> today to today
            DateFilterType.WEEK -> {
                val s = today.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY))
                val e = today.with(TemporalAdjusters.nextOrSame(java.time.DayOfWeek.SUNDAY))
                s to e
            }
            DateFilterType.MONTH -> {
                val s = today.with(TemporalAdjusters.firstDayOfMonth())
                val e = today.with(TemporalAdjusters.lastDayOfMonth())
                s to e
            }
            DateFilterType.CUSTOM -> {
                _uiState.value.customStartDate to _uiState.value.customEndDate
            }
        }

        loadJob = viewModelScope.launch {
            getExercisesByDateRangeUseCase(start, end).collect { logs ->
                _uiState.update { state ->
                    state.copy(
                        exercises = logs,
                        totalCaloriesBurned = logs.sumOf { it.caloriesBurned }
                    )
                }
            }
        }
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
            try {
                LoadingController.withLoading {
                    delay(600)
                    addExerciseUseCase(state.selectedExerciseType, duration, state.selectedDate)
                }
                widgetUpdater.updateWidget()
                _uiEvent.emit(ActivityUiEvent.ShowSuccess(R.string.toast_activity_added))
                _uiEvent.emit(ActivityUiEvent.NavigateBack)
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _uiEvent.emit(ActivityUiEvent.ShowError(R.string.error_occurred))
            }
        }
    }

    fun deleteExercise(exerciseLog: ExerciseLog) {
        viewModelScope.launch {
            try {
                LoadingController.withLoading {
                    delay(500)
                    deleteExerciseUseCase(exerciseLog)
                }
                widgetUpdater.updateWidget()
                _uiEvent.emit(ActivityUiEvent.ShowSuccess(R.string.toast_activity_deleted))
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _uiEvent.emit(ActivityUiEvent.ShowError(R.string.error_occurred))
            }
        }
    }

    private fun emitEvent(event: ActivityUiEvent) {
        viewModelScope.launch { _uiEvent.emit(event) }
    }
}

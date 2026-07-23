package com.example.healthtracker.presentation.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthtracker.R
import com.example.healthtracker.domain.model.ExerciseLog
import com.example.healthtracker.domain.model.ExerciseType
import com.example.healthtracker.domain.usecase.AddExerciseUseCase
import com.example.healthtracker.domain.usecase.DeleteExerciseUseCase
import com.example.healthtracker.domain.usecase.GetExercisesByDateRangeUseCase
import com.example.healthtracker.domain.usecase.GetUserUseCase
import com.example.healthtracker.presentation.components.LoadingController
import com.example.healthtracker.presentation.widget.WidgetUpdater
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import kotlin.coroutines.cancellation.CancellationException

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

    private val _filterState = MutableStateFlow(FilterState())
    private val _addFormState = MutableStateFlow(AddFormState())

    private val _uiEvent = MutableSharedFlow<ActivityUiEvent>()
    val uiEvent: SharedFlow<ActivityUiEvent> = _uiEvent.asSharedFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _exercisesFlow = _filterState.flatMapLatest { filter ->
        val today = LocalDate.now()
        val (start, end) = when (filter.filterType) {
            DateFilterType.TODAY -> today to today
            DateFilterType.WEEK -> {
                val s = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                val e = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
                s to e
            }
            DateFilterType.MONTH -> {
                val s = today.with(TemporalAdjusters.firstDayOfMonth())
                val e = today.with(TemporalAdjusters.lastDayOfMonth())
                s to e
            }
            DateFilterType.CUSTOM -> {
                filter.customStartDate to filter.customEndDate
            }
        }
        getExercisesByDateRangeUseCase(start, end)
    }

    val uiState: StateFlow<ActivityUiState> = combine(
        getUserUseCase(),
        _filterState,
        _exercisesFlow,
        _addFormState
    ) { user, filter, exercises, addForm ->
        ActivityUiState(
            selectedDate = filter.selectedDate,
            filterType = filter.filterType,
            customStartDate = filter.customStartDate,
            customEndDate = filter.customEndDate,
            exercises = exercises,
            totalCaloriesBurned = exercises.sumOf { it.caloriesBurned },
            selectedExerciseType = addForm.selectedExerciseType,
            durationInput = addForm.durationInput,
            isUserLoading = user == null
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ActivityUiState()
    )

    fun onEvent(event: ActivityEvent) {
        when (event) {
            is ActivityEvent.OnFilterTypeChanged -> setFilterType(event.filterType)
            is ActivityEvent.OnCustomRangeSelected -> setCustomRange(event.startDate, event.endDate)
            is ActivityEvent.OnExerciseTypeSelected -> selectExerciseType(event.type)
            is ActivityEvent.OnDurationInputChanged -> setDurationInput(event.duration)
            is ActivityEvent.OnDeleteExercise -> deleteExercise(event.exerciseLog)
            is ActivityEvent.OnAddExercise -> addExercise()
        }
    }

    fun setFilterType(filterType: DateFilterType) {
        _filterState.update { it.copy(filterType = filterType) }
    }

    fun setCustomRange(startDate: LocalDate, endDate: LocalDate) {
        _filterState.update {
            it.copy(
                filterType = DateFilterType.CUSTOM,
                customStartDate = startDate,
                customEndDate = endDate
            )
        }
    }

    fun selectExerciseType(type: ExerciseType) {
        _addFormState.update { it.copy(selectedExerciseType = type) }
    }

    fun setDurationInput(duration: String) {
        if (duration.isEmpty() || duration.all { it.isDigit() }) {
            _addFormState.update { it.copy(durationInput = duration) }
        }
    }

    fun addExercise() {
        val state = uiState.value

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

private data class FilterState(
    val selectedDate: LocalDate = LocalDate.now(),
    val filterType: DateFilterType = DateFilterType.TODAY,
    val customStartDate: LocalDate = LocalDate.now(),
    val customEndDate: LocalDate = LocalDate.now()
)

private data class AddFormState(
    val selectedExerciseType: ExerciseType? = null,
    val durationInput: String = ""
)

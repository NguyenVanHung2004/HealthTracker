package com.example.healthtracker.presentation.activity

import com.example.healthtracker.domain.model.ExerciseLog
import com.example.healthtracker.domain.model.ExerciseType
import java.time.LocalDate

sealed interface ActivityEvent {
    data class OnFilterTypeChanged(val filterType: DateFilterType) : ActivityEvent
    data class OnCustomRangeSelected(val startDate: LocalDate, val endDate: LocalDate) : ActivityEvent
    data class OnExerciseTypeSelected(val type: ExerciseType) : ActivityEvent
    data class OnDurationInputChanged(val duration: String) : ActivityEvent
    data class OnDeleteExercise(val exerciseLog: ExerciseLog) : ActivityEvent
    object OnAddExercise : ActivityEvent
}

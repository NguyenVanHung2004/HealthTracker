package com.example.healthtracker.domain.model

import androidx.annotation.StringRes
import com.example.healthtracker.R

enum class ExerciseType(val met: Double, @StringRes val nameRes: Int) {
    WALKING(3.5,       R.string.exercise_walking),
    RUNNING(9.8,       R.string.exercise_running),
    CYCLING(7.5,       R.string.exercise_cycling),
    SWIMMING(8.0,      R.string.exercise_swimming),
    YOGA(3.0,          R.string.exercise_yoga),
    GYM(6.0,           R.string.exercise_gym),
    STAIRS(8.0,        R.string.exercise_stairs),
    JUMP_ROPE(12.0,    R.string.exercise_jump_rope),
    BADMINTON(5.5,     R.string.exercise_badminton),
    FOOTBALL(7.0,      R.string.exercise_football),
    BASKETBALL(8.0,    R.string.exercise_basketball),
    TENNIS(7.3,        R.string.exercise_tennis),
    VOLLEYBALL(4.0,    R.string.exercise_volleyball),
    DANCING(4.5,       R.string.exercise_dancing),
    AEROBICS(7.3,      R.string.exercise_aerobics),
    HIKING(6.0,        R.string.exercise_hiking),
    PILATES(3.8,       R.string.exercise_pilates),
    BOXING(10.0,       R.string.exercise_boxing),
    SKATEBOARDING(5.0, R.string.exercise_skateboarding),
    MARTIAL_ARTS(10.0, R.string.exercise_martial_arts)
}

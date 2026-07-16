package com.example.healthtracker.presentation.activity

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.healthtracker.domain.model.ExerciseType
import com.example.healthtracker.ui.theme.*

data class ExerciseUiMeta(
    val icon: ImageVector,
    val gradientIndex: Int
)

val exerciseMeta = mapOf(
    ExerciseType.WALKING      to ExerciseUiMeta(Icons.AutoMirrored.Filled.DirectionsWalk,    0),
    ExerciseType.RUNNING      to ExerciseUiMeta(Icons.AutoMirrored.Filled.DirectionsRun,     2),
    ExerciseType.CYCLING      to ExerciseUiMeta(Icons.AutoMirrored.Filled.DirectionsBike,    1),
    ExerciseType.SWIMMING     to ExerciseUiMeta(Icons.Default.Pool,              3),
    ExerciseType.YOGA         to ExerciseUiMeta(Icons.Default.SelfImprovement,   4),
    ExerciseType.GYM          to ExerciseUiMeta(Icons.Default.FitnessCenter,     5),
    ExerciseType.STAIRS       to ExerciseUiMeta(Icons.Default.Stairs,            0),
    ExerciseType.JUMP_ROPE    to ExerciseUiMeta(Icons.Default.Loop,              2),
    ExerciseType.BADMINTON    to ExerciseUiMeta(Icons.Default.SportsTennis,      1),
    ExerciseType.FOOTBALL     to ExerciseUiMeta(Icons.Default.SportsSoccer,      3),
    ExerciseType.BASKETBALL   to ExerciseUiMeta(Icons.Default.SportsBasketball,  4),
    ExerciseType.TENNIS       to ExerciseUiMeta(Icons.Default.SportsTennis,      5),
    ExerciseType.VOLLEYBALL   to ExerciseUiMeta(Icons.Default.SportsVolleyball,  0),
    ExerciseType.DANCING      to ExerciseUiMeta(Icons.Default.MusicNote,         2),
    ExerciseType.AEROBICS     to ExerciseUiMeta(Icons.Default.Favorite,          4),
    ExerciseType.HIKING       to ExerciseUiMeta(Icons.Default.Terrain,           1),
    ExerciseType.PILATES      to ExerciseUiMeta(Icons.Default.SelfImprovement,   3),
    ExerciseType.BOXING       to ExerciseUiMeta(Icons.Default.SportsMartialArts, 5),
    ExerciseType.SKATEBOARDING to ExerciseUiMeta(Icons.Default.Skateboarding,   2),
    ExerciseType.MARTIAL_ARTS to ExerciseUiMeta(Icons.Default.SportsMartialArts, 4)
)

val exerciseGradients = listOf(
    Brush.linearGradient(listOf(GradientOrangeStart, GradientOrangeEnd)),
    Brush.linearGradient(listOf(GradientGreenStart, GradientGreenEnd)),
    Brush.linearGradient(listOf(GradientBlueStart, GradientBlueEnd)),
    Brush.linearGradient(listOf(GradientPurpleStart, GradientPurpleEnd)),
    Brush.linearGradient(listOf(GradientRoseStart, GradientRoseEnd)),
    Brush.linearGradient(listOf(GradientTealStart, GradientTealEnd))
)

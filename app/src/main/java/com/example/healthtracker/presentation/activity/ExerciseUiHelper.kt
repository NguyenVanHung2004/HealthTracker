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
import com.example.healthtracker.R
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
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

val ExerciseType.nameRes: Int
    get() = when (this) {
        ExerciseType.WALKING -> R.string.exercise_walking
        ExerciseType.RUNNING -> R.string.exercise_running
        ExerciseType.CYCLING -> R.string.exercise_cycling
        ExerciseType.SWIMMING -> R.string.exercise_swimming
        ExerciseType.YOGA -> R.string.exercise_yoga
        ExerciseType.GYM -> R.string.exercise_gym
        ExerciseType.STAIRS -> R.string.exercise_stairs
        ExerciseType.JUMP_ROPE -> R.string.exercise_jump_rope
        ExerciseType.BADMINTON -> R.string.exercise_badminton
        ExerciseType.FOOTBALL -> R.string.exercise_football
        ExerciseType.BASKETBALL -> R.string.exercise_basketball
        ExerciseType.TENNIS -> R.string.exercise_tennis
        ExerciseType.VOLLEYBALL -> R.string.exercise_volleyball
        ExerciseType.DANCING -> R.string.exercise_dancing
        ExerciseType.AEROBICS -> R.string.exercise_aerobics
        ExerciseType.HIKING -> R.string.exercise_hiking
        ExerciseType.PILATES -> R.string.exercise_pilates
        ExerciseType.BOXING -> R.string.exercise_boxing
        ExerciseType.SKATEBOARDING -> R.string.exercise_skateboarding
        ExerciseType.MARTIAL_ARTS -> R.string.exercise_martial_arts
    }

@Composable
fun getThemeExerciseGradients(): List<Brush> {
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val tertiary = MaterialTheme.colorScheme.tertiary
    
    return listOf(
        Brush.linearGradient(listOf(primary, primary.copy(alpha = 0.7f))),
        Brush.linearGradient(listOf(secondary, secondary.copy(alpha = 0.7f))),
        Brush.linearGradient(listOf(tertiary, tertiary.copy(alpha = 0.7f))),
        Brush.linearGradient(listOf(primary.copy(alpha = 0.8f), secondary.copy(alpha = 0.8f))),
        Brush.linearGradient(listOf(secondary.copy(alpha = 0.8f), tertiary.copy(alpha = 0.8f))),
        Brush.linearGradient(listOf(tertiary.copy(alpha = 0.8f), primary.copy(alpha = 0.8f)))
    )
}

package com.example.healthtracker.presentation.components

import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

data class SnackbarMessage(
    val id: String = UUID.randomUUID().toString(),
    val message: String,
    val isError: Boolean = false
)

object SnackbarController {
    val activeMessages = mutableStateListOf<SnackbarMessage>()

    private val scope = CoroutineScope(Dispatchers.Main.immediate)

    fun showSnackbar(message: String, isError: Boolean = false) {
        val msg = SnackbarMessage(message = message, isError = isError)
        activeMessages.add(msg)
        
        scope.launch {
            delay(3000) // Auto-dismiss after 3 seconds
            activeMessages.remove(msg)
        }
    }

    fun dismiss(msg: SnackbarMessage) {
        activeMessages.remove(msg)
    }
}

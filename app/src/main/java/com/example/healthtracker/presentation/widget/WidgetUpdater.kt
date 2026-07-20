package com.example.healthtracker.presentation.widget

import android.content.Context
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WidgetUpdater(private val context: Context) {
    fun updateWidget() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                CalorieWidget().updateAll(context)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

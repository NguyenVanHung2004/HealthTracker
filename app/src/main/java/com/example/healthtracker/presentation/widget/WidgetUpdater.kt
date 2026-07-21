package com.example.healthtracker.presentation.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.example.healthtracker.domain.usecase.GetDashboardDataUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate

class WidgetUpdater(
    private val context: Context,
    private val getDashboardDataUseCase: GetDashboardDataUseCase
) {
    fun updateWidget() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                delay(500)
                val data = getDashboardDataUseCase().first()
                
                val manager = GlanceAppWidgetManager(context)
                val widget = CalorieWidget()
                val glanceIds = manager.getGlanceIds(widget.javaClass)
                
                for (id in glanceIds) {
                    updateAppWidgetState(context, id) { prefs ->
                        prefs[CalorieWidget.TARGET_KEY] = data.targetCalories
                        prefs[CalorieWidget.CONSUMED_KEY] = data.consumedCaloriesToday
                        prefs[CalorieWidget.BURNED_KEY] = data.burnedCaloriesToday
                        prefs[CalorieWidget.TDEE_KEY] = data.tdee
                        prefs[CalorieWidget.DATE_KEY] = LocalDate.now().toString()
                    }
                    widget.update(context, id)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

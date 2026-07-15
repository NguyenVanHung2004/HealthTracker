package com.example.healthtracker.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.healthtracker.data.alarm.AlarmSchedulerImpl
import com.example.healthtracker.data.local.preferences.UserPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val userPreferences = UserPreferences(context)
            val alarmScheduler = AlarmSchedulerImpl(context)
            
            CoroutineScope(Dispatchers.IO).launch {
                val isEnabled = userPreferences.notificationsEnabled.firstOrNull() ?: false
                if (isEnabled) {
                    alarmScheduler.scheduleDailyReminders()
                }
            }
        }
    }
}

package com.example.healthtracker.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.healthtracker.MainActivity
import com.example.healthtracker.R
import com.example.healthtracker.domain.usecase.GetExercisesByDateUseCase
import com.example.healthtracker.domain.usecase.GetMealsByDateUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.LocalDate

import com.example.healthtracker.domain.alarm.ReminderType

class NotificationReceiver : BroadcastReceiver(), KoinComponent {

    private val getMealsByDateUseCase: GetMealsByDateUseCase by inject()
    private val getExercisesByDateUseCase: GetExercisesByDateUseCase by inject()
    private val alarmScheduler: com.example.healthtracker.domain.alarm.AlarmScheduler by inject()

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                val channelId = "daily_reminders_channel"

                val channel = NotificationChannel(
                    channelId,
                    context.getString(R.string.notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = context.getString(R.string.notification_channel_description)
                }
                notificationManager.createNotificationChannel(channel)

                val activityIntent = Intent(context, MainActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    activityIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val typeStr = intent.getStringExtra("NOTIFICATION_TYPE")
                val reminderType = try {
                    ReminderType.valueOf(typeStr ?: "")
                } catch (_: Exception) {
                    null
                }
                
                val title = context.getString(R.string.notification_title)
                
                val message = when (reminderType) {
                    ReminderType.MORNING -> context.getString(R.string.notification_morning)
                    ReminderType.NOON -> context.getString(R.string.notification_noon)
                    ReminderType.EVENING -> {
                        val today = LocalDate.now()
                        val meals = getMealsByDateUseCase(today).firstOrNull() ?: emptyList()
                        val exercises = getExercisesByDateUseCase(today).firstOrNull() ?: emptyList()
                        
                        val consumed = meals.sumOf { it.totalCalories }
                        val burned = exercises.sumOf { it.caloriesBurned }
                        
                        if (consumed > 0 || burned > 0) {
                            context.getString(R.string.notification_evening, consumed, burned)
                        } else {
                            context.getString(R.string.notification_evening_empty)
                        }
                    }
                    else -> context.getString(R.string.notification_default)
                }

                val notification = NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(message))
                    .build()

                val notificationId = intent.getIntExtra("NOTIFICATION_ID", reminderType?.id ?: 0)
                notificationManager.notify(notificationId, notification)
                
                // Reschedule for the next day to ensure exact timing
                alarmScheduler.scheduleDailyReminders()
            } finally {
                pendingResult.finish()
            }
        }
    }
}

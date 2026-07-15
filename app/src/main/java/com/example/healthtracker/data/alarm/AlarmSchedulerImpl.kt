package com.example.healthtracker.data.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.healthtracker.receiver.NotificationReceiver
import com.example.healthtracker.domain.alarm.ReminderType
import com.example.healthtracker.domain.alarm.AlarmScheduler
import java.util.Calendar

class AlarmSchedulerImpl(
    private val context: Context
) : AlarmScheduler {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun scheduleDailyReminders() {
        for (reminder in ReminderType.entries) {
            val intent = Intent(context, NotificationReceiver::class.java).apply {
                putExtra("NOTIFICATION_ID", reminder.id)
                putExtra("NOTIFICATION_TYPE", reminder.name)
            }
            
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                reminder.id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, reminder.defaultHour)
                set(Calendar.MINUTE, reminder.defaultMinute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)

                if (before(Calendar.getInstance())) {
                    add(Calendar.DATE, 1)
                }
            }

            try {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } catch (e: SecurityException) {
                // Ignore if exact alarm permission is missing
            }
        }
    }

    override fun cancelReminders() {
        for (reminder in ReminderType.entries) {
            val intent = Intent(context, NotificationReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                reminder.id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
        }
    }

    override fun testNotification() {
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("NOTIFICATION_TYPE", ReminderType.EVENING.name)
            putExtra("NOTIFICATION_ID", 999)
        }
        context.sendBroadcast(intent)
    }
}

package com.example.healthtracker.domain.alarm

interface AlarmScheduler {
    fun scheduleDailyReminders()
    fun cancelReminders()
    fun testNotification()
}

enum class ReminderType(val id: Int, val defaultHour: Int, val defaultMinute: Int) {
    MORNING(1, 7, 0),
    NOON(2, 12, 0),
    EVENING(3, 19, 0),
}

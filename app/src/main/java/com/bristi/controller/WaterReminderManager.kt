package com.bristi.controller

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

object WaterReminderManager {

    private const val ALARM_REQUEST_CODE = 1002

    fun scheduleAlarm(context: Context, intervalMinutes: Int = 30) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        val intent = Intent(context, WaterAlarmReceiver::class.java)
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val triggerAtMillis = System.currentTimeMillis() + intervalMinutes * 60 * 1000L

        try {
            // Using setAlarmClock ensures it fires accurately even in Doze mode
            // without requiring SCHEDULE_EXACT_ALARM permissions in Android 14
            val info = AlarmManager.AlarmClockInfo(triggerAtMillis, pendingIntent)
            alarmManager.setAlarmClock(info, pendingIntent)
            
            Log.d("WaterReminderManager", "Scheduled water alarm in $intervalMinutes minutes")
        } catch (e: Exception) {
            Log.e("WaterReminderManager", "Failed to schedule alarm using setAlarmClock, falling back...", e)
            try {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            } catch (ex: Exception) {
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
            }
        }
    }

    fun scheduleNextAlarm(context: Context) {
        val prefs = context.getSharedPreferences("JasicaSettings", Context.MODE_PRIVATE)
        val interval = prefs.getInt("WATER_REMINDER_INTERVAL", 30)
        scheduleAlarm(context, interval)
    }

    fun stopAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, WaterAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        Log.d("WaterReminderManager", "Cancelled water alarm")
    }
}

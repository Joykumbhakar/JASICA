package com.bristi.controller

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class WaterAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("WaterAlarmReceiver", "Water alarm triggered")
        
        val fullScreenIntent = Intent(context, WaterAlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val fullScreenPendingIntent = PendingIntent.getActivity(
            context,
            0,
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notificationHelper = NotificationHelper(context)
        notificationHelper.showWaterAlarmNotification(fullScreenPendingIntent)
        
        try {
            context.startActivity(fullScreenIntent)
        } catch (e: Exception) {
            Log.e("WaterAlarmReceiver", "Failed to start activity directly", e)
        }
        
        // Schedule the next alarm for 30 minutes later
        WaterReminderManager.scheduleNextAlarm(context)
    }
}

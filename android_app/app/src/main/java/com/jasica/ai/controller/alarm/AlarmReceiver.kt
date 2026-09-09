package com.jasica.ai.controller.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("ALARM_TITLE") ?: "Alarm!"

        // Acquire wake lock to ensure the CPU doesn't sleep before Activity starts
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "JasicaAI:AlarmWakeLock"
        )
        wakeLock.acquire(10 * 60 * 1000L) // 10 minutes timeout

        val activityIntent = Intent(context, AlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("ALARM_TITLE", title)
        }

        context.startActivity(activityIntent)
        wakeLock.release()
    }
}

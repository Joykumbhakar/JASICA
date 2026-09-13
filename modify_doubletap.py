path = r"E:\Controller\app\src\main\java\com\bristi\controller\DoubleTapService.kt"

new_code = """package com.bristi.controller

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.os.PowerManager
import android.os.Handler
import android.os.Looper
import android.util.Log

class DoubleTapService : Service(), SensorEventListener {

    companion object {
        private const val TAG = "DoubleTapService"
        private const val CHANNEL_ID = "double_tap_channel"
        private const val NOTIFICATION_ID = 1004

        private const val THRESHOLD_G = 15.0f
        private const val TAP_WINDOW_MS = 400L
    }

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var wakeLock: PowerManager.WakeLock? = null

    private var tapCount = 0
    private var lastTapTime = 0L
    private var wasBelowThreshold = true
    private val handler = Handler(Looper.getMainLooper())

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startForegroundNotification()

        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Jasica::DoubleTapWakeLock")
        wakeLock?.acquire()

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
            Log.d(TAG, "Accelerometer registered")
        } else {
            Log.w(TAG, "No accelerometer available")
        }
    }

    private fun startForegroundNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Double Tap Detection",
                NotificationManager.IMPORTANCE_LOW
            ).apply { description = "Listens for double-tap on device back" }
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }

        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("Tap Control Active")
                .setContentText("Double tap to turn ON, Triple tap to turn OFF")
                .setSmallIcon(R.drawable.ic_ai_waves)
                .build()
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(this)
                .setContentTitle("Tap Control Active")
                .setContentText("Double tap to turn ON, Triple tap to turn OFF")
                .setSmallIcon(R.drawable.ic_ai_waves)
                .build()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]
        val magnitude = Math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()

        if (magnitude < THRESHOLD_G) {
            wasBelowThreshold = true
            return
        }

        if (!wasBelowThreshold) return
        wasBelowThreshold = false

        val now = System.currentTimeMillis()

        if (now - lastTapTime > TAP_WINDOW_MS) {
            tapCount = 1
            lastTapTime = now
            
            handler.removeCallbacksAndMessages(null)
            handler.postDelayed({ finalizeTaps() }, TAP_WINDOW_MS + 150)
        } else {
            tapCount++
            lastTapTime = now
            
            handler.removeCallbacksAndMessages(null)
            handler.postDelayed({ finalizeTaps() }, TAP_WINDOW_MS + 150)
        }
    }

    private fun finalizeTaps() {
        val count = tapCount
        tapCount = 0
        
        if (count == 2) {
            onGestureDetected(isTurnOn = true)
        } else if (count == 3) {
            onGestureDetected(isTurnOn = false)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun onGestureDetected(isTurnOn: Boolean) {
        Log.d(TAG, "Gesture detected! isTurnOn=$isTurnOn")

        vibrate(if (isTurnOn) 80 else 150)

        val prefs = getSharedPreferences("JasicaSettings", Context.MODE_PRIVATE)
        val deviceIndex = prefs.getInt("DOUBLE_TAP_DEVICE_INDEX", 1)
        val devId = "dev$deviceIndex"

        val defaultPinOn = when(deviceIndex) { 1->"A"; 2->"B"; 3->"C"; 4->"D"; 5->"E"; 6->"F"; else->"A" }
        val defaultPinOff = when(deviceIndex) { 1->"a"; 2->"b"; 3->"c"; 4->"d"; 5->"e"; 6->"f"; else->"a" }

        val pinOn = prefs.getString("DEV_${devId}_PIN_ON", defaultPinOn) ?: defaultPinOn
        val pinOff = prefs.getString("DEV_${devId}_PIN_OFF", defaultPinOff) ?: defaultPinOff

        val command = if (isTurnOn) pinOn else pinOff
        JasicaBluetoothManager.sendRawCommand(command)

        JasicaBluetoothManager.deviceStates[devId] = isTurnOn
        prefs.edit().putBoolean("DEV_$devId", isTurnOn).apply()
    }

    private fun vibrate(duration: Long) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val mgr = getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager
                mgr.defaultVibrator.vibrate(
                    VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(
                        VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE)
                    )
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(duration)
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Vibration failed: ${e.message}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
        wakeLock?.let {
            if (it.isHeld) {
                it.release()
            }
        }
        handler.removeCallbacksAndMessages(null)
        Log.d(TAG, "DoubleTapService destroyed")
    }
}
"""

with open(path, "w", encoding="utf-8") as f:
    f.write(new_code)

print("DoubleTapService updated")

package com.bristi.controller

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
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log

class TiltService : Service(), SensorEventListener {

    companion object {
        private const val TAG = "TiltService"
        private const val CHANNEL_ID = "tilt_detect_channel"
        private const val NOTIFICATION_ID = 1006
        private const val TILT_THRESHOLD = 6.5f
        private const val NEUTRAL_THRESHOLD = 7.0f
        private const val COOLDOWN_MS = 1000L
    }

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var wakeLock: PowerManager.WakeLock? = null

    private var lastTriggerTime = 0L
    private var isNeutral = true

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startForegroundNotification()

        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Jasica::TiltWakeLock")
        wakeLock?.acquire()

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
            Log.d(TAG, "Accelerometer registered for tilt detection")
        }
    }

    private fun startForegroundNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Tilt Detection",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }

        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("Tilt Control Active")
                .setContentText("Tilt your phone to toggle specific devices")
                .setSmallIcon(R.drawable.ic_ai_waves)
                .build()
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(this)
                .setContentTitle("Tilt Control Active")
                .setContentText("Tilt your phone to toggle specific devices")
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

    private var holdingDirection = ""
    private var directionHoldStartTime = 0L

    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val now = System.currentTimeMillis()

        // Neutral state: Phone is mostly upright (Y > 7.0)
        if (y > NEUTRAL_THRESHOLD) {
            isNeutral = true
            holdingDirection = ""
            return
        }

        if (!isNeutral) return
        if (now - lastTriggerTime < COOLDOWN_MS) return

        var currentDirection = ""
        var devicePrefKey = ""

        if (x > TILT_THRESHOLD) {
            currentDirection = "LEFT"
            devicePrefKey = "TILT_LEFT_DEVICE"
        } else if (x < -TILT_THRESHOLD) {
            currentDirection = "RIGHT"
            devicePrefKey = "TILT_RIGHT_DEVICE"
        } else if (z > TILT_THRESHOLD) {
            currentDirection = "BACK"
            devicePrefKey = "TILT_BACK_DEVICE"
        } else if (z < -TILT_THRESHOLD) {
            currentDirection = "FRONT"
            devicePrefKey = "TILT_FRONT_DEVICE"
        }

        if (currentDirection.isNotEmpty()) {
            if (currentDirection != holdingDirection) {
                // Started a new tilt
                holdingDirection = currentDirection
                directionHoldStartTime = now
            } else if (now - directionHoldStartTime > 300) {
                // Held the tilt for 300ms, confirm it!
                isNeutral = false
                lastTriggerTime = now
                holdingDirection = ""
                
                Log.d(TAG, "Tilt detected: $currentDirection")
                
                val prefs = getSharedPreferences("JasicaSettings", Context.MODE_PRIVATE)
                val deviceIndex = prefs.getInt(devicePrefKey, -1)
                
                if (deviceIndex != -1) {
                    toggleDevice(deviceIndex, prefs)
                }
            }
        } else {
            // Not tilting enough in any primary direction
            holdingDirection = ""
        }
    }

    private fun toggleDevice(deviceIndex: Int, prefs: android.content.SharedPreferences) {
        vibrate()
        val devId = "dev$deviceIndex"

        val isOn = JasicaBluetoothManager.deviceStates[devId] ?: prefs.getBoolean("DEV_$devId", false)

        val defaultPinOn = when(deviceIndex) { 1->"A"; 2->"B"; 3->"C"; 4->"D"; 5->"E"; 6->"F"; else->"A" }
        val defaultPinOff = when(deviceIndex) { 1->"a"; 2->"b"; 3->"c"; 4->"d"; 5->"e"; 6->"f"; else->"a" }

        val pinOn = prefs.getString("DEV_${devId}_PIN_ON", defaultPinOn) ?: defaultPinOn
        val pinOff = prefs.getString("DEV_${devId}_PIN_OFF", defaultPinOff) ?: defaultPinOff

        val command = if (isOn) pinOff else pinOn
        JasicaBluetoothManager.sendRawCommand(command)

        JasicaBluetoothManager.deviceStates[devId] = !isOn
        prefs.edit().putBoolean("DEV_$devId", !isOn).apply()
    }

    private fun vibrate() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val mgr = getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager
                mgr.defaultVibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(100)
                }
            }
        } catch (e: Exception) {}
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
        wakeLock?.let {
            if (it.isHeld) it.release()
        }
        Log.d(TAG, "TiltService destroyed")
    }
}

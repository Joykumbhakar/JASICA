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
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.os.PowerManager
import android.util.Log

class ShakeService : Service(), SensorEventListener {

    companion object {
        private const val TAG = "ShakeService"
        private const val CHANNEL_ID = "shake_detect_channel"
        private const val NOTIFICATION_ID = 1005
        private const val SHAKE_COOLDOWN_MS = 1500L
    }

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var wakeLock: PowerManager.WakeLock? = null

    private var lastShakeTime = 0L

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startForegroundNotification()

        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Jasica::ShakeWakeLock")
        wakeLock?.acquire()

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
            Log.d(TAG, "Accelerometer registered for shake detection")
        }
    }

    private fun startForegroundNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Shake Detection",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }

        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("Shake Control Active")
                .setContentText("Shake your phone to toggle the selected device")
                .setSmallIcon(R.drawable.ic_ai_waves)
                .build()
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(this)
                .setContentTitle("Shake Control Active")
                .setContentText("Shake your phone to toggle the selected device")
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

        val gX = x / SensorManager.GRAVITY_EARTH
        val gY = y / SensorManager.GRAVITY_EARTH
        val gZ = z / SensorManager.GRAVITY_EARTH

        // Calculate g-force (1.0 = resting)
        val gForce = Math.sqrt((gX * gX + gY * gY + gZ * gZ).toDouble()).toFloat()

        val prefs = getSharedPreferences("JasicaSettings", Context.MODE_PRIVATE)
        // Sensitivity 0 to 100. 100 = very sensitive (trigger at ~1.3g). 0 = hard shake (trigger at ~3.0g).
        val sensitivity = prefs.getInt("SHAKE_SENSITIVITY", 50)
        
        // Map 0 -> 3.0f, 100 -> 1.3f
        val threshold = 3.0f - (sensitivity / 100f) * 1.7f

        if (gForce > threshold) {
            val now = System.currentTimeMillis()
            if (now - lastShakeTime > SHAKE_COOLDOWN_MS) {
                lastShakeTime = now
                onShakeDetected(prefs)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun onShakeDetected(prefs: android.content.SharedPreferences) {
        Log.d(TAG, "Shake detected!")
        vibrate()

        val deviceIndex = prefs.getInt("SHAKE_DEVICE_INDEX", 1)
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
                mgr.defaultVibrator.vibrate(
                    VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(
                        VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE)
                    )
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(200)
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
            if (it.isHeld) it.release()
        }
        Log.d(TAG, "ShakeService destroyed")
    }
}

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
import android.util.Log

/**
 * Foreground service that listens to the accelerometer for double-tap gestures
 * on the back of the device.  When a double-tap is detected it broadcasts
 * SEND_QUICK_COMMAND with the user's chosen device index so MainActivity's
 * existing receiver can toggle the device on/off.
 *
 * Detection algorithm:
 * – Continuously measures acceleration magnitude.
 * – A "tap" is a spike above THRESHOLD_G (≈ 15 m/s²) after being below it.
 * – Two taps within TAP_WINDOW_MS (400 ms) count as a double-tap.
 * – After a double-tap fires, a cooldown of COOLDOWN_MS (800 ms) prevents
 *   rapid repeat triggers.
 */
class DoubleTapService : Service(), SensorEventListener {

    companion object {
        private const val TAG = "DoubleTapService"
        private const val CHANNEL_ID = "double_tap_channel"
        private const val NOTIFICATION_ID = 1004

        // Acceleration spike must exceed this (m/s²) to register as a tap.
        // Gravity ≈ 9.81, so 15 filters out normal movement.
        private const val THRESHOLD_G = 15.0f

        // Max gap between two taps to count as a double-tap.
        private const val TAP_WINDOW_MS = 400L

        // Cooldown after a double-tap fires, to prevent rapid re-triggers.
        private const val COOLDOWN_MS = 800L
    }

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    private var lastTapTime = 0L
    private var lastFireTime = 0L
    private var wasBelowThreshold = true

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startForegroundNotification()

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
            Log.d(TAG, "Accelerometer registered for double-tap detection")
        } else {
            Log.w(TAG, "No accelerometer available – double-tap will not work")
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
                .setContentTitle("Double Tap Active")
                .setContentText("Tap twice on the back of your phone to toggle your device")
                .setSmallIcon(R.drawable.ic_ai_waves)
                .build()
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(this)
                .setContentTitle("Double Tap Active")
                .setContentText("Tap twice on the back of your phone to toggle your device")
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

    // ── Sensor callbacks ─────────────────────────────────────────────────

    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]
        val magnitude = Math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()

        val now = System.currentTimeMillis()

        if (magnitude < THRESHOLD_G) {
            wasBelowThreshold = true
            return
        }

        // Only register a tap on the rising edge (was below → now above)
        if (!wasBelowThreshold) return
        wasBelowThreshold = false

        // Cooldown: ignore taps too soon after the last double-tap
        if (now - lastFireTime < COOLDOWN_MS) return

        if (now - lastTapTime <= TAP_WINDOW_MS) {
            // ── Double-tap detected! ──
            lastFireTime = now
            lastTapTime = 0L
            onDoubleTapDetected()
        } else {
            lastTapTime = now
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    // ── Double-tap handler ───────────────────────────────────────────────

    private fun onDoubleTapDetected() {
        Log.d(TAG, "Double-tap detected!")

        // Haptic feedback
        vibrate()

        val prefs = getSharedPreferences("JasicaSettings", Context.MODE_PRIVATE)
        val deviceIndex = prefs.getInt("DOUBLE_TAP_DEVICE_INDEX", 1)

        // Use same broadcast mechanism as Quick Access widget
        val devId = "dev$deviceIndex"
        val isOn = JasicaBluetoothManager.deviceStates[devId] ?: prefs.getBoolean("DEV_$devId", false)

        val defaultPinOn = when(deviceIndex) { 1->"A"; 2->"B"; 3->"C"; 4->"D"; 5->"E"; 6->"F"; else->"A" }
        val defaultPinOff = when(deviceIndex) { 1->"a"; 2->"b"; 3->"c"; 4->"d"; 5->"e"; 6->"f"; else->"a" }

        val pinOn = prefs.getString("DEV_${devId}_PIN_ON", defaultPinOn) ?: defaultPinOn
        val pinOff = prefs.getString("DEV_${devId}_PIN_OFF", defaultPinOff) ?: defaultPinOff

        val command = if (isOn) pinOff else pinOn
        JasicaBluetoothManager.sendRawCommand(command)

        // Update state optimistically
        JasicaBluetoothManager.deviceStates[devId] = !isOn
        prefs.edit().putBoolean("DEV_$devId", !isOn).apply()

        Log.d(TAG, "Sent toggle command for device index $deviceIndex")
    }

    private fun vibrate() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val mgr = getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager
                mgr.defaultVibrator.vibrate(
                    VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(
                        VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE)
                    )
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(80)
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Vibration failed: ${e.message}")
        }
    }

    // ── Lifecycle ────────────────────────────────────────────────────────

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
        Log.d(TAG, "DoubleTapService destroyed, sensor unregistered")
    }
}

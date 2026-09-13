import os

path = r"E:\Controller\app\src\main\java\com\bristi\controller\TiltService.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

target = """    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        // Neutral state: Phone is mostly upright (Y > 7.0)
        if (y > NEUTRAL_THRESHOLD) {
            isNeutral = true
            return
        }

        if (!isNeutral) return

        val now = System.currentTimeMillis()
        if (now - lastTriggerTime < COOLDOWN_MS) return

        var detectedDirection = ""
        var devicePrefKey = ""

        if (x > TILT_THRESHOLD) {
            detectedDirection = "LEFT"
            devicePrefKey = "TILT_LEFT_DEVICE"
        } else if (x < -TILT_THRESHOLD) {
            detectedDirection = "RIGHT"
            devicePrefKey = "TILT_RIGHT_DEVICE"
        } else if (z > TILT_THRESHOLD) {
            detectedDirection = "BACK"
            devicePrefKey = "TILT_BACK_DEVICE"
        } else if (z < -TILT_THRESHOLD) {
            detectedDirection = "FRONT"
            devicePrefKey = "TILT_FRONT_DEVICE"
        }

        if (detectedDirection.isNotEmpty()) {
            isNeutral = false
            lastTriggerTime = now
            Log.d(TAG, "Tilt detected: $detectedDirection")
            
            val prefs = getSharedPreferences("JasicaSettings", Context.MODE_PRIVATE)
            val deviceIndex = prefs.getInt(devicePrefKey, -1)
            
            if (deviceIndex != -1) {
                toggleDevice(deviceIndex, prefs)
            }
        }
    }"""

replacement = """    private var holdingDirection = ""
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
    }"""

if target in text:
    text = text.replace(target, replacement)
    with open(path, "w", encoding="utf-8") as f:
        f.write(text)
    print("Tilt logic improved")
else:
    print("Target not found in TiltService")

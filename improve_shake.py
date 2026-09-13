import os

path = r"E:\Controller\app\src\main\java\com\bristi\controller\ShakeService.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

target = """    override fun onSensorChanged(event: SensorEvent) {
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
    }"""

replacement = """    private var shakeCount = 0
    private var lastShakePeakTime = 0L

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
        // Sensitivity 0 to 100. 100 = very sensitive (trigger at ~1.3g). 0 = hard shake (trigger at ~2.5g).
        val sensitivity = prefs.getInt("SHAKE_SENSITIVITY", 50)
        
        // Map 0 -> 2.5f, 100 -> 1.3f
        val threshold = 2.5f - (sensitivity / 100f) * 1.2f

        val now = System.currentTimeMillis()

        // Reset shake count if too much time has passed since last peak (500ms)
        if (now - lastShakePeakTime > 500) {
            shakeCount = 0
        }

        if (gForce > threshold) {
            // Require peaks to be at least 100ms apart to count as separate shake movements
            if (now - lastShakePeakTime > 100) {
                shakeCount++
                lastShakePeakTime = now

                // Require 3 distinct shake movements to trigger
                if (shakeCount >= 3) {
                    if (now - lastShakeTime > SHAKE_COOLDOWN_MS) {
                        lastShakeTime = now
                        shakeCount = 0
                        onShakeDetected(prefs)
                    }
                }
            }
        }
    }"""

if target in text:
    text = text.replace(target, replacement)
    with open(path, "w", encoding="utf-8") as f:
        f.write(text)
    print("Shake logic improved")
else:
    print("Target not found in ShakeService")

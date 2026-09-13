path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

target = """        // Start Shake service if enabled
        if (sharedPrefs.getBoolean("SHAKE_FEATURE_ENABLED", false)) {
            val shakeIntent = Intent(this, ShakeService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(shakeIntent)
            } else {
                startService(shakeIntent)
            }
        }"""

replacement = target + """

        // Start Tilt service if enabled
        if (sharedPrefs.getBoolean("TILT_FEATURE_ENABLED", false)) {
            val tiltIntent = Intent(this, TiltService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(tiltIntent)
            } else {
                startService(tiltIntent)
            }
        }"""

if target in text:
    text = text.replace(target, replacement)
    with open(path, "w", encoding="utf-8") as f:
        f.write(text)
    print("Added TiltService autostart")
else:
    print("Could not find ShakeService autostart")

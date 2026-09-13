path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

target = """        // Start Double Tap Back service if enabled
        if (sharedPrefs.getBoolean("DOUBLE_TAP_BACK", false)) {
            val dtIntent = Intent(this, DoubleTapService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(dtIntent)
            } else {
                startService(dtIntent)
            }
        }"""

replacement = target + """
        
        // Start Shake service if enabled
        if (sharedPrefs.getBoolean("SHAKE_FEATURE_ENABLED", false)) {
            val shakeIntent = Intent(this, ShakeService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(shakeIntent)
            } else {
                startService(shakeIntent)
            }
        }"""

if target in text:
    text = text.replace(target, replacement)
    with open(path, "w", encoding="utf-8") as f:
        f.write(text)
    print("MainActivity patched (auto-start) successfully.")
else:
    print("Target string not found for auto-start.")

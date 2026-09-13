import re
path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

target = """        sharedPrefs = getSharedPreferences("JasicaSettings", Context.MODE_PRIVATE)"""

replacement = """        sharedPrefs = getSharedPreferences("JasicaSettings", Context.MODE_PRIVATE)

        // Start Quick Access Service if enabled
        if (sharedPrefs.getBoolean("QUICK_ACCESS", false) && android.provider.Settings.canDrawOverlays(this)) {
            val serviceIntent = Intent(this, FloatingControlService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent)
            } else {
                startService(serviceIntent)
            }
        }
        
        // Register Quick Access Receiver
        val filter = IntentFilter().apply {
            addAction("com.bristi.controller.SEND_QUICK_COMMAND")
            addAction("com.bristi.controller.START_MIC")
        }
        registerReceiver(quickAccessReceiver, filter, Context.RECEIVER_NOT_EXPORTED)"""

if target in text:
    text = text.replace(target, replacement)
    with open(path, "w", encoding="utf-8") as f:
        f.write(text)
    print("Updated MainActivity onCreate")
else:
    print("Could not find target")

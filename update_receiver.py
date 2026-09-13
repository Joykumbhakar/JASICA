import re
path = r"E:\Controller\app\src\main\java\com\bristi\controller\WaterAlarmReceiver.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

target = """    override fun onReceive(context: Context, intent: Intent) {
        Log.d("WaterAlarmReceiver", "Water alarm triggered")
        
        val fullScreenIntent = Intent(context, WaterAlarmActivity::class.java).apply {"""

replacement = """    override fun onReceive(context: Context, intent: Intent) {
        Log.d("WaterAlarmReceiver", "Water alarm triggered")
        
        val prefs = context.getSharedPreferences("JasicaSettings", Context.MODE_PRIVATE)
        val isEnabled = prefs.getBoolean("WATER_REMINDER", false)
        if (!isEnabled) {
            Log.d("WaterAlarmReceiver", "Water reminder is disabled. Aborting alarm.")
            return
        }
        
        val fullScreenIntent = Intent(context, WaterAlarmActivity::class.java).apply {"""

if target in text:
    text = text.replace(target, replacement)
    with open(path, "w", encoding="utf-8") as f:
        f.write(text)
    print("Updated receiver")
else:
    print("Could not find target in receiver")

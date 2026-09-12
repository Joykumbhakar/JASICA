import re

FILE = r'E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt'
with open(FILE, 'r', encoding='utf-8') as f:
    content = f.read()

content = content.replace('private val activeTimerEndTimes', 'internal val activeTimerEndTimes')

old_elapsed = """                    val elapsed: String? = if (isChecked && startTime != null) {
                        val secs = ((tick - startTime) / 1000L).coerceAtLeast(0L)
                        "${secs / 60}:${(secs % 60).toString().padStart(2, '0')}"
                    } else null"""

new_elapsed = """                    val context = androidx.compose.ui.platform.LocalContext.current
                    val mainActivity = context as? MainActivity
                    val endTime = mainActivity?.activeTimerEndTimes?.get(device.id)
                    val elapsed: String? = if (endTime != null && endTime > tick) {
                        val remainingSecs = ((endTime - tick) / 1000L).coerceAtLeast(0L)
                        "Timer \u00b7 ${remainingSecs / 60}:${(remainingSecs % 60).toString().padStart(2, '0')}"
                    } else if (isChecked && startTime != null) {
                        val secs = ((tick - startTime) / 1000L).coerceAtLeast(0L)
                        "${secs / 60}:${(secs % 60).toString().padStart(2, '0')}"
                    } else null"""

if old_elapsed in content:
    content = content.replace(old_elapsed, new_elapsed)
    print("Replaced elapsed logic successfully.")
else:
    print("Could not find elapsed logic block.")

with open(FILE, 'w', encoding='utf-8', newline='') as f:
    f.write(content)

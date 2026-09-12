import re

with open(r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt", "r", encoding="utf-8") as f:
    text = f.read()

# For the generic Switches
text = re.sub(
    r'Switch\(\s*checked = (.*?),\s*onCheckedChange = (\{.*?\}),\s*colors = SwitchDefaults\.colors\(checkedThumbColor = Color\.White, checkedTrackColor = Color\(0xFF34C759\)\)\s*\)',
    r'AppleSwitch(\n                                      checked = \1,\n                                      onCheckedChange = \2\n                                  )',
    text,
    flags=re.DOTALL
)

# For the water reminder switch
water_switch_old = """Switch(
                                    checked = waterReminderInput,
                                    onCheckedChange = { isChecked ->
                                        if (!isChecked) {
                                            showPasswordDialog = true
                                        } else {
                                            waterReminderInput = true
                                            sharedPrefs.edit().putBoolean("WATER_REMINDER", true).apply()
                                            WaterReminderManager.scheduleAlarm(context, waterInterval)
                                            android.widget.Toast.makeText(context, "Water Reminder Active ($waterInterval min)", android.widget.Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF34C759))
                                )"""

water_switch_new = """AppleSwitch(
                                    checked = waterReminderInput,
                                    onCheckedChange = { isChecked ->
                                        if (!isChecked) {
                                            showPasswordDialog = true
                                        } else {
                                            waterReminderInput = true
                                            sharedPrefs.edit().putBoolean("WATER_REMINDER", true).apply()
                                            WaterReminderManager.scheduleAlarm(context, waterInterval)
                                            android.widget.Toast.makeText(context, "Water Reminder Active ($waterInterval min)", android.widget.Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                )"""

text = text.replace(water_switch_old, water_switch_new)

# For the slider
slider_old = """androidx.compose.material3.Slider(
                                    value = waterInterval.toFloat(),
                                    onValueChange = { 
                                        waterInterval = it.toInt()
                                        sharedPrefs.edit().putInt("WATER_REMINDER_INTERVAL", waterInterval).apply()
                                        if (waterReminderInput) {
                                            WaterReminderManager.scheduleAlarm(context, waterInterval)
                                        }
                                    },
                                    valueRange = 10f..120f,
                                    steps = 10,
                                    colors = androidx.compose.material3.SliderDefaults.colors(
                                        thumbColor = Color(0xFF007AFF),
                                        activeTrackColor = Color(0xFF007AFF)
                                    )
                                )"""

slider_new = """Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    LucideClock(tint = textSecondary, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(12.dp))
                                    androidx.compose.material3.Slider(
                                        value = waterInterval.toFloat(),
                                        onValueChange = { 
                                            waterInterval = it.toInt()
                                            sharedPrefs.edit().putInt("WATER_REMINDER_INTERVAL", waterInterval).apply()
                                            if (waterReminderInput) {
                                                WaterReminderManager.scheduleAlarm(context, waterInterval)
                                            }
                                        },
                                        valueRange = 10f..120f,
                                        steps = 10,
                                        modifier = Modifier.weight(1f),
                                        colors = androidx.compose.material3.SliderDefaults.colors(
                                            thumbColor = Color.White,
                                            activeTrackColor = Color(0xFF007AFF),
                                            inactiveTrackColor = Color(0xFFE5E5EA),
                                            activeTickColor = Color.Transparent,
                                            inactiveTickColor = Color.Transparent
                                        )
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Text("${waterInterval}m", color = textPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, fontFamily = InterFontFamily, modifier = Modifier.width(36.dp), textAlign = androidx.compose.ui.text.style.TextAlign.End)
                                }"""

text = text.replace(slider_old, slider_new)

with open(r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt", "w", encoding="utf-8") as f:
    f.write(text)

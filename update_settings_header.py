import re

file_path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

old_header = """            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp, start = 8.dp, end = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Settings",
                    color = textPrimary,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = InterFontFamily
                )
                TextButton(onClick = { 
                    // Save and show restart prompt
                    sharedPrefs.edit()
                        .putBoolean("DARK_MODE", darkModeInput)
                        .putBoolean("HISTORY_LOGGING", historyLoggingInput)
                        .putBoolean("WATER_REMINDER", waterReminderInput)
                        .putInt("WATER_REMINDER_INTERVAL", waterInterval)
                        .putBoolean("ONLINE_MODE_ENABLED", onlineModeInput)
                        .putBoolean("USE_ADMIN_PANEL_KEY", adminKeyInput)
                        .putString("GEMINI_API_KEY", geminiKeyInput.trim())
                        .apply()
                    onSave(apiKeyInput, selectedModel, wakeWordInput)
                    showRestartDialog = true
                }) {
                    Text("Done", color = Color(0xFF007AFF), fontSize = 17.sp, fontWeight = FontWeight.SemiBold, fontFamily = InterFontFamily)
                }
            }"""

new_header = """            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp, start = 8.dp, end = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    "Settings",
                    color = textPrimary,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = InterFontFamily,
                    letterSpacing = (-1).sp
                )
                Text(
                    "Done",
                    color = Color(0xFF007AFF),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = InterFontFamily,
                    modifier = Modifier.clickable(
                        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                        indication = null
                    ) { 
                        sharedPrefs.edit()
                            .putBoolean("DARK_MODE", darkModeInput)
                            .putBoolean("HISTORY_LOGGING", historyLoggingInput)
                            .putBoolean("WATER_REMINDER", waterReminderInput)
                            .putInt("WATER_REMINDER_INTERVAL", waterInterval)
                            .putBoolean("ONLINE_MODE_ENABLED", onlineModeInput)
                            .putBoolean("USE_ADMIN_PANEL_KEY", adminKeyInput)
                            .putString("GEMINI_API_KEY", geminiKeyInput.trim())
                            .apply()
                        onSave(apiKeyInput, selectedModel, wakeWordInput)
                        showRestartDialog = true
                    }.padding(horizontal = 8.dp, vertical = 6.dp)
                )
            }"""

content = content.replace(old_header, new_header)

with open(file_path, "w", encoding="utf-8") as f:
    f.write(content)

print("Updated Settings header")

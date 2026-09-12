import re

with open(r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt", "r", encoding="utf-8") as f:
    content = f.read()

content = content.replace("\r\n", "\n")

def replace_block(content, start_str, end_str, new_block, name):
    start_idx = content.find(start_str)
    if start_idx == -1:
        print(f"Error: {name} start_str not found!")
        return content
    
    end_idx = content.find(end_str, start_idx)
    if end_idx == -1:
        print(f"Error: {name} end_str not found!")
        return content
        
    return content[:start_idx] + new_block + content[end_idx:]

apple_components = """
@Composable
fun AppleSettingsGroup(
    title: String? = null,
    footer: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
        if (title != null) {
            Text(
                title.uppercase(java.util.Locale.getDefault()),
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF1C1C1E))
        ) {
            content()
        }
        if (footer != null) {
            Text(
                footer,
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 13.sp,
                modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 16.dp)
            )
        }
    }
}

@Composable
fun AppleSettingsRow(
    title: String,
    subtitle: String? = null,
    icon: String? = null,
    iconBgColor: Color = Color.Transparent,
    showDivider: Boolean = true,
    onClick: (() -> Unit)? = null,
    control: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(start = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Text(icon, fontSize = 16.sp)
            }
            Spacer(Modifier.width(16.dp))
        }
        
        Column(modifier = Modifier.weight(1f).padding(vertical = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth().padding(end = 16.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    if (subtitle != null) {
                        Spacer(Modifier.height(2.dp))
                        Text(subtitle, color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp)
                    }
                }
                if (control != null) {
                    Spacer(Modifier.width(12.dp))
                    control()
                }
            }
            if (showDivider) {
                androidx.compose.material.Divider(modifier = Modifier.padding(top = 12.dp), color = Color(0xFF38383A), thickness = 0.5.dp)
            }
        }
    }
}

"""

new_settings_screen = """
@Composable
fun SettingsScreen(
    currentApiKey: String,
    currentModel: String,
    isWakeWordMode: Boolean,
    isAdvancedAiMode: Boolean,
    isOnlineModeEnabled: Boolean,
    useAdminPanelKey: Boolean,
    sharedPrefs: android.content.SharedPreferences,
    onDismiss: () -> Unit,
    onSave: (String, String, Boolean) -> Unit
) {
    var apiKeyInput by remember { mutableStateOf(currentApiKey) }
    var selectedModel by remember { mutableStateOf(currentModel) }
    var wakeWordInput by remember { mutableStateOf(isWakeWordMode) }
    var historyLoggingInput by remember { mutableStateOf(sharedPrefs.getBoolean("HISTORY_LOGGING", true)) }
    var waterReminderInput by remember { mutableStateOf(sharedPrefs.getBoolean("WATER_REMINDER", false)) }
    var advancedAiInput by remember { mutableStateOf(isAdvancedAiMode) }
    var onlineModeInput by remember { mutableStateOf(isOnlineModeEnabled) }
    var adminKeyInput by remember { mutableStateOf(useAdminPanelKey) }
    var geminiKeyInput by remember { mutableStateOf(sharedPrefs.getString("GEMINI_API_KEY", "") ?: "") }
    var showApiKey by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    
    var showPasswordDialog by remember { mutableStateOf(false) }
    var passwordInput by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable(enabled = false) {}
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 54.dp, start = 16.dp, end = 16.dp, bottom = 0.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp, start = 8.dp, end = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Settings",
                    color = Color.White,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = InterFontFamily
                )
                TextButton(onClick = { onSave(apiKeyInput, selectedModel, wakeWordInput) }) {
                    Text("Done", color = Color(0xFF0A84FF), fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            
            // Unified Scrolling Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
            ) {
                // Core System Group
                AppleSettingsGroup(title = "Core System") {
                    AppleSettingsRow(
                        title = "Hands-Free Wake Word",
                        subtitle = "Say 'Hey Jasica' to activate",
                        icon = "🎙️",
                        iconBgColor = Color(0xFF007AFF),
                        showDivider = true,
                        control = {
                            Switch(
                                checked = wakeWordInput,
                                onCheckedChange = { wakeWordInput = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF34C759))
                            )
                        }
                    )
                    
                    AppleSettingsRow(
                        title = "Save History",
                        subtitle = "Log conversations locally",
                        icon = "🕒",
                        iconBgColor = Color(0xFF5856D6),
                        showDivider = true,
                        control = {
                            Switch(
                                checked = historyLoggingInput,
                                onCheckedChange = { 
                                    historyLoggingInput = it
                                    sharedPrefs.edit().putBoolean("HISTORY_LOGGING", it).apply()
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF34C759))
                            )
                        }
                    )
                    
                    AppleSettingsRow(
                        title = "Water Reminder",
                        subtitle = "30 minute intervals",
                        icon = "💧",
                        iconBgColor = Color(0xFF5AC8FA),
                        showDivider = false,
                        onClick = {
                            if (waterReminderInput) showPasswordDialog = true
                            else {
                                waterReminderInput = true
                                sharedPrefs.edit().putBoolean("WATER_REMINDER", true).apply()
                                WaterReminderManager.scheduleNextAlarm(context)
                            }
                        },
                        control = {
                            Switch(
                                checked = waterReminderInput,
                                onCheckedChange = null,
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF34C759))
                            )
                        }
                    )
                }

                // AI Mode Group
                AppleSettingsGroup(
                    title = "JASICA ONLINE", 
                    footer = if (!onlineModeInput) "Offline mode is 100% free with no internet needed." else "Jasica Online uses AI to handle complex tasks."
                ) {
                    AppleSettingsRow(
                        title = "Enable Jasica Online",
                        icon = "⚡",
                        iconBgColor = Color(0xFFFF9500),
                        showDivider = onlineModeInput,
                        control = {
                            Switch(
                                checked = onlineModeInput,
                                onCheckedChange = { checked ->
                                    onlineModeInput = checked
                                    sharedPrefs.edit().putBoolean("ONLINE_MODE_ENABLED", checked).putBoolean("ADVANCED_AI_MODE", checked).apply()
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF34C759))
                            )
                        }
                    )
                    
                    if (onlineModeInput) {
                        AppleSettingsRow(
                            title = "Admin Panel Key",
                            subtitle = "Auto-fetched from Portfolio",
                            icon = "🔗",
                            iconBgColor = Color(0xFF34C759),
                            showDivider = true,
                            onClick = {
                                adminKeyInput = true
                                sharedPrefs.edit().putBoolean("USE_ADMIN_PANEL_KEY", true).apply()
                            },
                            control = {
                                if (adminKeyInput) {
                                    Icon(Icons.Rounded.Check, contentDescription = null, tint = Color(0xFF0A84FF))
                                }
                            }
                        )
                        AppleSettingsRow(
                            title = "My Own Key",
                            subtitle = "Use your personal API key",
                            icon = "🔑",
                            iconBgColor = Color(0xFFFF2D55),
                            showDivider = !adminKeyInput,
                            onClick = {
                                adminKeyInput = false
                                sharedPrefs.edit().putBoolean("USE_ADMIN_PANEL_KEY", false).apply()
                            },
                            control = {
                                if (!adminKeyInput) {
                                    Icon(Icons.Rounded.Check, contentDescription = null, tint = Color(0xFF0A84FF))
                                }
                            }
                        )
                        
                        if (!adminKeyInput) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                androidx.compose.material3.OutlinedTextField(
                                    value = geminiKeyInput,
                                    onValueChange = {
                                        geminiKeyInput = it
                                        sharedPrefs.edit().putString("GEMINI_API_KEY", it.trim()).apply()
                                    },
                                    placeholder = { Text("AIza...", color = Color.White.copy(alpha = 0.2f)) },
                                    textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 16.sp),
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    singleLine = true,
                                    visualTransformation = if (showApiKey) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                                    trailingIcon = {
                                        IconButton(onClick = { showApiKey = !showApiKey }) {
                                            Text(if (showApiKey) "👁" else "🔒", fontSize = 16.sp)
                                        }
                                    },
                                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF0A84FF),
                                        unfocusedBorderColor = Color(0xFF38383A)
                                    )
                                )
                            }
                        }
                    }
                }
                
                // Hardware Config Group
                AppleSettingsGroup(title = "Hardware Config", footer = "Configure Bluetooth device voice commands.") {
                    DEFAULT_DEVICES.forEachIndexed { index, dev ->
                        var name by remember { mutableStateOf(sharedPrefs.getString("DEV_${dev.id}_NAME", dev.defaultName) ?: dev.defaultName) }
                        var onCmd by remember { mutableStateOf(sharedPrefs.getString("DEV_${dev.id}_ON_CMD", dev.defaultOnCmd) ?: dev.defaultOnCmd) }
                        var offCmd by remember { mutableStateOf(sharedPrefs.getString("DEV_${dev.id}_OFF_CMD", dev.defaultOffCmd) ?: dev.defaultOffCmd) }
                        
                        AppleSettingsRow(
                            title = "Device '${dev.id.uppercase(java.util.Locale.getDefault())}'",
                            icon = "⚙️",
                            iconBgColor = Color(0xFF8E8E93),
                            showDivider = index != DEFAULT_DEVICES.size - 1,
                            control = {
                                Text(name, color = Color.White.copy(0.5f), fontSize = 16.sp)
                            }
                        )
                    }
                }
                Spacer(Modifier.height(40.dp))
            }
        }

        if (showPasswordDialog) {
            AlertDialog(
                onDismissRequest = { 
                    showPasswordDialog = false
                    passwordError = false
                    passwordInput = ""
                },
                title = { Text("Enter Admin Password", color = Color.White, fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text("A password is required to turn off the water reminder.", color = Color.White.copy(0.7f), fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        androidx.compose.material3.OutlinedTextField(
                            value = passwordInput,
                            onValueChange = { passwordInput = it; passwordError = false },
                            label = { Text("Password", color = Color.White.copy(0.5f)) },
                            isError = passwordError,
                            textStyle = androidx.compose.ui.text.TextStyle(color = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFFF9800), unfocusedBorderColor = Color.White.copy(alpha = 0.15f))
                        )
                        if (passwordError) {
                            Text("Incorrect password", color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                        }
                    }
                },
                containerColor = Color(0xFF1E1E2E),
                shape = RoundedCornerShape(24.dp),
                confirmButton = {
                    Button(
                        onClick = {
                            val correctPassword = sharedPrefs.getString("WATER_REMINDER_PASSWORD", "0000") ?: "0000"
                            if (passwordInput == correctPassword) {
                                waterReminderInput = false
                                sharedPrefs.edit().putBoolean("WATER_REMINDER", false).apply()
                                WaterReminderManager.stopAlarm(context)
                                android.widget.Toast.makeText(context, "Water Reminder Disabled", android.widget.Toast.LENGTH_SHORT).show()
                                showPasswordDialog = false
                                passwordError = false
                                passwordInput = ""
                            } else {
                                passwordError = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Confirm", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { 
                        showPasswordDialog = false
                        passwordError = false
                        passwordInput = ""
                    }) {
                        Text("Cancel", color = Color.White.copy(0.5f))
                    }
                }
            )
        }
    }
}
"""

content = replace_block(content, "@Composable\nfun SettingsScreen(", "\n@SuppressLint(\"MissingPermission\")\n@Composable\nfun DeviceSelectionDialog(", apple_components + new_settings_screen, "SettingsScreen")

with open(r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt", "w", encoding="utf-8") as f:
    f.write(content)

print("Settings done")

import re
path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

target = """                // Updates Group
                Box {"""

replacement = """                // Quick Access Group
                Box {
                    AppleSettingsGroup(title = "System", isDark = darkModeInput) {
                        var quickAccessEnabled by remember { mutableStateOf(sharedPrefs.getBoolean("QUICK_ACCESS", false)) }
                        AppleSettingsRow(
                            title = "Quick Access Widget",
                            subtitle = "Floating icon for instant device control anywhere",
                            icon = null,
                            customIcon = R.drawable.ic_ai_waves,
                            showDivider = false,
                            isDark = darkModeInput,
                            rightContent = {
                                Switch(
                                    checked = quickAccessEnabled,
                                    onCheckedChange = { isChecked ->
                                        if (isChecked) {
                                            if (!android.provider.Settings.canDrawOverlays(context)) {
                                                val intent = android.content.Intent(
                                                    android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                                    android.net.Uri.parse("package:${context.packageName}")
                                                )
                                                context.startActivity(intent)
                                            } else {
                                                quickAccessEnabled = true
                                                sharedPrefs.edit().putBoolean("QUICK_ACCESS", true).apply()
                                                val serviceIntent = Intent(context, FloatingControlService::class.java)
                                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                                    context.startForegroundService(serviceIntent)
                                                } else {
                                                    context.startService(serviceIntent)
                                                }
                                            }
                                        } else {
                                            quickAccessEnabled = false
                                            sharedPrefs.edit().putBoolean("QUICK_ACCESS", false).apply()
                                            val serviceIntent = Intent(context, FloatingControlService::class.java)
                                            context.stopService(serviceIntent)
                                        }
                                    },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF007AFF))
                                )
                            }
                        )
                    }
                }
                
                // Updates Group
                Box {"""

if target in text:
    text = text.replace(target, replacement)
    with open(path, "w", encoding="utf-8") as f:
        f.write(text)
    print("Updated MainActivity settings")
else:
    print("Could not find target")

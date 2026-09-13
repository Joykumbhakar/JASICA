import re

path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

target = """                                Switch(
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
                                )"""

replacement = """                                AppleSwitch(
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
                                    }
                                )"""

if target in text:
    text = text.replace(target, replacement)
    with open(path, "w", encoding="utf-8") as f:
        f.write(text)
    print("Fixed to AppleSwitch")
else:
    print("Target not found")

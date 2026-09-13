path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

target = """                        }
                    }
                }
                
                // Updates Group"""

replacement = """                        }

                        // -- Shake to Toggle Device ---------------------
                        var shakeEnabled by remember { mutableStateOf(sharedPrefs.getBoolean("SHAKE_FEATURE_ENABLED", false)) }
                        var shakeDeviceIndex by remember { mutableStateOf(sharedPrefs.getInt("SHAKE_DEVICE_INDEX", 1)) }
                        var shakeSensitivity by remember { mutableStateOf(sharedPrefs.getInt("SHAKE_SENSITIVITY", 50)) }

                        AppleSettingsRow(
                            title = "Shake to Toggle",
                            subtitle = "Shake your phone to toggle a device",
                            icon = { Icon(androidx.compose.material.icons.Icons.Rounded.Vibration, contentDescription = null, modifier = Modifier.size(22.dp), tint = Color.White) },
                            iconBgColor = Color(0xFFFF2D55),
                            showDivider = shakeEnabled,
                            isDark = darkModeInput,
                            control = {
                                AppleSwitch(
                                    checked = shakeEnabled,
                                    onCheckedChange = { isChecked ->
                                        shakeEnabled = isChecked
                                        sharedPrefs.edit().putBoolean("SHAKE_FEATURE_ENABLED", isChecked).apply()
                                        val serviceIntent = Intent(context, ShakeService::class.java)
                                        if (isChecked) {
                                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                                context.startForegroundService(serviceIntent)
                                            } else {
                                                context.startService(serviceIntent)
                                            }
                                        } else {
                                            context.stopService(serviceIntent)
                                        }
                                    }
                                )
                            }
                        )

                        if (shakeEnabled) {
                            val deviceNames = remember(sharedPrefs) {
                                DEFAULT_DEVICES.mapIndexed { index, dev ->
                                    val name = sharedPrefs.getString("DEV_${dev.id}_NAME", dev.defaultName) ?: dev.defaultName
                                    Pair(index + 1, name)
                                }
                            }
                            var expandedShakePicker by remember { mutableStateOf(false) }
                            val selectedName = deviceNames.find { it.first == shakeDeviceIndex }?.second ?: "Device $shakeDeviceIndex"

                            AppleSettingsRow(
                                title = "Target Device",
                                subtitle = "Currently: $selectedName",
                                icon = { Icon(androidx.compose.material.icons.Icons.Rounded.Devices, contentDescription = null, modifier = Modifier.size(22.dp), tint = Color.White) },
                                iconBgColor = Color(0xFFFF9500),
                                showDivider = true,
                                isDark = darkModeInput,
                                control = {
                                    Box {
                                        Text(
                                            selectedName,
                                            color = Color(0xFF007AFF),
                                            fontSize = 14.sp,
                                            modifier = Modifier.clickable { expandedShakePicker = true }
                                        )
                                        DropdownMenu(
                                            expanded = expandedShakePicker,
                                            onDismissRequest = { expandedShakePicker = false }
                                        ) {
                                            deviceNames.forEach { (idx, name) ->
                                                DropdownMenuItem(
                                                    text = { Text(name) },
                                                    onClick = {
                                                        shakeDeviceIndex = idx
                                                        sharedPrefs.edit().putInt("SHAKE_DEVICE_INDEX", idx).apply()
                                                        expandedShakePicker = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            )
                            
                            AppleSettingsRow(
                                title = "Shake Sensitivity",
                                subtitle = "Current: ${shakeSensitivity}%",
                                icon = { Icon(androidx.compose.material.icons.Icons.Rounded.Speed, contentDescription = null, modifier = Modifier.size(22.dp), tint = Color.White) },
                                iconBgColor = Color(0xFF007AFF),
                                showDivider = false,
                                isDark = darkModeInput
                            )
                            
                            androidx.compose.foundation.layout.Box(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp)
                            ) {
                                androidx.compose.material3.Slider(
                                    value = shakeSensitivity.toFloat(),
                                    onValueChange = { shakeSensitivity = it.toInt() },
                                    onValueChangeFinished = {
                                        sharedPrefs.edit().putInt("SHAKE_SENSITIVITY", shakeSensitivity).apply()
                                    },
                                    valueRange = 0f..100f
                                )
                            }
                        }
                    }
                }
                
                // Updates Group"""

if target in text:
    text = text.replace(target, replacement)
    with open(path, "w", encoding="utf-8") as f:
        f.write(text)
    print("MainActivity patched successfully.")
else:
    print("Target string not found in MainActivity.")

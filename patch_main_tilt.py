path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

target = """                        // -- Shake to Toggle Device ---------------------
                        var shakeEnabled by remember { mutableStateOf(sharedPrefs.getBoolean("SHAKE_FEATURE_ENABLED", false)) }"""

replacement = """                        // -- Shake to Toggle Device ---------------------
                        var tiltEnabled by remember { mutableStateOf(sharedPrefs.getBoolean("TILT_FEATURE_ENABLED", false)) }
                        var shakeEnabled by remember { mutableStateOf(sharedPrefs.getBoolean("SHAKE_FEATURE_ENABLED", false)) }"""

text = text.replace(target, replacement)

target2 = """                                    onCheckedChange = { isChecked ->
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
                                    }"""

replacement2 = """                                    onCheckedChange = { isChecked ->
                                        shakeEnabled = isChecked
                                        sharedPrefs.edit().putBoolean("SHAKE_FEATURE_ENABLED", isChecked).apply()
                                        val serviceIntent = Intent(context, ShakeService::class.java)
                                        if (isChecked) {
                                            // Mutual Exclusion: Disable Tilt if Shake is turned ON
                                            if (tiltEnabled) {
                                                tiltEnabled = false
                                                sharedPrefs.edit().putBoolean("TILT_FEATURE_ENABLED", false).apply()
                                                context.stopService(Intent(context, TiltService::class.java))
                                            }
                                            
                                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                                context.startForegroundService(serviceIntent)
                                            } else {
                                                context.startService(serviceIntent)
                                            }
                                        } else {
                                            context.stopService(serviceIntent)
                                        }
                                    }"""

text = text.replace(target2, replacement2)

target3 = """                                    valueRange = 0f..100f
                                )
                            }
                        }
                    }
                }
                
                // Updates Group"""

replacement3 = """                                    valueRange = 0f..100f
                                )
                            }
                        }

                        // -- Tilt to Toggle Device ---------------------
                        var tiltLeftDevice by remember { mutableStateOf(sharedPrefs.getInt("TILT_LEFT_DEVICE", -1)) }
                        var tiltRightDevice by remember { mutableStateOf(sharedPrefs.getInt("TILT_RIGHT_DEVICE", -1)) }
                        var tiltFrontDevice by remember { mutableStateOf(sharedPrefs.getInt("TILT_FRONT_DEVICE", -1)) }
                        var tiltBackDevice by remember { mutableStateOf(sharedPrefs.getInt("TILT_BACK_DEVICE", -1)) }

                        AppleSettingsRow(
                            title = "Tilt to Toggle",
                            subtitle = "Tilt phone left/right/front/back to toggle specific devices",
                            icon = { Icon(androidx.compose.material.icons.Icons.Rounded.ScreenRotation, contentDescription = null, modifier = Modifier.size(22.dp), tint = Color.White) },
                            iconBgColor = Color(0xFF34C759),
                            showDivider = tiltEnabled,
                            isDark = darkModeInput,
                            control = {
                                AppleSwitch(
                                    checked = tiltEnabled,
                                    onCheckedChange = { isChecked ->
                                        tiltEnabled = isChecked
                                        sharedPrefs.edit().putBoolean("TILT_FEATURE_ENABLED", isChecked).apply()
                                        val serviceIntent = Intent(context, TiltService::class.java)
                                        if (isChecked) {
                                            // Mutual Exclusion: Disable Shake if Tilt is turned ON
                                            if (shakeEnabled) {
                                                shakeEnabled = false
                                                sharedPrefs.edit().putBoolean("SHAKE_FEATURE_ENABLED", false).apply()
                                                context.stopService(Intent(context, ShakeService::class.java))
                                            }
                                            
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

                        if (tiltEnabled) {
                            val deviceNames = remember(sharedPrefs) {
                                val list = mutableListOf(Pair(-1, "None"))
                                list.addAll(DEFAULT_DEVICES.mapIndexed { index, dev ->
                                    val name = sharedPrefs.getString("DEV_${dev.id}_NAME", dev.defaultName) ?: dev.defaultName
                                    Pair(index + 1, name)
                                })
                                list
                            }

                            @androidx.compose.runtime.Composable
                            fun TiltDeviceRow(direction: String, currentValue: Int, onValueChange: (Int) -> Unit) {
                                var expanded by remember { mutableStateOf(false) }
                                val selectedName = deviceNames.find { it.first == currentValue }?.second ?: "None"
                                
                                AppleSettingsRow(
                                    title = direction,
                                    subtitle = "Currently: $selectedName",
                                    iconBgColor = Color.Transparent,
                                    showDivider = true,
                                    isDark = darkModeInput,
                                    control = {
                                        Box {
                                            Text(
                                                selectedName,
                                                color = Color(0xFF007AFF),
                                                fontSize = 14.sp,
                                                modifier = Modifier.clickable { expanded = true }
                                            )
                                            DropdownMenu(
                                                expanded = expanded,
                                                onDismissRequest = { expanded = false }
                                            ) {
                                                deviceNames.forEach { (idx, name) ->
                                                    DropdownMenuItem(
                                                        text = { Text(name) },
                                                        onClick = {
                                                            onValueChange(idx)
                                                            expanded = false
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                )
                            }

                            TiltDeviceRow("Tilt Left") { 
                                tiltLeftDevice = it
                                sharedPrefs.edit().putInt("TILT_LEFT_DEVICE", it).apply()
                            }
                            TiltDeviceRow("Tilt Right") { 
                                tiltRightDevice = it
                                sharedPrefs.edit().putInt("TILT_RIGHT_DEVICE", it).apply()
                            }
                            TiltDeviceRow("Tilt Front") { 
                                tiltFrontDevice = it
                                sharedPrefs.edit().putInt("TILT_FRONT_DEVICE", it).apply()
                            }
                            TiltDeviceRow("Tilt Back") { 
                                tiltBackDevice = it
                                sharedPrefs.edit().putInt("TILT_BACK_DEVICE", it).apply()
                            }
                        }
                    }
                }
                
                // Updates Group"""

text = text.replace(target3, replacement3)

with open(path, "w", encoding="utf-8") as f:
    f.write(text)
print("Tilt UI added to MainActivity")

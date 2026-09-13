path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

target = """                            @androidx.compose.runtime.Composable
                            fun TiltDeviceRow(direction: String, currentValue: Int, onValueChange: (Int) -> Unit) {
                                var expanded by remember { mutableStateOf(false) }
                                val selectedName = deviceNames.find { it.first == currentValue }?.second ?: "None"
                                
                                AppleSettingsRow(
                                    title = direction,
                                    subtitle = "Currently: $selectedName",
                                    iconBgColor = Color.Transparent,
                                    showDivider = true,
                                    isDark = darkModeInput,"""

replacement = """                            @androidx.compose.runtime.Composable
                            fun TiltDeviceRow(direction: String, iconVector: androidx.compose.ui.graphics.vector.ImageVector, bgColor: Color, currentValue: Int, onValueChange: (Int) -> Unit) {
                                var expanded by remember { mutableStateOf(false) }
                                val selectedName = deviceNames.find { it.first == currentValue }?.second ?: "None"
                                
                                AppleSettingsRow(
                                    title = direction,
                                    subtitle = "Currently: $selectedName",
                                    icon = { Icon(iconVector, contentDescription = null, modifier = Modifier.size(22.dp), tint = Color.White) },
                                    iconBgColor = bgColor,
                                    showDivider = true,
                                    isDark = darkModeInput,"""

text = text.replace(target, replacement)

target2 = """                            TiltDeviceRow(direction = "Tilt Left", currentValue = tiltLeftDevice) { 
                                tiltLeftDevice = it
                                sharedPrefs.edit().putInt("TILT_LEFT_DEVICE", it).apply()
                            }
                            TiltDeviceRow(direction = "Tilt Right", currentValue = tiltRightDevice) { 
                                tiltRightDevice = it
                                sharedPrefs.edit().putInt("TILT_RIGHT_DEVICE", it).apply()
                            }
                            TiltDeviceRow(direction = "Tilt Front", currentValue = tiltFrontDevice) { 
                                tiltFrontDevice = it
                                sharedPrefs.edit().putInt("TILT_FRONT_DEVICE", it).apply()
                            }
                            TiltDeviceRow(direction = "Tilt Back", currentValue = tiltBackDevice) { 
                                tiltBackDevice = it
                                sharedPrefs.edit().putInt("TILT_BACK_DEVICE", it).apply()
                            }"""

replacement2 = """                            TiltDeviceRow(direction = "Tilt Left", iconVector = androidx.compose.material.icons.Icons.Rounded.ArrowBack, bgColor = Color(0xFF007AFF), currentValue = tiltLeftDevice) { 
                                tiltLeftDevice = it
                                sharedPrefs.edit().putInt("TILT_LEFT_DEVICE", it).apply()
                            }
                            TiltDeviceRow(direction = "Tilt Right", iconVector = androidx.compose.material.icons.Icons.Rounded.ArrowForward, bgColor = Color(0xFF34C759), currentValue = tiltRightDevice) { 
                                tiltRightDevice = it
                                sharedPrefs.edit().putInt("TILT_RIGHT_DEVICE", it).apply()
                            }
                            TiltDeviceRow(direction = "Tilt Front", iconVector = androidx.compose.material.icons.Icons.Rounded.ArrowUpward, bgColor = Color(0xFFFF9500), currentValue = tiltFrontDevice) { 
                                tiltFrontDevice = it
                                sharedPrefs.edit().putInt("TILT_FRONT_DEVICE", it).apply()
                            }
                            TiltDeviceRow(direction = "Tilt Back", iconVector = androidx.compose.material.icons.Icons.Rounded.ArrowDownward, bgColor = Color(0xFFFF2D55), currentValue = tiltBackDevice) { 
                                tiltBackDevice = it
                                sharedPrefs.edit().putInt("TILT_BACK_DEVICE", it).apply()
                            }"""

text = text.replace(target2, replacement2)

with open(path, "w", encoding="utf-8") as f:
    f.write(text)
print("Tilt UI icons added")

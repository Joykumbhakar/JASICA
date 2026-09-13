path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

target = """                            TiltDeviceRow("Tilt Left") { 
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
                            }"""

replacement = """                            TiltDeviceRow(direction = "Tilt Left", currentValue = tiltLeftDevice) { 
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

if target in text:
    text = text.replace(target, replacement)
    with open(path, "w", encoding="utf-8") as f:
        f.write(text)
    print("Fixed TiltDeviceRow calls")
else:
    print("Could not find TiltDeviceRow calls in MainActivity")

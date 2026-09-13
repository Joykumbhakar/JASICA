path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

target = """                                androidx.compose.material3.Slider(
                                    value = shakeSensitivity.toFloat(),
                                    onValueChange = { shakeSensitivity = it.toInt() },
                                    onValueChangeFinished = {
                                        sharedPrefs.edit().putInt("SHAKE_SENSITIVITY", shakeSensitivity).apply()
                                    },
                                    valueRange = 0f..100f
                                )"""

replacement = """                                androidx.compose.material3.Slider(
                                    value = shakeSensitivity.toFloat(),
                                    onValueChange = { shakeSensitivity = it.toInt() },
                                    onValueChangeFinished = {
                                        sharedPrefs.edit().putInt("SHAKE_SENSITIVITY", shakeSensitivity).apply()
                                    },
                                    valueRange = 0f..100f,
                                    colors = androidx.compose.material3.SliderDefaults.colors(
                                        thumbColor = Color.White,
                                        activeTrackColor = Color(0xFF007AFF),
                                        inactiveTrackColor = if (darkModeInput) Color(0xFF38383A) else Color(0xFFE5E5EA)
                                    )
                                )"""

if target in text:
    text = text.replace(target, replacement)
    with open(path, "w", encoding="utf-8") as f:
        f.write(text)
    print("Styled the slider")
else:
    print("Slider target not found")

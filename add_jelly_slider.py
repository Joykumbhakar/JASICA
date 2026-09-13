import os

path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

jelly_slider_code = """@androidx.compose.runtime.Composable
fun AppleJellySlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: (() -> Unit)? = null,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    activeColor: Color = Color(0xFF007AFF),
    inactiveColor: Color = Color(0xFFE5E5EA)
) {
    var width by remember { mutableStateOf(0f) }
    val progress = ((value - valueRange.start) / (valueRange.endInclusive - valueRange.start)).coerceIn(0f, 1f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(32.dp)
            .clip(androidx.compose.foundation.shape.CircleShape)
            .background(inactiveColor)
            .androidx.compose.ui.layout.onGloballyPositioned { width = it.size.width.toFloat() }
            .androidx.compose.ui.input.pointer.pointerInput(Unit) {
                androidx.compose.foundation.gestures.detectTapGestures(
                    onTap = { offset ->
                        if (width > 0) {
                            val newValue = valueRange.start + (offset.x / width) * (valueRange.endInclusive - valueRange.start)
                            onValueChange(newValue.coerceIn(valueRange))
                            onValueChangeFinished?.invoke()
                        }
                    }
                )
            }
            .androidx.compose.ui.input.pointer.pointerInput(Unit) {
                androidx.compose.foundation.gestures.detectHorizontalDragGestures(
                    onDragEnd = { onValueChangeFinished?.invoke() }
                ) { change, _ ->
                    if (width > 0) {
                        val newValue = valueRange.start + (change.position.x / width) * (valueRange.endInclusive - valueRange.start)
                        onValueChange(newValue.coerceIn(valueRange))
                    }
                }
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(fraction = progress.coerceIn(0.001f, 1f))
                .background(activeColor)
        )
    }
}

"""

if "fun AppleJellySlider(" not in text:
    # Insert it right before AppleSettingsRow
    text = text.replace("fun AppleSettingsRow(", jelly_slider_code + "fun AppleSettingsRow(")

target_slider = """                            androidx.compose.foundation.layout.Box(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp)
                            ) {
                                androidx.compose.material3.Slider(
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
                                )
                            }"""

replacement_slider = """                            androidx.compose.foundation.layout.Box(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp)
                            ) {
                                AppleJellySlider(
                                    value = shakeSensitivity.toFloat(),
                                    onValueChange = { shakeSensitivity = it.toInt() },
                                    onValueChangeFinished = {
                                        sharedPrefs.edit().putInt("SHAKE_SENSITIVITY", shakeSensitivity).apply()
                                    },
                                    valueRange = 0f..100f,
                                    inactiveColor = if (darkModeInput) Color(0xFF38383A) else Color(0xFFE5E5EA)
                                )
                            }"""

if target_slider in text:
    text = text.replace(target_slider, replacement_slider)
    with open(path, "w", encoding="utf-8") as f:
        f.write(text)
    print("AppleJellySlider added and implemented")
else:
    print("Target slider not found")

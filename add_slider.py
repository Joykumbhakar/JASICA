import re

with open(r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt", "r", encoding="utf-8") as f:
    text = f.read()

apple_slider_code = """
@Composable
fun AppleSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    modifier: Modifier = Modifier,
    steps: Int = 0
) {
    val isDark = isSystemInDarkTheme()
    val activeTrackColor = Color(0xFF007AFF) // Apple Blue
    val inactiveTrackColor = if (isDark) Color(0xFF333336) else Color(0xFFE5E5EA)

    BoxWithConstraints(
        modifier = modifier
            .height(28.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        val thumbRadius = 14.dp
        val trackHeight = 4.dp
        val widthPx = constraints.maxWidth.toFloat()
        val thumbRadiusPx = with(LocalDensity.current) { thumbRadius.toPx() }
        val trackWidthPx = widthPx - 2 * thumbRadiusPx

        val fraction = ((value - valueRange.start) / (valueRange.endInclusive - valueRange.start)).coerceIn(0f, 1f)
        val thumbOffsetX = (fraction * trackWidthPx)

        var isDragging by remember { mutableStateOf(false) }
        val thumbScale by animateFloatAsState(targetValue = if (isDragging) 1.15f else 1f, label = "thumbScale")
        
        fun updateValueFromOffset(offsetX: Float) {
            val newOffsetX = (offsetX - thumbRadiusPx).coerceIn(0f, trackWidthPx)
            val newFraction = newOffsetX / trackWidthPx
            val newValue = valueRange.start + newFraction * (valueRange.endInclusive - valueRange.start)
            
            val roundedValue = if (steps > 0) {
                val stepSize = (valueRange.endInclusive - valueRange.start) / (steps + 1)
                Math.round(newValue / stepSize) * stepSize
            } else newValue
            
            onValueChange(roundedValue.coerceIn(valueRange.start, valueRange.endInclusive))
        }

        // Full interaction area
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    androidx.compose.foundation.gestures.detectHorizontalDragGestures(
                        onDragStart = { isDragging = true },
                        onDragEnd = { isDragging = false },
                        onDragCancel = { isDragging = false }
                    ) { change, _ ->
                        change.consume()
                        updateValueFromOffset(change.position.x)
                    }
                }
                .pointerInput(Unit) {
                    androidx.compose.foundation.gestures.detectTapGestures(
                        onPress = { offset ->
                            isDragging = true
                            updateValueFromOffset(offset.x)
                            tryAwaitRelease()
                            isDragging = false
                        }
                    )
                }
        )

        // Track (Inactive)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = thumbRadius)
                .height(trackHeight)
                .clip(CircleShape)
                .background(inactiveTrackColor)
        ) {
            // Track (Active)
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(activeTrackColor)
            )
        }

        // Thumb
        Box(
            modifier = Modifier
                .offset { IntOffset(thumbOffsetX.roundToInt(), 0) }
                .size(28.dp)
                .scale(thumbScale)
                .shadow(
                    elevation = if (isDark) 2.dp else 4.dp, 
                    shape = CircleShape, 
                    spotColor = Color.Black.copy(alpha = 0.4f),
                    ambientColor = Color.Black.copy(alpha = 0.1f)
                )
                .border(
                    width = 0.5.dp,
                    color = Color.Black.copy(alpha = 0.04f),
                    shape = CircleShape
                )
                .background(Color.White, CircleShape)
        )
    }
}
"""

text = text.replace("@Composable\nfun AppleSwitch", apple_slider_code + "\n@Composable\nfun AppleSwitch")

old_slider_code = """                                    androidx.compose.material3.Slider(
                                        value = waterInterval.toFloat(),
                                        onValueChange = { 
                                            waterInterval = it.toInt()
                                            sharedPrefs.edit().putInt("WATER_REMINDER_INTERVAL", waterInterval).apply()
                                            if (waterReminderInput) {
                                                WaterReminderManager.scheduleAlarm(context, waterInterval)
                                            }
                                        },
                                        valueRange = 10f..120f,
                                        steps = 10,
                                        modifier = Modifier.weight(1f),
                                        colors = androidx.compose.material3.SliderDefaults.colors(
                                            thumbColor = Color.White,
                                            activeTrackColor = Color(0xFF007AFF),
                                            inactiveTrackColor = Color(0xFFE5E5EA),
                                            activeTickColor = Color.Transparent,
                                            inactiveTickColor = Color.Transparent
                                        )
                                    )"""

new_slider_code = """                                    AppleSlider(
                                        value = waterInterval.toFloat(),
                                        onValueChange = { 
                                            waterInterval = it.toInt()
                                            sharedPrefs.edit().putInt("WATER_REMINDER_INTERVAL", waterInterval).apply()
                                            if (waterReminderInput) {
                                                WaterReminderManager.scheduleAlarm(context, waterInterval)
                                            }
                                        },
                                        valueRange = 10f..120f,
                                        steps = 10,
                                        modifier = Modifier.weight(1f)
                                    )"""

text = text.replace(old_slider_code, new_slider_code)

if "import androidx.compose.ui.input.pointer.pointerInput" not in text:
    text = text.replace("import androidx.compose.ui.Modifier", "import androidx.compose.ui.Modifier\nimport androidx.compose.ui.input.pointer.pointerInput")

with open(r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt", "w", encoding="utf-8") as f:
    f.write(text)

import os

path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

target = """@androidx.compose.runtime.Composable
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
}"""

replacement = """@androidx.compose.runtime.Composable
fun AppleJellySlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: (() -> Unit)? = null,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    activeColor: Color = Color(0xFF007AFF),
    inactiveColor: Color = Color(0xFFE5E5EA)
) {
    androidx.compose.material3.Slider(
        value = value,
        onValueChange = onValueChange,
        onValueChangeFinished = onValueChangeFinished,
        valueRange = valueRange,
        colors = androidx.compose.material3.SliderDefaults.colors(
            activeTrackColor = Color.Transparent,
            inactiveTrackColor = Color.Transparent,
            thumbColor = Color.Transparent
        ),
        track = { sliderState ->
            val fraction = (sliderState.value - sliderState.valueRange.start) / (sliderState.valueRange.endInclusive - sliderState.valueRange.start)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp)
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(50))
                    .background(inactiveColor)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fraction = fraction.coerceIn(0f, 1f))
                        .background(activeColor)
                )
            }
        },
        thumb = { sliderState ->
            val isPressed = true // We can't easily get pressed state without custom interaction source, but we can do a scale animation if we wanted
            val thumbWidth = 24.dp
            val thumbHeight = 18.dp
            
            Box(
                modifier = Modifier
                    .size(width = thumbWidth, height = thumbHeight)
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(50))
                    .background(Color.White) // Base white
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            0f to Color.White.copy(alpha = 0.95f),
                            0.45f to Color.White.copy(alpha = 0.1f),
                            0.5f to Color.Transparent,
                            1.0f to Color.Transparent
                        )
                    )
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            0f to Color.Transparent,
                            0.5f to Color.Transparent,
                            0.6f to Color.White.copy(alpha = 0f),
                            1.0f to Color.White.copy(alpha = 0.85f)
                        )
                    )
                    // Inner shadow simulation
                    .androidx.compose.foundation.border(
                        0.5.dp, 
                        Color.Black.copy(alpha = 0.1f), 
                        androidx.compose.foundation.shape.RoundedCornerShape(50)
                    )
                    .androidx.compose.ui.draw.shadow(
                        elevation = 3.dp,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(50),
                        spotColor = Color.Black.copy(alpha = 0.2f)
                    )
            )
        }
    )
}"""

if target in text:
    text = text.replace(target, replacement)
    with open(path, "w", encoding="utf-8") as f:
        f.write(text)
    print("Fixed Jelly Slider with thumb and track")
else:
    print("Target not found")

import os

path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

# Remove the old AppleJellySlider block entirely.
start_index = text.find("@androidx.compose.runtime.Composable\nfun AppleJellySlider(")
if start_index == -1:
    start_index = text.find("@androidx.compose.material3.ExperimentalMaterial3Api\n@androidx.compose.runtime.Composable\nfun AppleJellySlider(")

if start_index != -1:
    # Find the end of AppleJellySlider (the next Composable is AppleSettingsRow)
    end_index = text.find("fun AppleSettingsRow(", start_index)
    if end_index != -1:
        # Also remove the @Composable if it is right before AppleSettingsRow
        comp_index = text.rfind("@Composable", start_index, end_index)
        if comp_index != -1:
            end_index = comp_index
        text = text[:start_index] + text[end_index:]

new_jelly_slider = """@androidx.compose.material3.ExperimentalMaterial3Api
@androidx.compose.runtime.Composable
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
        thumb = {
            val thumbWidth = 24.dp
            val thumbHeight = 18.dp
            
            Box(
                modifier = Modifier
                    .size(width = thumbWidth, height = thumbHeight)
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(50))
                    .background(Color.White)
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            0.0f to Color.White.copy(alpha = 0.95f),
                            0.45f to Color.White.copy(alpha = 0.1f),
                            0.5f to Color.Transparent,
                            1.0f to Color.Transparent
                        )
                    )
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            0.0f to Color.Transparent,
                            0.5f to Color.Transparent,
                            0.6f to Color.White.copy(alpha = 0f),
                            1.0f to Color.White.copy(alpha = 0.85f)
                        )
                    )
                    .androidx.compose.foundation.border(
                        width = 0.5.dp, 
                        color = Color.Black.copy(alpha = 0.1f), 
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(50)
                    )
                    .shadow(
                        elevation = 3.dp,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(50),
                        spotColor = Color.Black.copy(alpha = 0.2f)
                    )
            )
        }
    )
}

"""

if "@Composable\nfun AppleSettingsRow(" in text:
    text = text.replace("@Composable\nfun AppleSettingsRow(", new_jelly_slider + "@Composable\nfun AppleSettingsRow(")
else:
    text = text.replace("fun AppleSettingsRow(", new_jelly_slider + "fun AppleSettingsRow(")

with open(path, "w", encoding="utf-8") as f:
    f.write(text)
print("AppleJellySlider replaced")

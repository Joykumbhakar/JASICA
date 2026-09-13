import os

path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

start_index = text.find("@OptIn(ExperimentalMaterial3Api::class)\n@Composable\nfun AppleJellySlider(")

if start_index != -1:
    end_index = text.find("@Composable\nfun AppleSettingsRow(", start_index)
    if end_index == -1:
        end_index = text.find("fun AppleSettingsRow(", start_index)
    
    text = text[:start_index] + text[end_index:]

new_jelly_slider = """@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppleJellySlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: (() -> Unit)? = null,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    activeColor: Color = Color(0xFF007AFF),
    inactiveColor: Color = Color(0xFFE5E5EA)
) {
    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val isPressed by androidx.compose.foundation.interaction.collectIsPressedAsState(interactionSource)
    val isDragged by androidx.compose.foundation.interaction.collectIsDraggedAsState(interactionSource)
    val isActive = isPressed || isDragged

    val scale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isActive) 1.25f else 1.0f,
        animationSpec = androidx.compose.animation.core.spring(
            dampingRatio = 0.4f, // bouncy jelly effect
            stiffness = 500f
        )
    )

    androidx.compose.material3.Slider(
        value = value,
        onValueChange = onValueChange,
        onValueChangeFinished = onValueChangeFinished,
        valueRange = valueRange,
        interactionSource = interactionSource,
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
                    .height(4.dp)
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
                    .androidx.compose.ui.draw.scale(scale)
                    .shadow(
                        elevation = 4.dp,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(50),
                        spotColor = Color.Black.copy(alpha = 0.3f),
                        ambientColor = Color.Black.copy(alpha = 0.1f)
                    )
                    .background(Color.White, shape = androidx.compose.foundation.shape.RoundedCornerShape(50))
                    .border(
                        width = 0.5.dp, 
                        color = Color.Black.copy(alpha = 0.15f), 
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(50)
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
print("Slider updated with zoom and pure white thumb")

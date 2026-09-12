import re

with open(r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt", "r", encoding="utf-8") as f:
    text = f.read()

# 1. Update SettingsScreen call
text = text.replace(
    '                onSave              = onSaveSettings\n            )',
    '                onSave              = onSaveSettings,\n                hazeState           = hazeState\n            )'
)

# 2. Update SettingsScreen signature
text = text.replace(
    '    sharedPrefs: android.content.SharedPreferences,\n    onDismiss: () -> Unit,\n    onSave: (String, String, Boolean) -> Unit\n) {',
    '    sharedPrefs: android.content.SharedPreferences,\n    onDismiss: () -> Unit,\n    onSave: (String, String, Boolean) -> Unit,\n    hazeState: dev.chrisbanes.haze.HazeState? = null\n) {'
)

# 3. Add LiquidGlassKnob before AppleSlider
knob_code = """
@Composable
fun LiquidGlassKnob(
    width: androidx.compose.ui.unit.Dp,
    height: androidx.compose.ui.unit.Dp,
    scale: Float,
    baseAlpha: Float,
    hazeState: dev.chrisbanes.haze.HazeState? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(width, height)
            .scale(scale)
            .clip(RoundedCornerShape(50.dp))
            .then(if (hazeState != null) Modifier.hazeChild(state = hazeState) else Modifier)
            .background(Color.White.copy(alpha = baseAlpha))
            .border(0.5.dp, Color.Black.copy(alpha = 0.1f), RoundedCornerShape(50.dp))
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(50.dp),
                spotColor = Color.Black.copy(alpha = 0.2f),
                ambientColor = Color.Black.copy(alpha = 0.1f),
                clip = false
            )
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(start = 2.dp, end = 2.dp, top = 1.dp)
                .fillMaxWidth()
                .fillMaxHeight(0.48f)
                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomStart = 10.dp, bottomEnd = 10.dp))
                .background(
                    Brush.verticalGradient(
                        0.0f to Color.White.copy(alpha = 0.95f),
                        1.0f to Color.White.copy(alpha = 0.1f)
                    )
                )
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(start = 2.dp, end = 2.dp, bottom = 1.dp)
                .fillMaxWidth()
                .fillMaxHeight(0.35f)
                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp, bottomStart = 12.dp, bottomEnd = 12.dp))
                .background(
                    Brush.verticalGradient(
                        0.0f to Color.Transparent,
                        1.0f to Color.White.copy(alpha = 0.85f)
                    )
                )
        )
    }
}

@Composable
fun AppleSlider"""
text = text.replace("@Composable\nfun AppleSlider", knob_code)

# 4. Replace AppleSlider implementation
old_slider_pattern = r'fun AppleSlider\(.*?\}\n\}\n\n@Composable\nfun AppleSwitch'
new_slider = """fun AppleSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    modifier: Modifier = Modifier,
    steps: Int = 0,
    hazeState: dev.chrisbanes.haze.HazeState? = null
) {
    val activeTrackColor = Color(0xFF0A84FF)
    val inactiveTrackColor = Color(0xFFE5E5EA)

    BoxWithConstraints(
        modifier = modifier.height(22.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        val maxW = constraints.maxWidth.toFloat()
        val thumbRadiusPx = with(LocalDensity.current) { 12.dp.toPx() } 
        val trackWidthPx = maxW - 2 * thumbRadiusPx

        val fraction = ((value - valueRange.start) / (valueRange.endInclusive - valueRange.start)).coerceIn(0f, 1f)
        val thumbOffsetX = (fraction * trackWidthPx)

        var isDragging by remember { mutableStateOf(false) }
        val targetWidth = if (isDragging) 28.dp else 24.dp
        val targetScale = if (isDragging) 1.15f else 1f
        
        val thumbWidth by animateDpAsState(targetWidth, animationSpec = tween(200, easing = FastOutSlowInEasing), label = "tw")
        val thumbScale by animateFloatAsState(targetScale, animationSpec = tween(200, easing = FastOutSlowInEasing), label = "ts")
        
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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragStart = { isDragging = true },
                        onDragEnd = { isDragging = false },
                        onDragCancel = { isDragging = false }
                    ) { change, _ ->
                        change.consume()
                        updateValueFromOffset(change.position.x)
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = { offset ->
                            isDragging = true
                            updateValueFromOffset(offset.x)
                            tryAwaitRelease()
                            isDragging = false
                        }
                    )
                }
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .height(6.dp)
                .clip(CircleShape)
                .background(inactiveTrackColor)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(activeTrackColor)
            )
        }

        Box(
            modifier = Modifier
                .offset { IntOffset(thumbOffsetX.roundToInt(), 0) }
                .size(width = thumbWidth, height = 18.dp),
            contentAlignment = Alignment.Center
        ) {
            LiquidGlassKnob(
                width = thumbWidth,
                height = 18.dp,
                scale = thumbScale,
                baseAlpha = 0.9f,
                hazeState = hazeState
            )
        }
    }
}

@Composable
fun AppleSwitch"""
text = re.sub(old_slider_pattern, new_slider, text, flags=re.DOTALL)

# 5. Replace AppleSwitch implementation
old_switch_pattern = r'fun AppleSwitch\(.*?\}\n\}\n\n@Composable\nfun LucideSliders'
new_switch = """fun AppleSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    hazeState: dev.chrisbanes.haze.HazeState? = null
) {
    var isPressed by remember { mutableStateOf(false) }
    val trackColor by animateColorAsState(if (checked) Color(0xFF34C759) else Color(0xFFE5E5EA), label = "trackColor")
    
    val targetTranslation = if (checked && isPressed) 15f 
                            else if (checked) 16f 
                            else if (isPressed) -3f 
                            else 0f
                            
    val targetWidth = if (isPressed) 28.dp else 24.dp
    val targetScale = if (isPressed) 1.26f else 1f
    
    val thumbTranslationX by animateFloatAsState(targetTranslation, animationSpec = tween(250, easing = FastOutSlowInEasing), label = "tx")
    val thumbWidth by animateDpAsState(targetWidth, animationSpec = tween(250, easing = FastOutSlowInEasing), label = "tw")
    val thumbScale by animateFloatAsState(targetScale, animationSpec = tween(250, easing = FastOutSlowInEasing), label = "ts")

    Box(
        modifier = modifier
            .width(44.dp)
            .height(22.dp)
            .clip(RoundedCornerShape(100.dp))
            .background(trackColor)
            .border(0.5.dp, Color.Black.copy(alpha=0.1f), RoundedCornerShape(100.dp))
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = {
                        onCheckedChange(!checked)
                    }
                )
            }
    ) {
        Box(
            modifier = Modifier
                .padding(top = 2.dp, bottom = 2.dp)
                .offset(x = (thumbTranslationX + 2).dp)
                .size(width = thumbWidth, height = 18.dp)
        ) {
            LiquidGlassKnob(
                width = thumbWidth,
                height = 18.dp,
                scale = thumbScale,
                baseAlpha = 0.1f,
                hazeState = hazeState
            )
        }
    }
}

@Composable
fun LucideSliders"""
text = re.sub(old_switch_pattern, new_switch, text, flags=re.DOTALL)

# Update AppleSwitch usages in SettingsScreen to pass hazeState
text = text.replace(
    'AppleSwitch(checked = darkModeInput, onCheckedChange = { darkModeInput = it })',
    'AppleSwitch(checked = darkModeInput, onCheckedChange = { darkModeInput = it }, hazeState = hazeState)'
)
text = text.replace(
    'AppleSwitch(checked = wakeWordInput, onCheckedChange = { wakeWordInput = it })',
    'AppleSwitch(checked = wakeWordInput, onCheckedChange = { wakeWordInput = it }, hazeState = hazeState)'
)
text = text.replace(
    'AppleSwitch(checked = historyLoggingInput, onCheckedChange = { historyLoggingInput = it })',
    'AppleSwitch(checked = historyLoggingInput, onCheckedChange = { historyLoggingInput = it }, hazeState = hazeState)'
)
text = text.replace(
    'AppleSwitch(checked = waterReminderInput, onCheckedChange = { waterReminderInput = it })',
    'AppleSwitch(checked = waterReminderInput, onCheckedChange = { waterReminderInput = it }, hazeState = hazeState)'
)
text = text.replace(
    'AppleSlider(value = waterInterval.toFloat()',
    'AppleSlider(hazeState = hazeState, value = waterInterval.toFloat()'
)

with open(r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt", "w", encoding="utf-8") as f:
    f.write(text)

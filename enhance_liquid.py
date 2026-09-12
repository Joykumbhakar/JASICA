import re

with open(r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt", "r", encoding="utf-8") as f:
    text = f.read()

# 1. Slider: Replace animationSpecs with spring
text = text.replace(
    'val thumbWidth by animateDpAsState(targetWidth, animationSpec = tween(200, easing = FastOutSlowInEasing), label = "tw")',
    'val thumbWidth by animateDpAsState(targetWidth, animationSpec = androidx.compose.animation.core.spring(dampingRatio = 0.6f, stiffness = 400f), label = "tw")'
)
text = text.replace(
    'val thumbScale by animateFloatAsState(targetScale, animationSpec = tween(200, easing = FastOutSlowInEasing), label = "ts")',
    'val thumbScale by animateFloatAsState(targetScale, animationSpec = androidx.compose.animation.core.spring(dampingRatio = 0.6f, stiffness = 400f), label = "ts")'
)

# 2. Switch: Replace animationSpecs with spring
text = text.replace(
    'val thumbTranslationX by animateFloatAsState(targetTranslation, animationSpec = tween(250, easing = FastOutSlowInEasing), label = "tx")',
    'val thumbTranslationX by animateFloatAsState(targetTranslation, animationSpec = androidx.compose.animation.core.spring(dampingRatio = 0.6f, stiffness = 400f), label = "tx")'
)
text = text.replace(
    'val thumbWidth by animateDpAsState(targetWidth, animationSpec = tween(250, easing = FastOutSlowInEasing), label = "tw")',
    'val thumbWidth by animateDpAsState(targetWidth, animationSpec = androidx.compose.animation.core.spring(dampingRatio = 0.6f, stiffness = 400f), label = "tw")'
)
text = text.replace(
    'val thumbScale by animateFloatAsState(targetScale, animationSpec = tween(250, easing = FastOutSlowInEasing), label = "ts")',
    'val thumbScale by animateFloatAsState(targetScale, animationSpec = androidx.compose.animation.core.spring(dampingRatio = 0.6f, stiffness = 400f), label = "ts")'
)

# Switch: Use spring for color
text = text.replace(
    'val trackColor by animateColorAsState(if (checked) Color(0xFF34C759) else Color(0xFFE5E5EA), label = "trackColor")',
    'val trackColor by animateColorAsState(if (checked) Color(0xFF34C759) else Color(0xFFE5E5EA), animationSpec = androidx.compose.animation.core.tween(300), label = "trackColor")'
)

# 3. Add Haptic feedback and minimumInteractiveComponentSize to Switch
text = text.replace(
    'fun AppleSwitch(\n    checked: Boolean,\n    onCheckedChange: (Boolean) -> Unit,\n    modifier: Modifier = Modifier,\n    hazeState: dev.chrisbanes.haze.HazeState? = null\n) {',
    'fun AppleSwitch(\n    checked: Boolean,\n    onCheckedChange: (Boolean) -> Unit,\n    modifier: Modifier = Modifier,\n    hazeState: dev.chrisbanes.haze.HazeState? = null\n) {\n    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current'
)

text = text.replace(
    '        modifier = modifier\n            .width(44.dp)',
    '        modifier = modifier\n            .androidx.compose.material3.minimumInteractiveComponentSize()\n            .width(44.dp)'
)

old_tap = """            .pointerInput(Unit) {
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
            }"""
new_tap = """            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = {
                        onCheckedChange(!checked)
                    }
                )
            }"""
text = text.replace(old_tap, new_tap)

# 4. Same for AppleSlider - Add Haptics
text = text.replace(
    'fun AppleSlider(\n    value: Float,\n    onValueChange: (Float) -> Unit,\n    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,\n    modifier: Modifier = Modifier,\n    steps: Int = 0,\n    hazeState: dev.chrisbanes.haze.HazeState? = null\n) {',
    'fun AppleSlider(\n    value: Float,\n    onValueChange: (Float) -> Unit,\n    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,\n    modifier: Modifier = Modifier,\n    steps: Int = 0,\n    hazeState: dev.chrisbanes.haze.HazeState? = null\n) {\n    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current'
)

old_slider_drag = """                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragStart = { isDragging = true },
                        onDragEnd = { isDragging = false },
                        onDragCancel = { isDragging = false }
                    ) { change, _ ->
                        change.consume()
                        updateValueFromOffset(change.position.x)
                    }
                }"""
new_slider_drag = """                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragStart = { 
                            isDragging = true 
                            haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                        },
                        onDragEnd = { isDragging = false },
                        onDragCancel = { isDragging = false }
                    ) { change, _ ->
                        change.consume()
                        updateValueFromOffset(change.position.x)
                    }
                }"""
text = text.replace(old_slider_drag, new_slider_drag)

with open(r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt", "w", encoding="utf-8") as f:
    f.write(text)

package com.jasica.ai.controller.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// EXACT LIQUID GLASS COLORS MATCHING CSS
val LiquidTrackOff = Color(0xFFE5E5EA)
val LiquidTrackOn = Color(0xFF34C759)
val LiquidSliderBlue = Color(0xFF0A84FF)
val LiquidKnobBase = Color(0xE6FFFFFF)

/**
 * Exact Liquid Glass Toggle Component (44px by 22px with 24px x 18px Jelly Knob)
 */
@Composable
fun LiquidGlassToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    var isPressed by remember { mutableStateOf(false) }

    // Animated Track Color
    val trackColor by animateColorAsState(
        targetValue = if (checked) LiquidTrackOn else LiquidTrackOff,
        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
        label = "trackColor"
    )

    // Knob offset transition with spring overshoot bounce: cubic-bezier(0.34, 1.8, 0.64, 1)
    val knobOffset by animateDpAsState(
        targetValue = if (checked) 18.dp else 2.dp,
        animationSpec = spring(
            dampingRatio = 0.52f, // Exact overshoot bounce
            stiffness = 500f
        ),
        label = "knobOffset"
    )

    // Jelly knob width stretch on active press (24dp -> 28dp)
    val knobWidth by animateDpAsState(
        targetValue = if (isPressed) 28.dp else 24.dp,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = 600f
        ),
        label = "knobWidth"
    )

    val knobScale by animateFloatAsState(
        targetValue = if (isPressed) 1.15f else 1.0f,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = 600f
        ),
        label = "knobScale"
    )

    Box(
        modifier = modifier
            .size(width = 44.dp, height = 22.dp)
            .pointerInput(enabled) {
                if (enabled) {
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
            },
        contentAlignment = Alignment.CenterStart
    ) {
        // 1. TRACK (44dp x 22dp pill with inner shadow)
        Canvas(modifier = Modifier.size(width = 44.dp, height = 22.dp)) {
            val corner = CornerRadius(size.height / 2f, size.height / 2f)
            // Base Track
            drawRoundRect(
                color = trackColor,
                size = size,
                cornerRadius = corner
            )

            // Inset shadow simulation (Top dark shadow)
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0x2E000000),
                        Color(0x10000000),
                        Color.Transparent
                    ),
                    startY = 0f,
                    endY = 7.dp.toPx()
                ),
                size = size,
                cornerRadius = corner
            )

            // Inner border rim
            drawRoundRect(
                color = Color(0x18000000),
                size = size,
                cornerRadius = corner,
                style = Stroke(width = 0.5.dp.toPx())
            )
        }

        // 2. LIQUID JELLY KNOB (24dp x 18dp at 2dp Y offset)
        Box(
            modifier = Modifier
                .offset(x = knobOffset, y = 0.dp)
                .size(width = knobWidth, height = 18.dp)
                .scale(knobScale)
                .shadow(
                    elevation = 2.dp,
                    shape = RoundedCornerShape(50.dp),
                    clip = false,
                    ambientColor = Color(0x33000000),
                    spotColor = Color(0x40000000)
                )
        ) {
            Canvas(modifier = Modifier.size(width = knobWidth, height = 18.dp)) {
                val knobCorner = CornerRadius(size.height / 2f, size.height / 2f)

                // Base Glassy White Capsule
                drawRoundRect(
                    color = LiquidKnobBase,
                    size = size,
                    cornerRadius = knobCorner
                )

                // Top Glossy Reflection Curve (linear-gradient: 95% white to 10%)
                val topGlossHeight = size.height * 0.48f
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xF2FFFFFF),
                            Color(0x33FFFFFF),
                            Color(0x00FFFFFF)
                        ),
                        startY = 1.dp.toPx(),
                        endY = topGlossHeight
                    ),
                    topLeft = Offset(2.dp.toPx(), 1.dp.toPx()),
                    size = Size(size.width - 4.dp.toPx(), topGlossHeight),
                    cornerRadius = CornerRadius(knobCorner.x, knobCorner.y)
                )

                // Bottom Ambient Light Rim (linear-gradient: 85% white to 0%)
                val bottomRimHeight = size.height * 0.35f
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0x00FFFFFF),
                            Color(0x40FFFFFF),
                            Color(0xD9FFFFFF)
                        ),
                        startY = size.height - bottomRimHeight,
                        endY = size.height - 1.dp.toPx()
                    ),
                    topLeft = Offset(2.dp.toPx(), size.height - bottomRimHeight - 1.dp.toPx()),
                    size = Size(size.width - 4.dp.toPx(), bottomRimHeight),
                    cornerRadius = CornerRadius(knobCorner.x, knobCorner.y)
                )

                // Inset Rim Highlights
                drawRoundRect(
                    color = Color(0x66FFFFFF),
                    size = size,
                    cornerRadius = knobCorner,
                    style = Stroke(width = 0.8.dp.toPx())
                )

                // Subtle Outer Edge Shadow
                drawRoundRect(
                    color = Color(0x1F000000),
                    size = size,
                    cornerRadius = knobCorner,
                    style = Stroke(width = 0.5.dp.toPx())
                )
            }
        }
    }
}

/**
 * Exact Custom Liquid Jelly Range Slider Component
 */
@Composable
fun LiquidGlassSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..100f,
    enabled: Boolean = true
) {
    var isDragging by remember { mutableStateOf(false) }

    val normalizedValue = ((value - valueRange.start) / (valueRange.endInclusive - valueRange.start)).coerceIn(0f, 1f)

    // Animated thumb width on active touch (24dp -> 28dp)
    val thumbWidth by animateDpAsState(
        targetValue = if (isDragging) 28.dp else 24.dp,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 600f),
        label = "thumbWidth"
    )

    val thumbScale by animateFloatAsState(
        targetValue = if (isDragging) 1.15f else 1.0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 600f),
        label = "thumbScale"
    )

    Box(
        modifier = modifier
            .height(26.dp)
            .fillMaxWidth()
            .pointerInput(enabled) {
                if (!enabled) return@pointerInput
                detectTapGestures(
                    onPress = { offset ->
                        isDragging = true
                        val newNorm = (offset.x / size.width).coerceIn(0f, 1f)
                        val newValue = valueRange.start + newNorm * (valueRange.endInclusive - valueRange.start)
                        onValueChange(newValue)
                        tryAwaitRelease()
                        isDragging = false
                    }
                )
            }
            .pointerInput(enabled) {
                if (!enabled) return@pointerInput
                detectHorizontalDragGestures(
                    onDragStart = { isDragging = true },
                    onDragEnd = { isDragging = false },
                    onDragCancel = { isDragging = false },
                    onHorizontalDrag = { change, _ ->
                        change.consume()
                        val newNorm = (change.position.x / size.width).coerceIn(0f, 1f)
                        val newValue = valueRange.start + newNorm * (valueRange.endInclusive - valueRange.start)
                        onValueChange(newValue)
                    }
                )
            },
        contentAlignment = Alignment.CenterStart
    ) {
        // 1. SLIDER TRACK (6dp height pill with inner shadow)
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
        ) {
            val trackHeight = 6.dp.toPx()
            val trackCorner = CornerRadius(trackHeight / 2f, trackHeight / 2f)
            val fillWidth = size.width * normalizedValue

            // Inactive Track (Right: #e5e5ea)
            drawRoundRect(
                color = LiquidTrackOff,
                size = size,
                cornerRadius = trackCorner
            )

            // Active Track (Left: #0a84ff)
            if (fillWidth > 0f) {
                drawRoundRect(
                    color = LiquidSliderBlue,
                    size = Size(fillWidth, size.height),
                    cornerRadius = trackCorner
                )
            }

            // Inset Top Shadow
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0x28000000),
                        Color.Transparent
                    ),
                    startY = 0f,
                    endY = 3.dp.toPx()
                ),
                size = size,
                cornerRadius = trackCorner
            )

            // Subtle 0.5px Inset Rim
            drawRoundRect(
                color = Color(0x14000000),
                size = size,
                cornerRadius = trackCorner,
                style = Stroke(width = 0.5.dp.toPx())
            )
        }

        // 2. LIQUID JELLY SLIDER THUMB (24dp x 18dp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(18.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(18.dp)
            ) {
                val availableWidth = size.width - thumbWidth.toPx()
                val thumbX = availableWidth * normalizedValue
                val thumbHeight = 18.dp.toPx()
                val thumbWidthPx = thumbWidth.toPx()
                val thumbCorner = CornerRadius(thumbHeight / 2f, thumbHeight / 2f)
                val thumbOffset = Offset(thumbX, (size.height - thumbHeight) / 2f)

                // Drop Shadow
                drawRoundRect(
                    color = Color(0x33000000),
                    topLeft = Offset(thumbOffset.x, thumbOffset.y + 2.dp.toPx()),
                    size = Size(thumbWidthPx, thumbHeight),
                    cornerRadius = thumbCorner
                )

                // Base Glassy White Capsule (#ffffff 90%)
                drawRoundRect(
                    color = LiquidKnobBase,
                    topLeft = thumbOffset,
                    size = Size(thumbWidthPx, thumbHeight),
                    cornerRadius = thumbCorner
                )

                // Top Gloss Reflection
                val topGlossHeight = thumbHeight * 0.45f
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xF2FFFFFF),
                            Color(0x26FFFFFF),
                            Color(0x00FFFFFF)
                        ),
                        startY = thumbOffset.y + 1.dp.toPx(),
                        endY = thumbOffset.y + topGlossHeight
                    ),
                    topLeft = Offset(thumbOffset.x + 2.dp.toPx(), thumbOffset.y + 1.dp.toPx()),
                    size = Size(thumbWidthPx - 4.dp.toPx(), topGlossHeight),
                    cornerRadius = thumbCorner
                )

                // Bottom Ambient Light Reflection
                val bottomRimHeight = thumbHeight * 0.40f
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0x00FFFFFF),
                            Color(0x33FFFFFF),
                            Color(0xD9FFFFFF)
                        ),
                        startY = thumbOffset.y + thumbHeight - bottomRimHeight,
                        endY = thumbOffset.y + thumbHeight - 1.dp.toPx()
                    ),
                    topLeft = Offset(thumbOffset.x + 2.dp.toPx(), thumbOffset.y + thumbHeight - bottomRimHeight - 1.dp.toPx()),
                    size = Size(thumbWidthPx - 4.dp.toPx(), bottomRimHeight),
                    cornerRadius = thumbCorner
                )

                // Inner White Highlight Border
                drawRoundRect(
                    color = Color(0x66FFFFFF),
                    topLeft = thumbOffset,
                    size = Size(thumbWidthPx, thumbHeight),
                    cornerRadius = thumbCorner,
                    style = Stroke(width = 0.8.dp.toPx())
                )

                // Outer Edge Thin Rim
                drawRoundRect(
                    color = Color(0x1F000000),
                    topLeft = thumbOffset,
                    size = Size(thumbWidthPx, thumbHeight),
                    cornerRadius = thumbCorner,
                    style = Stroke(width = 0.5.dp.toPx())
                )
            }
        }
    }
}

import re

file_path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

old_box = """@Composable
fun LucideIconBox(
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .size(30.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}"""

new_box = """@Composable
fun LucideIconBox(
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    // Map solid colors to Apple gradients
    val brush = remember(backgroundColor) {
        when (backgroundColor.value.toULong()) {
            0xFF007AFFuL -> androidx.compose.ui.graphics.Brush.verticalGradient(listOf(Color(0xFF44A6FF), Color(0xFF007AFF))) // Blue
            0xFF5856D6uL -> androidx.compose.ui.graphics.Brush.verticalGradient(listOf(Color(0xFF7868E6), Color(0xFF5856D6))) // Indigo
            0xFF34C759uL -> androidx.compose.ui.graphics.Brush.verticalGradient(listOf(Color(0xFF4CD964), Color(0xFF34C759))) // Green
            0xFFFF9500uL -> androidx.compose.ui.graphics.Brush.verticalGradient(listOf(Color(0xFFFFB340), Color(0xFFFF9500))) // Orange
            0xFFFF3B30uL -> androidx.compose.ui.graphics.Brush.verticalGradient(listOf(Color(0xFFFF665A), Color(0xFFFF3B30))) // Red
            0xFF8E8E93uL -> androidx.compose.ui.graphics.Brush.verticalGradient(listOf(Color(0xFFA3AAB2), Color(0xFF8E8E93))) // Silver
            0xFFAF52DEuL -> androidx.compose.ui.graphics.Brush.verticalGradient(listOf(Color(0xFFC973F0), Color(0xFFAF52DE))) // Purple
            0xFF30B0C7uL -> androidx.compose.ui.graphics.Brush.verticalGradient(listOf(Color(0xFF5AC8FA), Color(0xFF30B0C7))) // Teal
            else -> androidx.compose.ui.graphics.Brush.verticalGradient(listOf(backgroundColor, backgroundColor))
        }
    }

    Box(
        modifier = modifier
            .size(30.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(brush)
            // Add top inner shadow (white 0.3)
            .border(
                width = 0.5.dp,
                color = Color.White.copy(alpha = 0.3f),
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        // Drop shadow for the icon itself
        Box(
            modifier = Modifier.graphicsLayer {
                shadowElevation = 2f
                shape = RoundedCornerShape(1.dp)
                ambientShadowColor = Color.Black.copy(alpha=0.12f)
                spotShadowColor = Color.Black.copy(alpha=0.12f)
            }
        ) {
            content()
        }
    }
}"""

if old_box in content:
    content = content.replace(old_box, new_box)
    with open(file_path, "w", encoding="utf-8") as f:
        f.write(content)
    print("Updated LucideIconBox")
else:
    print("Could not find old LucideIconBox")

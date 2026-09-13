import re

file_path = r"E:\Controller\app\src\main\java\com\bristi\controller\WaterAlarmActivity.kt"
with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

# Replace the text inside the drop icon
content = content.replace('Text("dY\' ", fontSize = 60.sp)', 'Text("??", fontSize = 60.sp)')

# Let's improve the animation: Add a third wave, change colors slightly.
old_canvas = r"""        // Animated Water Waves
        Canvas\(modifier = Modifier\.fillMaxSize\(\)\) \{.*?// Content"""

new_canvas = """        // Animated Water Waves
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val midY = height * 0.45f // Higher water level
            
            // Wave 1 (Back)
            val path1 = Path()
            path1.moveTo(0f, height)
            for (i in 0..width.toInt() step 10) {
                val x = i.toFloat()
                val y = midY + 45f * sin((x / width * 1.5f * Math.PI) + phase1).toFloat()
                path1.lineTo(x, y)
            }
            path1.lineTo(width, height)
            path1.close()
            drawPath(path1, brush = Brush.verticalGradient(listOf(Color(0xFF0EA5E9).copy(alpha = 0.4f), Color(0xFF0369A1).copy(alpha = 0.8f))))

            // Wave 2 (Middle)
            val path2 = Path()
            path2.moveTo(0f, height)
            for (i in 0..width.toInt() step 10) {
                val x = i.toFloat()
                val y = (midY + 25f) + 30f * sin((x / width * 2f * Math.PI) - phase2 * 1.2f).toFloat()
                path2.lineTo(x, y)
            }
            path2.lineTo(width, height)
            path2.close()
            drawPath(path2, brush = Brush.verticalGradient(listOf(Color(0xFF38BDF8).copy(alpha = 0.6f), Color(0xFF0284C7))))

            // Wave 3 (Front)
            val path3 = Path()
            path3.moveTo(0f, height)
            for (i in 0..width.toInt() step 10) {
                val x = i.toFloat()
                val y = (midY + 50f) + 20f * sin((x / width * 3f * Math.PI) + phase1 * 1.5f).toFloat()
                path3.lineTo(x, y)
            }
            path3.lineTo(width, height)
            path3.close()
            drawPath(path3, brush = Brush.verticalGradient(listOf(Color(0xFF7DD3FC).copy(alpha = 0.8f), Color(0xFF0284C7))))
        }

        // Content"""

content = re.sub(old_canvas, new_canvas, content, flags=re.DOTALL)

with open(file_path, "w", encoding="utf-8") as f:
    f.write(content)
print("Updated water animation")

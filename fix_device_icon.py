import re

file_path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

old_icon = """                Icon(
                    imageVector = Icons.Rounded.Lightbulb,
                    contentDescription = null,
                    tint = if (isChecked) activeAccent else subTextColor,
                    modifier = Modifier.size(20.dp)
                )"""
new_icon = """                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = R.drawable.robot),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(if (isChecked) activeAccent else subTextColor)
                )"""

content = content.replace(old_icon, new_icon)

with open(file_path, "w", encoding="utf-8") as f:
    f.write(content)
print("Fixed device icon")

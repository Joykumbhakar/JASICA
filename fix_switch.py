import re

file_path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

# AppleSwitch sizing
content = content.replace('val targetWidth = if (isPressed) 28.dp else 24.dp', 'val targetWidth = if (isPressed) 33.dp else 29.dp')
content = content.replace('.width(44.dp)\n            .height(22.dp)', '.width(49.dp)\n            .height(27.dp)')
content = content.replace('height = 18.dp', 'height = 23.dp')
content = content.replace('.size(width = thumbWidth, height = 18.dp)', '.size(width = thumbWidth, height = 23.dp)')

# LiquidGlassKnob inner glares rounding
content = content.replace('RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomStart = 10.dp, bottomEnd = 10.dp)', 'RoundedCornerShape(50.dp)')
content = content.replace('RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp, bottomStart = 12.dp, bottomEnd = 12.dp)', 'RoundedCornerShape(50.dp)')

with open(file_path, "w", encoding="utf-8") as f:
    f.write(content)
print("Done fixing switch.")

import re

file_path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

broken_func = """fun AppleMenuItemImage(
    painter: androidx.compose.ui.graphics.painter.Painter,
    text: String,
    onClick: () -> Unit
) {"""
fixed_func = """@Composable
fun AppleMenuItemImage(
    painter: androidx.compose.ui.graphics.painter.Painter,
    text: String,
    onClick: () -> Unit
) {"""
content = content.replace(broken_func, fixed_func)

with open(file_path, "w", encoding="utf-8") as f:
    f.write(content)
print("Added @Composable.")

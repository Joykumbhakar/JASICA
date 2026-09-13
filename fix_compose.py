import re

file_path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

# Fix AppleMenuItem
broken_menu = """fun AppleMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,"""
fixed_menu = """@Composable
fun AppleMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,"""
content = content.replace(broken_menu, fixed_menu)

# Fix AppleMenuItemImage detectTapGestures
broken_detect = """androidx.compose.foundation.gestures.detectTapGestures("""
fixed_detect = """detectTapGestures("""
content = content.replace(broken_detect, fixed_detect)

with open(file_path, "w", encoding="utf-8") as f:
    f.write(content)
print("Done fixing @Composable and detectTapGestures.")

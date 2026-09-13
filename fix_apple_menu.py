import re

file_path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

broken_code = """fun AppleMenuItem(
    Row(
        modifier = Modifier"""
fixed_code = """fun AppleMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier"""

content = content.replace(broken_code, fixed_code)

with open(file_path, "w", encoding="utf-8") as f:
    f.write(content)
print("Done fixing AppleMenuItem.")

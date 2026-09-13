import re

file_path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

content = content.replace('val targetWidth = if (isPressed) 33.dp else 29.dp', 'val targetWidth = if (isPressed) 35.dp else 31.dp')
content = content.replace('.width(49.dp)\n            .height(27.dp)', '.width(51.dp)\n            .height(27.dp)')
content = content.replace('.width(49.dp)\r\n            .height(27.dp)', '.width(51.dp)\r\n            .height(27.dp)') # Catch CRLF just in case

with open(file_path, "w", encoding="utf-8") as f:
    f.write(content)
print("Done fixing switch width.")

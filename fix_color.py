import re

file_path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

content = content.replace('LucideIconBox(backgroundColor = Color(0xFF636366))', 'LucideIconBox(backgroundColor = Color(0xFF007AFF))')

with open(file_path, "w", encoding="utf-8") as f:
    f.write(content)
print("Fixed gray color")

import re

file_path = r'e:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    lines = f.readlines()

for i, line in enumerate(lines):
    if 'split("\\\\\\\\' in line or 'split("\\\\' in line:
        print(f"Line {i+1}: {line.strip()}")

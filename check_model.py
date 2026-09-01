import re
file_path = r'e:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    lines = f.readlines()
for i, line in enumerate(lines):
    if 'gemini' in line.lower() or 'model' in line.lower():
        if 'value =' in line or 'getString' in line:
            print(f"Line {i+1}: {line.strip()}")

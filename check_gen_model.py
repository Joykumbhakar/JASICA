import re
file_path = r'e:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    lines = f.readlines()
for i, line in enumerate(lines):
    if 'GenerativeModel(' in line:
        for j in range(i-2, i+5):
            print(f"Line {j+1}: {lines[j].strip()}")

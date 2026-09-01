import re
file_path = r'e:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    lines = f.readlines()
for i in range(2840, 2865):
    print(f"Line {i+1}: {lines[i].strip()}")

import re
file_path = r'e:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    lines = f.readlines()
for i, line in enumerate(lines):
    if 'Button(' in line and 'onSave(' in lines[i+1]:
        for j in range(i-1, i+6):
            print(f"Line {j+1}: {lines[j].strip()}")

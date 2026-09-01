import re
file_path = r'e:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    lines = f.readlines()
for i, line in enumerate(lines):
    if 'fun SettingsDialog(' in line:
        start_idx = i - 2
        print(f"Start index: {start_idx}")
        break

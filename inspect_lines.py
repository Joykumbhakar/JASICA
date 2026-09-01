import re
file_path = r'e:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    lines = f.readlines()

print('--- Line 458-470 ---')
for i in range(457, 469):
    print(f"Line {i+1}: {lines[i].strip()}")

print('--- Line 1740-1750 ---')
for i in range(1739, 1750):
    print(f"Line {i+1}: {lines[i].strip()}")

print('--- Line 2850-2860 ---')
for i in range(2849, 2860):
    print(f"Line {i+1}: {lines[i].strip()}")

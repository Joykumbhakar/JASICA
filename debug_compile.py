import re
file_path = r'e:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    lines = f.readlines()

for i in range(2310, 2335):
    print(f"Line {i+1}: {lines[i].strip()}")

# Also let's find the correct dark gray color
for line in lines:
    if 'val Jasica' in line:
        print(line.strip())

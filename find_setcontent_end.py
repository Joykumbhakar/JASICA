import re
file_path = r'e:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    lines = f.readlines()
start = -1
for i, line in enumerate(lines):
    if 'setContent {' in line:
        start = i
        break

if start != -1:
    brace_count = 0
    found_open = False
    for i in range(start, len(lines)):
        line = lines[i]
        if '{' in line:
            brace_count += line.count('{')
            found_open = True
        if '}' in line:
            brace_count -= line.count('}')
        if found_open and brace_count == 0:
            print(f"End brace at line {i+1}: {lines[i].strip()}")
            # Print previous lines for context
            for j in range(i-5, i+2):
                if j < len(lines):
                    print(f"Line {j+1}: {lines[j].strip()}")
            break

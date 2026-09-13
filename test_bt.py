import sys
with open(r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt", "r", encoding="utf-8") as f:
    lines = f.readlines()

bt_lines = []
for i, line in enumerate(lines):
    if "private var btAdapter" in line or "private var classicSocket" in line:
        bt_lines.append(i)

print(bt_lines)

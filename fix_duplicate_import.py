import re

path = r"E:\Controller\app\src\main\java\com\bristi\controller\FloatingControlService.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

# Remove exact duplicate lines of Color import
lines = text.split("\n")
new_lines = []
seen_imports = set()
for line in lines:
    if line.startswith("import "):
        if line in seen_imports:
            continue
        seen_imports.add(line)
    new_lines.append(line)

with open(path, "w", encoding="utf-8") as f:
    f.write("\n".join(new_lines))

print("Fixed duplicate imports")

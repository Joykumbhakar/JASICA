import re
file_path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

# Replace Box's modifier in AppleSettingsRow
content = re.sub(
    r"(if \(icon != null\) \{\s*Box\(\s*modifier = Modifier\s*)\.size\(28\.dp\)",
    r"\1.size(iconSize)",
    content
)

with open(file_path, "w", encoding="utf-8") as f:
    f.write(content)

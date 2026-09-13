import re

file_path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

content = content.replace("R.drawable.mic", "R.drawable.fluentui_system_icons_mic")

with open(file_path, "w", encoding="utf-8") as f:
    f.write(content)
print("Done fixing mic icon.")

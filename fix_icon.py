import re
path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

text = text.replace("Icons.Rounded.Article", "Icons.AutoMirrored.Rounded.Article")

with open(path, "w", encoding="utf-8") as f:
    f.write(text)
print("Fixed icon")

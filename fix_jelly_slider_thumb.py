import os

path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

target = """        thumb = {
            val thumbWidth = 24.dp"""

replacement = """        thumb = { sliderState ->
            val thumbWidth = 24.dp"""

if target in text:
    text = text.replace(target, replacement)
    with open(path, "w", encoding="utf-8") as f:
        f.write(text)
    print("Fixed thumb parameter")
else:
    print("Target not found")

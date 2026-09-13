import os

path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

if "import androidx.compose.ui.draw.scale" not in text:
    text = text.replace("import androidx.compose.ui.draw.shadow", "import androidx.compose.ui.draw.shadow\nimport androidx.compose.ui.draw.scale\nimport androidx.compose.animation.core.animateFloatAsState")

text = text.replace(".androidx.compose.ui.draw.scale(scale)", ".scale(scale)")

with open(path, "w", encoding="utf-8") as f:
    f.write(text)
print("Scale fixed")

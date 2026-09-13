import re

path = r"E:\Controller\app\src\main\java\com\bristi\controller\FloatingControlService.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

if "import androidx.compose.ui.graphics.graphicsLayer" not in text:
    text = text.replace("import androidx.compose.ui.graphics.Brush", "import androidx.compose.ui.graphics.Brush\nimport androidx.compose.ui.graphics.graphicsLayer\nimport androidx.compose.ui.graphics.Color")
    with open(path, "w", encoding="utf-8") as f:
        f.write(text)
    print("Added graphicsLayer import")
else:
    print("Import already exists")

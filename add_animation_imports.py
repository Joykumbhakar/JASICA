import os

file_path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

content = content.replace(
    "import androidx.compose.animation.fadeOut",
    "import androidx.compose.animation.fadeOut\nimport androidx.compose.animation.scaleIn\nimport androidx.compose.animation.scaleOut"
)

with open(file_path, "w", encoding="utf-8") as f:
    f.write(content)

print("Added animation imports")

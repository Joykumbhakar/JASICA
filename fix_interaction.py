import os

path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

if "import androidx.compose.foundation.interaction.collectIsPressedAsState" not in text:
    text = text.replace("import androidx.compose.ui.draw.scale", "import androidx.compose.ui.draw.scale\nimport androidx.compose.foundation.interaction.collectIsPressedAsState\nimport androidx.compose.foundation.interaction.collectIsDraggedAsState")

text = text.replace("androidx.compose.foundation.interaction.collectIsPressedAsState(interactionSource)", "interactionSource.collectIsPressedAsState()")
text = text.replace("androidx.compose.foundation.interaction.collectIsDraggedAsState(interactionSource)", "interactionSource.collectIsDraggedAsState()")

with open(path, "w", encoding="utf-8") as f:
    f.write(text)
print("Interaction fixed")

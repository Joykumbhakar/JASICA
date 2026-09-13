import os

path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

text = text.replace(
    "@androidx.compose.material3.ExperimentalMaterial3Api\n@androidx.compose.runtime.Composable\nfun AppleJellySlider(",
    "@androidx.compose.runtime.OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)\n@androidx.compose.runtime.Composable\nfun AppleJellySlider("
)

target_shadow = """.shadow(
                        elevation = 3.dp,"""

replacement_shadow = """.androidx.compose.ui.draw.shadow(
                        elevation = 3.dp,"""

text = text.replace(target_shadow, replacement_shadow)

with open(path, "w", encoding="utf-8") as f:
    f.write(text)
print("Fixed jelly slider build issues")

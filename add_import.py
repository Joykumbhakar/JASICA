import re

file_path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

import_statement = "import androidx.compose.animation.core.FastOutSlowInEasing"
if import_statement not in content:
    content = content.replace("import androidx.compose.animation.core.tween", "import androidx.compose.animation.core.tween\n" + import_statement)
    with open(file_path, "w", encoding="utf-8") as f:
        f.write(content)
    print("Added import.")
else:
    print("Import already exists.")

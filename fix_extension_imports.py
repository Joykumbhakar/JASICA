import os

path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

imports_to_add = """
import androidx.compose.foundation.border
import androidx.compose.ui.draw.shadow
import androidx.compose.material3.ExperimentalMaterial3Api
import kotlin.OptIn
"""

if "import androidx.compose.foundation.border" not in text:
    # Insert after package declaration
    text = text.replace("package com.bristi.controller", "package com.bristi.controller\n" + imports_to_add)

target_optin = "@androidx.compose.runtime.OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)"
text = text.replace(target_optin, "@OptIn(ExperimentalMaterial3Api::class)")

target_border = ".androidx.compose.foundation.border("
text = text.replace(target_border, ".border(")

target_shadow = ".androidx.compose.ui.draw.shadow("
text = text.replace(target_shadow, ".shadow(")

with open(path, "w", encoding="utf-8") as f:
    f.write(text)
print("Fixed extension function imports")

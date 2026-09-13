import re

file_path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

# Replace unfocusedBorderColor = Color.Transparent with unfocusedBorderColor = cardBorder
# in the OutlinedTextFieldDefaults block.

old_colors = r"""colors = androidx\.compose\.material3\.OutlinedTextFieldDefaults\.colors\(
\s*focusedBorderColor = Color\(0xFF007AFF\),
\s*unfocusedBorderColor = Color\.Transparent,
\s*focusedContainerColor = cardBg,
\s*unfocusedContainerColor = cardBg,
\s*cursorColor = Color\(0xFF007AFF\)
\s*\)"""

new_colors = """colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = Color(0xFF007AFF),
                                                unfocusedBorderColor = cardBorder,
                                                focusedContainerColor = cardBg,
                                                unfocusedContainerColor = cardBg,
                                                cursorColor = Color(0xFF007AFF)
                                            )"""

content = re.sub(old_colors, new_colors, content)

with open(file_path, "w", encoding="utf-8") as f:
    f.write(content)
print("Updated borders")

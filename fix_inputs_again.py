import re

file_path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

# Replace focusedContainerColor = fieldBg with focusedContainerColor = cardBg
# Replace unfocusedContainerColor = fieldBg with unfocusedContainerColor = cardBg
# But only for the fields inside the device config!
# We can just look for the OutlinedTextFieldDefaults.colors(...) block that has fieldBg and change it.

old_colors = r"""colors = androidx\.compose\.material3\.OutlinedTextFieldDefaults\.colors\(
\s*focusedBorderColor = Color\(0xFF007AFF\),
\s*unfocusedBorderColor = Color\.Transparent,
\s*focusedContainerColor = fieldBg,
\s*unfocusedContainerColor = fieldBg,
\s*cursorColor = Color\(0xFF007AFF\)
\s*\)"""

new_colors = """colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = Color(0xFF007AFF),
                                                unfocusedBorderColor = Color.Transparent,
                                                focusedContainerColor = cardBg,
                                                unfocusedContainerColor = cardBg,
                                                cursorColor = Color(0xFF007AFF)
                                            )"""

content = re.sub(old_colors, new_colors, content)

with open(file_path, "w", encoding="utf-8") as f:
    f.write(content)
print("Updated input styles to use cardBg")

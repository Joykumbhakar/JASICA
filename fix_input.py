import re

file_path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

# Replace shape = RoundedCornerShape(8.dp) with shape = RoundedCornerShape(12.dp) in the Hardware Config section
# Actually it's easier to just do a global replace for the OutlinedTextFields inside SettingsScreen.
# The user wants improved input box style for the hardware config.

old_colors = r'colors = androidx\.compose\.material3\.OutlinedTextFieldDefaults\.colors\(\s*focusedBorderColor = Color\(0xFF007AFF\),\s*unfocusedBorderColor = cardBorder,\s*focusedContainerColor = cardBg,\s*unfocusedContainerColor = cardBg\s*\)'
new_colors = r"""colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = Color(0xFF007AFF),
                                                unfocusedBorderColor = Color.Transparent,
                                                focusedContainerColor = fieldBg,
                                                unfocusedContainerColor = fieldBg,
                                                cursorColor = Color(0xFF007AFF)
                                            )"""

content = re.sub(old_colors, new_colors, content)
content = content.replace('shape = RoundedCornerShape(8.dp)', 'shape = RoundedCornerShape(12.dp)')

with open(file_path, "w", encoding="utf-8") as f:
    f.write(content)
print("Updated input styles")

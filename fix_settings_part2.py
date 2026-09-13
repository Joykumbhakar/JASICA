import re

file_path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

# 1. Replace the icon in AI Model Engine
old_ai_icon = r'icon = \{ androidx\.compose\.foundation\.Image\(painter = androidx\.compose\.ui\.res\.painterResource\(id = R\.drawable\.robot\), contentDescription = null, modifier = Modifier\.size\(24\.dp\)\) \}'
new_ai_icon = r'icon = { Icon(painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_ai_waves), contentDescription = null, modifier = Modifier.size(24.dp), tint = Color.White) }'
content = re.sub(old_ai_icon, new_ai_icon, content)

# 2. Update the admin password input box colors
# Note: Since the admin password box is inside SettingsScreen, `darkModeInput` is available, but wait...
# is fieldBg available? Yes, fieldBg is defined at the top of SettingsScreen!
# Wait, the AppleDialog for the password is shown inside SettingsScreen but at the bottom.
# Let's check if fieldBg is accessible. If not, I'll redefine it locally or use hardcoded colors.
# Let's use `if (darkModeInput) Color(0xFF2C2C2E) else Color(0xFFF2F2F7)`

old_pass_colors = r"""colors = androidx\.compose\.material3\.OutlinedTextFieldDefaults\.colors\(
\s*focusedBorderColor = Color\(0xFF007AFF\),
\s*unfocusedBorderColor = Color\.Gray\.copy\(alpha = 0\.3f\),
\s*focusedContainerColor = Color\.Transparent,
\s*unfocusedContainerColor = Color\.Transparent
\s*\)"""

new_pass_colors = r"""colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF007AFF),
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = if (darkModeInput) Color(0xFF2C2C2E) else Color(0xFFF2F2F7),
                            unfocusedContainerColor = if (darkModeInput) Color(0xFF2C2C2E) else Color(0xFFF2F2F7),
                            cursorColor = Color(0xFF007AFF)
                        )"""
content = re.sub(old_pass_colors, new_pass_colors, content)

with open(file_path, "w", encoding="utf-8") as f:
    f.write(content)
print("Updated AI model icon and password box")

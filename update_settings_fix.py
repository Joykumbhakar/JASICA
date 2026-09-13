import re
path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

target = """                        AppleSettingsRow(
                            title = "Quick Access Widget",
                            subtitle = "Floating icon for instant device control anywhere",
                            icon = null,
                            customIcon = R.drawable.ic_ai_waves,
                            showDivider = false,
                            isDark = darkModeInput,
                            rightContent = {"""

replacement = """                        AppleSettingsRow(
                            title = "Quick Access Widget",
                            subtitle = "Floating icon for instant device control anywhere",
                            icon = { androidx.compose.foundation.Image(androidx.compose.ui.res.painterResource(id = R.drawable.ic_ai_waves), contentDescription = null, modifier = Modifier.size(24.dp)) },
                            showDivider = false,
                            isDark = darkModeInput,
                            control = {"""

if target in text:
    text = text.replace(target, replacement)
    with open(path, "w", encoding="utf-8") as f:
        f.write(text)
    print("Fixed AppleSettingsRow usage")
else:
    print("Could not find target")

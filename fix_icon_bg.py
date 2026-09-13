import re

path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

target = """                        AppleSettingsRow(
                            title = "Quick Access Widget",
                            subtitle = "Floating icon for instant device control anywhere",
                            icon = { androidx.compose.foundation.Image(androidx.compose.ui.res.painterResource(id = R.drawable.ic_ai_waves), contentDescription = null, modifier = Modifier.size(24.dp)) },
                            showDivider = false,"""

replacement = """                        AppleSettingsRow(
                            title = "Quick Access Widget",
                            subtitle = "Floating icon for instant device control anywhere",
                            icon = { Icon(painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_ai_waves), contentDescription = null, modifier = Modifier.size(22.dp), tint = Color.White) },
                            iconBgColor = Color(0xFF007AFF),
                            showDivider = false,"""

if target in text:
    text = text.replace(target, replacement)
    with open(path, "w", encoding="utf-8") as f:
        f.write(text)
    print("Fixed icon background")
else:
    print("Target not found")

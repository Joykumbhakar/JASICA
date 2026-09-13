import re
path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

target = """                // Danger & Reset Group
                Box {"""

replacement = """                // Updates Group
                Box {
                    AppleSettingsGroup(title = "Updates & Info", isDark = darkModeInput) {
                        AppleSettingsRow(
                            title = "Check For New Update",
                            subtitle = "See if a newer version of JASICA is available",
                            icon = { Icon(Icons.Rounded.SystemUpdate, contentDescription = null, modifier = Modifier.size(24.dp), tint = Color.White) },
                            iconBgColor = Color(0xFF34C759),
                            showDivider = true,
                            isDark = darkModeInput,
                            onClick = {
                                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("https://jasicaai.vercel.app/updates"))
                                context.startActivity(intent)
                            }
                        )
                        AppleSettingsRow(
                            title = "Current Version Release Notes",
                            subtitle = "What's new in v1.2.13.09.2026-beta",
                            icon = { Icon(Icons.Rounded.Article, contentDescription = null, modifier = Modifier.size(24.dp), tint = Color.White) },
                            iconBgColor = Color(0xFF5856D6),
                            showDivider = false,
                            isDark = darkModeInput,
                            onClick = {
                                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("https://jasicaai.vercel.app/release-notes"))
                                context.startActivity(intent)
                            }
                        )
                    }
                }
                
                // Danger & Reset Group
                Box {"""

if target in text:
    text = text.replace(target, replacement)
    with open(path, "w", encoding="utf-8") as f:
        f.write(text)
    print("Added update settings")
else:
    print("Target not found")

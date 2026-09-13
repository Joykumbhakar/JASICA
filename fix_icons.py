import re

file_path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

# 1. Update main menu items
old_menu = r"""Column \{
\s*AppleMenuItem\(icon = Icons\.Rounded\.Home, text = "Manual Controls", onClick = \{ showMenu = false; onManualControlsTap\(\) \}\)
\s*androidx\.compose\.material3\.HorizontalDivider\(color = Color\.Black\.copy\(alpha=0\.1f\), thickness = 0\.5\.dp, modifier = Modifier\.padding\(start = 44\.dp\)\)
\s*AppleMenuItem\(icon = Icons\.Rounded\.History, text = "Chat History", onClick = \{ showMenu = false; onHistoryTap\(\) \}\)
\s*androidx\.compose\.material3\.HorizontalDivider\(color = Color\.Black\.copy\(alpha=0\.1f\), thickness = 0\.5\.dp, modifier = Modifier\.padding\(start = 44\.dp\)\)
\s*AppleMenuItemImage\(painter = androidx\.compose\.ui\.res\.painterResource\(id = R\.drawable\.arduino_ide\), text = "Arduino Code", onClick = \{ showMenu = false; onArduinoCodeTap\(\) \}\)
\s*androidx\.compose\.material3\.HorizontalDivider\(color = Color\.Black\.copy\(alpha=0\.1f\), thickness = 0\.5\.dp, modifier = Modifier\.padding\(start = 44\.dp\)\)
\s*AppleMenuItem\(icon = Icons\.Rounded\.Settings, text = "Settings", onClick = \{ showMenu = false; onSettingsTap\(\) \}\)
\s*\}"""

new_menu = """Column {
                    AppleMenuItemImage(painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_home_custom), text = "Manual Controls", onClick = { showMenu = false; onManualControlsTap() })
                    androidx.compose.material3.HorizontalDivider(color = Color.Black.copy(alpha=0.1f), thickness = 0.5.dp, modifier = Modifier.padding(start = 44.dp))
                    AppleMenuItemImage(painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_messages_custom), text = "Chat History", onClick = { showMenu = false; onHistoryTap() })
                    androidx.compose.material3.HorizontalDivider(color = Color.Black.copy(alpha=0.1f), thickness = 0.5.dp, modifier = Modifier.padding(start = 44.dp))
                    AppleMenuItemImage(painter = androidx.compose.ui.res.painterResource(id = R.drawable.arduino_ide), text = "Arduino Code", onClick = { showMenu = false; onArduinoCodeTap() })
                    androidx.compose.material3.HorizontalDivider(color = Color.Black.copy(alpha=0.1f), thickness = 0.5.dp, modifier = Modifier.padding(start = 44.dp))
                    AppleMenuItemImage(painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_settings_custom), text = "Settings", onClick = { showMenu = false; onSettingsTap() })
                }"""
content = re.sub(old_menu, new_menu, content)

# 2. Update API Key setting row
old_api = r"""AppleSettingsRow\(
\s*title = "Use Portfolio API Key",
\s*subtitle = "Automatically load dynamic API keys from cloud",
\s*icon = \{ Icon\(Icons\.Rounded\.VpnKey, contentDescription = null, modifier = Modifier\.size\(24\.dp\), tint = Color\.White\) \},
\s*iconBgColor = Color\(0xFFAF52DE\),"""

new_api = """AppleSettingsRow(
                            title = "Use Portfolio API Key",
                            subtitle = "Automatically load dynamic API keys from cloud",
                            icon = { Image(painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_maclaps), contentDescription = null, modifier = Modifier.size(24.dp)) },
                            iconBgColor = Color(0xFFAF52DE),"""
content = re.sub(old_api, new_api, content)

with open(file_path, "w", encoding="utf-8") as f:
    f.write(content)

# Update NotificationHelper.kt
notif_path = r"E:\Controller\app\src\main\java\com\bristi\controller\NotificationHelper.kt"
with open(notif_path, "r", encoding="utf-8") as f:
    notif_content = f.read()
notif_content = notif_content.replace("R.drawable.ic_launcher_foreground", "R.drawable.ic_ai_waves")
with open(notif_path, "w", encoding="utf-8") as f:
    f.write(notif_content)

print("Updated icons in MainActivity and NotificationHelper")

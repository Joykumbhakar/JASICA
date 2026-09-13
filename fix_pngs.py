import re

file_path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

# 1. Update Bluetooth icon to Image (no tint)
old_bt = r"""Icon\(
\s*painter = painterResource\(id = R\.drawable\.bluetooth_icon\),
\s*contentDescription = if \(isBtConnected\) "Bluetooth Connected" else "Bluetooth Disconnected",
\s*tint = if \(isBtConnected\) JasicaWhite else JasicaWhite\.copy\(alpha = 0\.4f\)
\s*\)"""

new_bt = """Image(
                            painter = painterResource(id = R.drawable.bluetooth_icon),
                            contentDescription = if (isBtConnected) "Bluetooth Connected" else "Bluetooth Disconnected"
                        )"""
content = re.sub(old_bt, new_bt, content)

# 2. Update API Key setting row (no background)
old_api = r"""AppleSettingsRow\(
\s*title = "Use Portfolio API Key",
\s*subtitle = "Automatically load dynamic API keys from cloud",
\s*icon = \{ Image\(painter = androidx\.compose\.ui\.res\.painterResource\(id = R\.drawable\.ic_maclaps\), contentDescription = null, modifier = Modifier\.size\(24\.dp\)\) \},
\s*iconBgColor = Color\(0xFFAF52DE\),"""

new_api = """AppleSettingsRow(
                            title = "Use Portfolio API Key",
                            subtitle = "Automatically load dynamic API keys from cloud",
                            icon = { Image(painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_maclaps), contentDescription = null, modifier = Modifier.size(28.dp)) },
                            iconBgColor = Color.Transparent,"""
content = re.sub(old_api, new_api, content)

with open(file_path, "w", encoding="utf-8") as f:
    f.write(content)

print("Removed tint and backgrounds")

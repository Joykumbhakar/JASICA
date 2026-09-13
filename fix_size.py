import re

file_path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

# 1. Update AppleSettingsRow to accept iconSize
old_row = r"""fun AppleSettingsRow\(
\s*title: String,
\s*subtitle: String\? = null,
\s*icon: @Composable \(\) -> Unit,
\s*iconBgColor: Color,
\s*showDivider: Boolean = true,
\s*isDark: Boolean,
\s*control: @Composable \(\) -> Unit
\) \{
\s*Row\(
\s*modifier = Modifier
\s*\.fillMaxWidth\(\)
\s*\.padding\(horizontal = 16\.dp, vertical = 12\.dp\),
\s*verticalAlignment = Alignment\.CenterVertically
\s*\) \{
\s*Box\(
\s*modifier = Modifier
\s*\.size\(28\.dp\)
\s*\.clip\(RoundedCornerShape\(6\.dp\)\)
\s*\.background\(iconBgColor\),
\s*contentAlignment = Alignment\.Center
\s*\) \{"""

new_row = """fun AppleSettingsRow(
    title: String,
    subtitle: String? = null,
    icon: @Composable () -> Unit,
    iconBgColor: Color,
    showDivider: Boolean = true,
    isDark: Boolean,
    iconSize: androidx.compose.ui.unit.Dp = 28.dp,
    control: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(iconSize)
                .clip(RoundedCornerShape(6.dp))
                .background(iconBgColor),
            contentAlignment = Alignment.Center
        ) {"""

content = re.sub(old_row, new_row, content)

# 2. Update the Use Portfolio API Key call to pass iconSize = 30.dp
old_api = r"""AppleSettingsRow\(
\s*title = "Use Portfolio API Key",
\s*subtitle = "Automatically load dynamic API keys from cloud",
\s*icon = \{ Image\(painter = androidx\.compose\.ui\.res\.painterResource\(id = R\.drawable\.ic_maclaps\), contentDescription = null, modifier = Modifier\.size\(28\.dp\)\) \},
\s*iconBgColor = Color\.Transparent,
\s*showDivider = !adminKeyInput,
\s*isDark = darkModeInput,
\s*control = \{"""

new_api = """AppleSettingsRow(
                            title = "Use Portfolio API Key",
                            subtitle = "Automatically load dynamic API keys from cloud",
                            icon = { Image(painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_maclaps), contentDescription = null, modifier = Modifier.size(30.dp)) },
                            iconBgColor = Color.Transparent,
                            showDivider = !adminKeyInput,
                            isDark = darkModeInput,
                            iconSize = 30.dp,
                            control = {"""
content = re.sub(old_api, new_api, content)

with open(file_path, "w", encoding="utf-8") as f:
    f.write(content)

print("Updated maclaps size")

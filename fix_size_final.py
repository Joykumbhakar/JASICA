import re

file_path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

# 1. Update AppleSettingsRow definition
old_row = r"""fun AppleSettingsRow\(
\s*title: String,
\s*subtitle: String\? = null,
\s*icon: \(@Composable \(\) -> Unit\)\? = null,
\s*iconBgColor: Color = Color\.Transparent,
\s*showDivider: Boolean = true,
\s*isDark: Boolean = false,
\s*onClick: \(\(\) -> Unit\)\? = null,
\s*control: \(@Composable \(\) -> Unit\)\? = null
\) \{
\s*val titleColor = if \(isDark\) Color\.White else Color\.Black
\s*val subColor = if \(isDark\) Color\.White\.copy\(alpha = 0\.5f\) else Color\(0xFF8E8E93\)
\s*val dividerColor = if \(isDark\) Color\(0xFF38383A\) else Color\(0xFFE5E5EA\)
\s*Row\(
\s*modifier = Modifier
\s*\.fillMaxWidth\(\)
\s*\.padding\(horizontal = 16\.dp, vertical = 12\.dp\)
\s*\.then\(if \(onClick != null\) Modifier\.pointerInput\(Unit\) \{ detectTapGestures \{ onClick\(\) \} \} else Modifier\),
\s*verticalAlignment = Alignment\.CenterVertically
\s*\) \{
\s*if \(icon != null\) \{
\s*Box\(
\s*modifier = Modifier
\s*\.size\(28\.dp\)"""

new_row = """fun AppleSettingsRow(
    title: String,
    subtitle: String? = null,
    icon: (@Composable () -> Unit)? = null,
    iconBgColor: Color = Color.Transparent,
    iconSize: androidx.compose.ui.unit.Dp = 28.dp,
    showDivider: Boolean = true,
    isDark: Boolean = false,
    onClick: (() -> Unit)? = null,
    control: (@Composable () -> Unit)? = null
) {
    val titleColor = if (isDark) Color.White else Color.Black
    val subColor = if (isDark) Color.White.copy(alpha = 0.5f) else Color(0xFF8E8E93)
    val dividerColor = if (isDark) Color(0xFF38383A) else Color(0xFFE5E5EA)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .then(if (onClick != null) Modifier.pointerInput(Unit) { detectTapGestures { onClick() } } else Modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Box(
                modifier = Modifier
                    .size(iconSize)"""

content = re.sub(old_row, new_row, content)

# 2. Update the API Key usage
old_api = r"""AppleSettingsRow\(
\s*title = "Use Portfolio API Key",
\s*subtitle = "Automatically load dynamic API keys from cloud",
\s*icon = \{ Image\(painter = androidx\.compose\.ui\.res\.painterResource\(id = R\.drawable\.ic_maclaps\), contentDescription = null, modifier = Modifier\.size\(24\.dp\)\) \},
\s*iconBgColor = Color\.Transparent,
\s*showDivider = !adminKeyInput,
\s*isDark = darkModeInput,
\s*control = \{"""

new_api = """AppleSettingsRow(
                            title = "Use Portfolio API Key",
                            subtitle = "Automatically load dynamic API keys from cloud",
                            icon = { Image(painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_maclaps), contentDescription = null, modifier = Modifier.size(30.dp)) },
                            iconBgColor = Color.Transparent,
                            iconSize = 30.dp,
                            showDivider = !adminKeyInput,
                            isDark = darkModeInput,
                            control = {"""

# Wait, the previous file reset means the Image size is 28.dp inside old_api?
# Let's check what it was before:
# icon = { Image(painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_maclaps), contentDescription = null, modifier = Modifier.size(28.dp)) },
# In fix_pngs.py it changed size(24.dp) to size(28.dp)? No!
# Ah! In fix_pngs.py: modifier = Modifier.size(28.dp)
content = re.sub(
    r"""icon = \{ Image\(painter = androidx\.compose\.ui\.res\.painterResource\(id = R\.drawable\.ic_maclaps\), contentDescription = null, modifier = Modifier\.size\(28\.dp\)\) \},\s*iconBgColor = Color\.Transparent,\s*showDivider = !adminKeyInput,\s*isDark = darkModeInput,""",
    """icon = { Image(painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_maclaps), contentDescription = null, modifier = Modifier.size(30.dp)) },\n                            iconBgColor = Color.Transparent,\n                            iconSize = 30.dp,\n                            showDivider = !adminKeyInput,\n                            isDark = darkModeInput,""",
    content
)

with open(file_path, "w", encoding="utf-8") as f:
    f.write(content)
print("Updated perfectly")

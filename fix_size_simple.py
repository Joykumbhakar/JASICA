import re

file_path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

# Replace the specific modifier inside AppleSettingsRow that constraints the icon to 28.dp
# We can just change Modifier.size(28.dp) inside the Box where `icon()` is called to size(30.dp) for ALL settings rows, or pass a parameter.
# Since passing a parameter is safer, let's update AppleSettingsRow properly.

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
\s*(.*?)
\s*verticalAlignment = Alignment\.CenterVertically
\s*\) \{
\s*if \(icon != null\) \{
\s*Box\(
\s*modifier = Modifier
\s*\.size\(28\.dp\)"""

# Wait, let's just find the exact text in AppleSettingsRow and replace .size(28.dp) with .size(30.dp) globally for AppleSettingsRow.
# A 2px change for all setting icons won't hurt, but if it does, let's add `iconSize: androidx.compose.ui.unit.Dp = 28.dp`

# Using a robust replace:
content = content.replace(
    "iconBgColor: Color = Color.Transparent,",
    "iconBgColor: Color = Color.Transparent,\n    iconSize: androidx.compose.ui.unit.Dp = 28.dp,"
)
content = content.replace(
    """if (icon != null) {
            Box(
                modifier = Modifier
                    .size(28.dp)""",
    """if (icon != null) {
            Box(
                modifier = Modifier
                    .size(iconSize)"""
)

# Also update the call for "Use Portfolio API Key"
# We already changed the Image to .size(30.dp), but need to pass iconSize to AppleSettingsRow
content = content.replace(
    """AppleSettingsRow(
                            title = "Use Portfolio API Key",
                            subtitle = "Automatically load dynamic API keys from cloud",
                            icon = { Image(painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_maclaps), contentDescription = null, modifier = Modifier.size(30.dp)) },
                            iconBgColor = Color.Transparent,
                            showDivider = !adminKeyInput,
                            isDark = darkModeInput,""",
    """AppleSettingsRow(
                            title = "Use Portfolio API Key",
                            subtitle = "Automatically load dynamic API keys from cloud",
                            icon = { Image(painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_maclaps), contentDescription = null, modifier = Modifier.size(30.dp)) },
                            iconBgColor = Color.Transparent,
                            showDivider = !adminKeyInput,
                            isDark = darkModeInput,
                            iconSize = 30.dp,"""
)

with open(file_path, "w", encoding="utf-8") as f:
    f.write(content)
print("done")

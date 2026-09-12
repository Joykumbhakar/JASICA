import re

with open(r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt", "r", encoding="utf-8") as f:
    text = f.read()

# For DropdownMenu, we have: Icon(painterResource(id = R.drawable.fluentui_system_icons_home), contentDescription = null, tint = JasicaWhite)
text = re.sub(
    r'Icon\(painterResource\(id = R\.drawable\.fluentui_system_icons_([^)]+)\),\s*contentDescription = null,\s*tint = JasicaWhite\)',
    r'Image(painterResource(id = R.drawable.fluentui_system_icons_\1), contentDescription = null, modifier = Modifier.size(24.dp))',
    text
)

# For AppleSettingsRow icons
text = re.sub(
    r'Icon\(painterResource\(id = R\.drawable\.fluentui_system_icons_([^)]+)\),\s*contentDescription = null,\s*tint = Color\.White\)',
    r'Image(painterResource(id = R.drawable.fluentui_system_icons_\1), contentDescription = null, modifier = Modifier.size(24.dp))',
    text
)

# For icons that already had a modifier
text = re.sub(
    r'Icon\(painterResource\(id = R\.drawable\.fluentui_system_icons_([^)]+)\),\s*contentDescription = null,\s*tint = [^,]+,\s*modifier = Modifier\.size\(([^)]+)\)\)',
    r'Image(painterResource(id = R.drawable.fluentui_system_icons_\1), contentDescription = null, modifier = Modifier.size(\2))',
    text
)

# For the complex tint in DeviceListItem
text = re.sub(
    r'Icon\(\s*painter = painterResource\(id = R\.drawable\.fluentui_system_icons_([^)]+)\),\s*contentDescription = null,\s*tint = [^,]+,\s*modifier = Modifier\.size\(([^)]+)\)\s*\)',
    r'Image(painter = painterResource(id = R.drawable.fluentui_system_icons_\1), contentDescription = null, modifier = Modifier.size(\2))',
    text
)

with open(r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt", "w", encoding="utf-8") as f:
    f.write(text)

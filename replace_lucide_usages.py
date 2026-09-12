import re

with open(r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt", "r", encoding="utf-8") as f:
    text = f.read()

# All ON / All OFF buttons
text = text.replace('LucideZap(tint = Color.White, modifier = Modifier.size(16.dp))',
                    'Icon(painterResource(id = R.drawable.fluentui_system_icons_lightbulb), contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))')

text = text.replace('LucideRotateCcw(tint = Color.White, modifier = Modifier.size(16.dp))',
                    'Icon(painterResource(id = R.drawable.fluentui_system_icons_dismiss_circle), contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))')

# DeviceListItem
text = text.replace('LucideCpu(\n                        tint = if (isChecked) Color.White else (if (isDark) Color.White else Color(0xFF007AFF)),\n                        modifier = Modifier.size(20.dp)\n                    )',
                    'Icon(\n                        painter = painterResource(id = R.drawable.fluentui_system_icons_lightbulb),\n                        contentDescription = null,\n                        tint = if (isChecked) Color.White else (if (isDark) Color.White else Color(0xFF007AFF)),\n                        modifier = Modifier.size(20.dp)\n                    )')

with open(r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt", "w", encoding="utf-8") as f:
    f.write(text)

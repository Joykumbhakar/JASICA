import re
with open(r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt", "r", encoding="utf-8") as f:
    text = f.read()

# 1. Replace dropdown menu icons with Fluent Icons
dropdown_replacements = [
    (r'Image\(painter = painterResource\(id = R\.drawable\.ic_home_mac\), contentDescription = null, modifier = Modifier\.size\(24\.dp\)\)', 
     r'Icon(painterResource(id = R.drawable.fluentui_system_icons_home), contentDescription = null, tint = JasicaWhite)'),
    
    (r'Image\(painter = painterResource\(id = R\.drawable\.ic_terminal_mac\), contentDescription = null, modifier = Modifier\.size\(24\.dp\)\)', 
     r'Icon(painterResource(id = R.drawable.fluentui_system_icons_history), contentDescription = null, tint = JasicaWhite)'),
    
    (r'Image\(painter = painterResource\(id = R\.drawable\.ic_apple_mac\), contentDescription = null, modifier = Modifier\.size\(24\.dp\)\)', 
     r'Icon(painterResource(id = R.drawable.fluentui_system_icons_code), contentDescription = null, tint = JasicaWhite)'),
    
    (r'Image\(painter = painterResource\(id = R\.drawable\.ic_settings_mac\), contentDescription = null, modifier = Modifier\.size\(24\.dp\)\)', 
     r'Icon(painterResource(id = R.drawable.fluentui_system_icons_settings), contentDescription = null, tint = JasicaWhite)'),
]

for old, new in dropdown_replacements:
    text = re.sub(old, new, text)

# 2. Replace AppleSettingsRow icons
settings_icons = {
    'LucideSliders': 'Icon(painterResource(id = R.drawable.fluentui_system_icons_options), contentDescription = null, tint = Color.White)',
    'LucideMic': 'Icon(painterResource(id = R.drawable.fluentui_system_icons_mic), contentDescription = null, tint = Color.White)',
    'LucideClock': 'Icon(painterResource(id = R.drawable.fluentui_system_icons_history), contentDescription = null, tint = Color.White)',
    'LucideZap': 'Icon(painterResource(id = R.drawable.fluentui_system_icons_wifi), contentDescription = null, tint = Color.White)',
    'LucideCpu': 'Icon(painterResource(id = R.drawable.fluentui_system_icons_bot), contentDescription = null, tint = Color.White)',
    'LucideKey': 'Icon(painterResource(id = R.drawable.fluentui_system_icons_person_key), contentDescription = null, tint = Color.White)',
    'LucideDroplet': 'Icon(painterResource(id = R.drawable.fluentui_system_icons_clock_alarm), contentDescription = null, tint = Color.White)',
    'LucideRotateCcw': 'Icon(painterResource(id = R.drawable.fluentui_system_icons_arrow_clockwise_dashes), contentDescription = null, tint = Color.White)'
}

for lucide, fluent in settings_icons.items():
    text = re.sub(r'\{\s*' + lucide + r'\(tint = Color\.White\)\s*\}', r'{ ' + fluent + r' }', text)
    # also handle the water reminder slider clock
    text = re.sub(r'\{\s*' + lucide + r'\(tint = textSecondary, modifier = Modifier\.size\(16\.dp\)\)\s*\}', r'{ Icon(painterResource(id = R.drawable.fluentui_system_icons_clock), contentDescription = null, tint = textSecondary, modifier = Modifier.size(16.dp)) }', text)
    text = re.sub(r'LucideClock\(tint = textSecondary, modifier = Modifier\.size\(16\.dp\)\)', r'Icon(painterResource(id = R.drawable.fluentui_system_icons_clock), contentDescription = null, tint = textSecondary, modifier = Modifier.size(16.dp))', text)

with open(r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt", "w", encoding="utf-8") as f:
    f.write(text)

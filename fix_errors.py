import re

file_path = r'e:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# Fix \s+ escape sequences
content = content.replace('split("\s+".toRegex())', 'split("\\\\s+".toRegex())')

# Fix SettingsDialog call
content = content.replace(
    'SettingsDialog(currentApiKey, currentModel, isWakeWordMode, onDismissSettings, onSaveSettings)',
    'SettingsDialog(currentApiKey, currentModel, isWakeWordMode, sharedPrefs, onDismissSettings, onSaveSettings)'
)

# Fix JasicaScreen call in onCreate
content = content.replace(
'''JasicaScreen(
                    appState            = appState.value,''',
'''JasicaScreen(
                    sharedPrefs         = sharedPrefs,
                    appState            = appState.value,'''
)
# Just in case spacing is weird:
content = re.sub(
    r'JasicaScreen\(\s*appState\s*=\s*appState\.value,',
    r'JasicaScreen(\n                    sharedPrefs         = sharedPrefs,\n                    appState            = appState.value,',
    content
)

# Fix JasicaScreenIdlePreview
old_preview = '''JasicaScreen(
            appState = AppState.IDLE,'''
new_preview = '''JasicaScreen(
            sharedPrefs = LocalContext.current.getSharedPreferences("JasicaSettings", Context.MODE_PRIVATE),
            appState = AppState.IDLE,'''
content = content.replace(old_preview, new_preview)
# fallback for preview
content = re.sub(
    r'JasicaScreen\(\s*appState = AppState\.IDLE,',
    r'JasicaScreen(\n            sharedPrefs = androidx.compose.ui.platform.LocalContext.current.getSharedPreferences("JasicaSettings", android.content.Context.MODE_PRIVATE),\n            appState = AppState.IDLE,',
    content
)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

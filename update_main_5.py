import re

file_path = r'e:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# Update JasicaScreen parameters in declaration
old_jasica_screen_decl = '''fun JasicaScreen(
    appState            : AppState,'''
new_jasica_screen_decl = '''fun JasicaScreen(
    sharedPrefs         : SharedPreferences,
    appState            : AppState,'''
content = content.replace(old_jasica_screen_decl, new_jasica_screen_decl)

# Update SettingsDialog call inside JasicaScreen
old_settings_call = '''            if (showSettings) {
                SettingsDialog(
                    currentApiKey = currentApiKey,
                    currentModel = currentModel,
                    isWakeWordMode = isWakeWordMode,
                    onDismiss = onDismissSettings,
                    onSave = onSaveSettings
                )
            }'''
new_settings_call = '''            if (showSettings) {
                SettingsDialog(
                    currentApiKey = currentApiKey,
                    currentModel = currentModel,
                    isWakeWordMode = isWakeWordMode,
                    sharedPrefs = sharedPrefs,
                    onDismiss = onDismissSettings,
                    onSave = onSaveSettings
                )
            }'''
content = content.replace(old_settings_call, new_settings_call)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

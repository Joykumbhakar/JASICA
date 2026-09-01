import re

file_path = r'e:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# Extract SettingsDialog
pattern = r'@OptIn\(ExperimentalMaterial3Api::class\)\n@Composable\nfun SettingsDialog\([\s\S]*?            \}\n        \}\n    \}\n\}'
match = re.search(pattern, content)
if match:
    with open('settings_dialog_extracted.txt', 'w', encoding='utf-8') as out:
        out.write(match.group(0))
    print("Extracted SettingsDialog to settings_dialog_extracted.txt")
else:
    print("SettingsDialog not found.")

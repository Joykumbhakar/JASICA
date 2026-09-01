import re

file_path = r'e:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

if 'import androidx.compose.ui.platform.LocalContext' not in content:
    content = content.replace('import androidx.compose.ui.platform.LocalHapticFeedback', 'import androidx.compose.ui.platform.LocalHapticFeedback\nimport androidx.compose.ui.platform.LocalContext')
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)
        print('Added LocalContext import')

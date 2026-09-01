import re
file_path = r'e:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

if 'import androidx.compose.foundation.layout.statusBarsPadding' not in content:
    content = content.replace('import androidx.compose.foundation.layout.*', 'import androidx.compose.foundation.layout.*\nimport androidx.compose.foundation.layout.statusBarsPadding')
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)
        print("Added statusBarsPadding import")
else:
    print("statusBarsPadding import already exists")

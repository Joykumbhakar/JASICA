import re

file_path = r'e:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

imports_to_add = [
    'import androidx.compose.material3.TabRow',
    'import androidx.compose.material3.Tab',
    'import androidx.compose.material3.TabRowDefaults',
    'import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset',
    'import androidx.compose.ui.Modifier' # usually there, but let's be sure
]

changed = False
for imp in imports_to_add:
    if imp not in content:
        content = content.replace('import androidx.compose.material3.*', f'import androidx.compose.material3.*\n{imp}')
        changed = True

if changed:
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)
        print('Added imports')

import re
file_path = r'e:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

content = content.replace('@OptIn(ExperimentalMaterial3Api::class)\n@OptIn(ExperimentalMaterial3Api::class)', '@OptIn(ExperimentalMaterial3Api::class)')
content = content.replace('.background(JasicaDarkGray)', '.background(androidx.compose.ui.graphics.Color(0xFF121212))')

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

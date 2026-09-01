import re
file_path = r'e:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# Fix DEV__
content = content.replace('DEV__', 'DEV_${dev.id}_')

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)
print("Fixed DEV__")

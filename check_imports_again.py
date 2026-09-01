import re
file_path = r'e:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    lines = f.readlines()
for line in lines:
    if 'lottie' in line.lower() or 'import android.media' in line.lower():
        print(line.strip())

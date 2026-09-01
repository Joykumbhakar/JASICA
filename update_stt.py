import re

file_path = r'e:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

stt_setup_pattern = r'''putExtra\(RecognizerIntent\.EXTRA_LANGUAGE_PREFERENCE, "en-IN"\)'''
stt_setup_replacement = '''putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en-IN")
                putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, !isNetworkAvailable())'''
content = content.replace(stt_setup_pattern, stt_setup_replacement)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)
print("Updated MainActivity STT configs")

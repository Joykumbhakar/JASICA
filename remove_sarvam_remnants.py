import re
file_path = r'e:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

content = content.replace('if (::sarvamTts.isInitialized)         if (::speechRecognizer.isInitialized) speechRecognizer.destroy()', 'if (::speechRecognizer.isInitialized) speechRecognizer.destroy()')
content = content.replace('if (::sarvamTts.isInitialized)     if (::speechRecognizer.isInitialized)', 'if (::speechRecognizer.isInitialized)')
content = re.sub(r'if \(::sarvamTts\.isInitialized && sarvamTts\.isSpeaking\) \{\s*sarvamTts\.stop\(\)\s*\}', '', content)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)
print("Removed remaining sarvamTts references")

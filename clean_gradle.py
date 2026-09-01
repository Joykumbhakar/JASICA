import re
file_path = r'e:\Controller\app\build.gradle.kts'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

content = re.sub(r'buildConfigField\("String", "SARVAM_API_KEY", ".*?"\)\n', '', content)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)
print("Cleaned build.gradle.kts")

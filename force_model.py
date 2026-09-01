import re
file_path = r'e:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

content = content.replace('selectedAiModel.value = sharedPrefs.getString("AI_MODEL", AiModelsList[0]) ?: AiModelsList[0]', 'selectedAiModel.value = "gemini-2.5-flash"')
content = content.replace('val AiModelsList = listOf("gemini-2.5-flash", "gemini-3.1-flash", "gemini-3.5-flash")', 'val AiModelsList = listOf("gemini-2.5-flash")')

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)
print("Forced gemini-2.5-flash")

import re

file_path = r'e:\Controller\app\src\main\java\com\bristi\controller\SarvamTtsEngine.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# Change timeouts to be aggressive to detect "slow" internet
content = content.replace('.connectTimeout(10, TimeUnit.SECONDS)', '.connectTimeout(3, TimeUnit.SECONDS)')
content = content.replace('.readTimeout(30, TimeUnit.SECONDS)', '.readTimeout(5, TimeUnit.SECONDS)')
content = content.replace('.writeTimeout(10, TimeUnit.SECONDS)', '.writeTimeout(3, TimeUnit.SECONDS)')

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)
print("Updated SarvamTtsEngine timeouts")

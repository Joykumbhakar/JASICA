import re
file_path = r'e:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

content = content.replace(
    'private const val PORTFOLIO_API_URL = "https://your-portfolio.com/api/get-gemini-key"',
    'private const val PORTFOLIO_API_URL = "https://joykumbhakar.vercel.app/api/get-gemini-key"'
)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)
print("Updated PORTFOLIO_API_URL")

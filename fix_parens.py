import re
with open(r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt", "r", encoding="utf-8") as f:
    text = f.read()

# Fix the multiple `)` followed by `}` in the control lambdas
text = re.sub(r'\)\s*\n\s*\)\s*\n(\s*\})', r')\n\1', text)

with open(r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt", "w", encoding="utf-8") as f:
    f.write(text)

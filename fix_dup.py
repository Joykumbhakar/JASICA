file_path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

content = content.replace("@Composable\n@Composable\nfun AppleMenuItemImage", "@Composable\nfun AppleMenuItemImage")

with open(file_path, "w", encoding="utf-8") as f:
    f.write(content)
print("Removed duplicate @Composable")

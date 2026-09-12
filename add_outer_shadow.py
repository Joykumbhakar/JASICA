import re

file_path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

old_mod = """    Box(
        modifier = modifier
            .size(30.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(brush)
            // Add top inner shadow (white 0.3)
            .border("""

new_mod = """    Box(
        modifier = modifier
            .size(30.dp)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(8.dp),
                ambientColor = Color.Black.copy(alpha = 0.12f),
                spotColor = Color.Black.copy(alpha = 0.12f)
            )
            .background(brush)
            // Add top inner shadow (white 0.3)
            .border("""

if old_mod in content:
    content = content.replace(old_mod, new_mod)
    with open(file_path, "w", encoding="utf-8") as f:
        f.write(content)
    print("Added outer shadow")
else:
    print("Not found")

import re
path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

conflict = """<<<<<<< HEAD
                                modifier = Modifier.fillMaxWidth().heightIn(max = 260.dp).padding(horizontal = 16.dp)
=======
                                modifier = Modifier.fillMaxWidth().height(260.dp).clip(RoundedCornerShape(16.dp))
>>>>>>> 8b95c9e"""

text = text.replace(conflict, "                                modifier = Modifier.fillMaxWidth().height(260.dp).clip(RoundedCornerShape(16.dp))")

with open(path, "w", encoding="utf-8") as f:
    f.write(text)
print("Conflict fixed")

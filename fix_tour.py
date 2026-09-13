import re
path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

old_code = """                        if (info.image != null) {
                            Image(
                                painterResource(info.image),
                                contentDescription = null,
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                modifier = Modifier.size(220.dp).shadow(24.dp, RoundedCornerShape(40.dp)).clip(RoundedCornerShape(40.dp))
                            )"""

new_code = """                        if (info.image != null) {
                            Image(
                                painterResource(info.image),
                                contentDescription = null,
                                contentScale = androidx.compose.ui.layout.ContentScale.Fit,
                                modifier = Modifier.fillMaxWidth().height(260.dp).clip(RoundedCornerShape(16.dp))
                            )"""

text = text.replace(old_code, new_code)
with open(path, "w", encoding="utf-8") as f:
    f.write(text)
print("Updated tour image modifier")

import re

file_path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

old_code = r"""Image\(
\s*painterResource\(info\.image\),
\s*contentDescription = null,
\s*contentScale = androidx\.compose\.ui\.layout\.ContentScale\.Crop,
\s*modifier = Modifier\.size\(220\.dp\)\.shadow\(24\.dp, RoundedCornerShape\(40\.dp\)\)\.clip\(RoundedCornerShape\(40\.dp\)\)
\s*\)"""

new_code = """Image(
                                painterResource(info.image),
                                contentDescription = null,
                                contentScale = androidx.compose.ui.layout.ContentScale.Fit,
                                modifier = Modifier.fillMaxWidth().heightIn(max = 260.dp).padding(horizontal = 16.dp)
                            )"""

content = re.sub(old_code, new_code, content)

with open(file_path, "w", encoding="utf-8") as f:
    f.write(content)

print("Updated tour images code")

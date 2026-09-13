file_path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

content = content.replace(
    "iconBgColor: Color = Color.Transparent,",
    "iconBgColor: Color = Color.Transparent,\n    iconSize: androidx.compose.ui.unit.Dp = 28.dp,"
)
content = content.replace(
    """if (icon != null) {
            Box(
                modifier = Modifier
                    .size(28.dp)""",
    """if (icon != null) {
            Box(
                modifier = Modifier
                    .size(iconSize)"""
)

with open(file_path, "w", encoding="utf-8") as f:
    f.write(content)

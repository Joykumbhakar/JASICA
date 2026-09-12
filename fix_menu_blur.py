import re

file_path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

old_haze = """                    .clip(RoundedCornerShape(16.dp))
                    .then(
                        if (hazeState != null) Modifier.hazeEffect(
                            state = hazeState,
                            style = dev.chrisbanes.haze.HazeStyle(
                                blurRadius = 28.dp,
                                tint = dev.chrisbanes.haze.HazeTint(Color.White.copy(alpha=0.25f))
                            )
                        ) else Modifier.background(Color.White.copy(alpha = 0.95f))
                    )
                    .border(0.5.dp, Color.White.copy(alpha=0.6f), RoundedCornerShape(16.dp))"""

new_haze = """                    .clip(RoundedCornerShape(16.dp))
                    .then(
                        if (hazeState != null) Modifier.hazeEffect(
                            state = hazeState,
                            style = dev.chrisbanes.haze.HazeStyle(
                                blurRadius = 24.dp,
                                tint = dev.chrisbanes.haze.HazeTint(Color.White.copy(alpha=0.35f))
                            )
                        ) else Modifier
                    )
                    .background(Color.White.copy(alpha = if (hazeState != null) 0.25f else 0.95f))
                    .border(0.5.dp, Color.White.copy(alpha=0.6f), RoundedCornerShape(16.dp))"""

if old_haze in content:
    content = content.replace(old_haze, new_haze)
    with open(file_path, "w", encoding="utf-8") as f:
        f.write(content)
    print("Fixed")
else:
    print("Could not find the target string.")

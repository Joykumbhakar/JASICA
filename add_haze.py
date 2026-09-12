import re

with open(r"E:\Controller\app\build.gradle.kts", "r", encoding="utf-8") as f:
    text = f.read()

if "dev.chrisbanes.haze:haze:" not in text:
    text = text.replace('implementation(libs.androidx.compose.animation)', 'implementation(libs.androidx.compose.animation)\n    implementation("dev.chrisbanes.haze:haze:1.5.3")')

with open(r"E:\Controller\app\build.gradle.kts", "w", encoding="utf-8") as f:
    f.write(text)

with open(r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt", "r", encoding="utf-8") as f:
    text = f.read()

imports = """
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt
import dev.chrisbanes.haze.*
"""

if "import dev.chrisbanes.haze.*" not in text:
    text = text.replace("import androidx.compose.ui.Modifier", "import androidx.compose.ui.Modifier" + imports)

with open(r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt", "w", encoding="utf-8") as f:
    f.write(text)

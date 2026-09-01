import re
file_path = r'e:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    lines = f.readlines()

imports_to_add = """import android.media.MediaPlayer
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import androidx.compose.runtime.DisposableEffect
"""

for i, line in enumerate(lines):
    if line.startswith('import androidx.compose.runtime.*'):
        lines.insert(i+1, imports_to_add)
        break

with open(file_path, 'w', encoding='utf-8') as f:
    f.writelines(lines)
print("Added imports")

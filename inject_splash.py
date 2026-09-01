import re

file_path = r'e:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    lines = f.readlines()

# 1. Add imports
imports_to_add = """import android.media.MediaPlayer
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import androidx.compose.runtime.DisposableEffect
"""
for i, line in enumerate(lines):
    if line.startswith('import androidx.compose.runtime.Composable'):
        lines.insert(i+1, imports_to_add)
        break

# 2. Add AnimatedSplashScreen composable at the end
splash_code = """
@Composable
fun AnimatedSplashScreen(onFinished: () -> Unit) {
    val context = LocalContext.current
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("opening.json"))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1
    )

    LaunchedEffect(progress) {
        if (progress == 1f) {
            onFinished()
        }
    }

    DisposableEffect(Unit) {
        val mediaPlayer = MediaPlayer.create(context, R.raw.opening)
        mediaPlayer?.start()
        
        onDispose {
            try {
                if (mediaPlayer?.isPlaying == true) {
                    mediaPlayer.stop()
                }
                mediaPlayer?.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(androidx.compose.ui.graphics.Color(0xFF121212)),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(300.dp)
        )
    }
}
"""
lines.append(splash_code)

# 3. Wrap JasicaScreen in setContent
start = -1
for i, line in enumerate(lines):
    if 'setContent {' in line:
        start = i
        break

if start != -1:
    # Find JasicaScreen(
    js_start = -1
    for i in range(start, len(lines)):
        if 'JasicaScreen(' in lines[i]:
            js_start = i
            break
    
    if js_start != -1:
        lines.insert(js_start, "                var showSplash by remember { mutableStateOf(true) }\n                if (showSplash) {\n                    AnimatedSplashScreen(onFinished = { showSplash = false })\n                } else {\n")
        
        # Now find the closing parenthesis of JasicaScreen
        brace_count = 0
        found_open = False
        js_end = -1
        # Re-evaluate js_start since we inserted lines
        js_start += 1
        
        for i in range(js_start, len(lines)):
            line = lines[i]
            if '(' in line:
                brace_count += line.count('(')
                found_open = True
            if ')' in line:
                brace_count -= line.count(')')
            if found_open and brace_count == 0:
                js_end = i
                break
        
        if js_end != -1:
            lines.insert(js_end + 1, "                }\n")

with open(file_path, 'w', encoding='utf-8') as f:
    f.writelines(lines)
print("Injected AnimatedSplashScreen")

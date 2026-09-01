import re

file_path = r'e:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# Add imports
imports = """
import android.media.MediaPlayer
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import androidx.compose.runtime.DisposableEffect
"""

if 'import com.airbnb.lottie' not in content:
    content = content.replace('import androidx.compose.runtime.Composable', 'import androidx.compose.runtime.Composable' + imports)


# Insert AnimatedSplashScreen composable at the end of the file
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

if 'fun AnimatedSplashScreen' not in content:
    content = content + splash_code


# Modify setContent
old_set_content = """        setContent {
            JasicaTheme {
                JasicaScreen("""

new_set_content = """        setContent {
            JasicaTheme {
                var showSplash by remember { mutableStateOf(true) }
                if (showSplash) {
                    AnimatedSplashScreen(onFinished = { showSplash = false })
                } else {
                    JasicaScreen("""

content = content.replace(old_set_content, new_set_content)

# Fix indentation for the rest of JasicaScreen call
# Well, compose is robust against indentation so it's fine, but let's just make sure we close the else block
# Wait, JasicaScreen doesn't have a trailing brace for setContent immediately?
# setContent { JasicaTheme { JasicaScreen(...) } }

old_closing = """                        }
                    )
                }
            }
        }
    }"""
# Wait, we need to match the closing brace precisely. Let's just use regex or AST.

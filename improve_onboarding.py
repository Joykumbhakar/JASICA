import re

file_path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

# 1. Update the call site
old_call = "OnboardingScreen(onDismiss = onDismissOnboarding)"
new_call = "OnboardingScreen(hazeState = hazeState, onDismiss = onDismissOnboarding)"
content = content.replace(old_call, new_call)

# 2. Replace the OnboardingScreen composable
old_onboarding_pattern = r"fun OnboardingScreen\(onDismiss: \(\) -> Unit\) \{.*?(?=^// ---|^@Composable|void setup\(\))"
# Actually, it's safer to just split and replace based on the function signature.
start_idx = content.find("fun OnboardingScreen(onDismiss: () -> Unit) {")
if start_idx != -1:
    end_idx = content.find("void setup() {", start_idx)
    if end_idx != -1:
        old_composable = content[start_idx:end_idx]
        
        new_composable = """fun OnboardingScreen(hazeState: dev.chrisbanes.haze.HazeState? = null, onDismiss: () -> Unit) {
    val pages = listOf(
        OnboardingPageInfo("Welcome to Jasica", "Your intelligent voice assistant for complete digital and hardware control.", null, R.drawable.jasica),
        OnboardingPageInfo("Voice Commands", "Say a command or tap the mic to control your lights, PC, AC, and more natively.", R.drawable.fluentui_system_icons_mic, null),
        OnboardingPageInfo("Manual Override", "Access the quick-switch panel from the top right home icon to toggle hardware without speaking.", R.drawable.fluentui_system_icons_home, null),
        OnboardingPageInfo("Stay Connected", "Pair your Bluetooth smart hub via the top right icon to get started.", R.drawable.fluentui_system_icons_phone_laptop, null)
    )

    val pagerState = androidx.compose.foundation.pager.rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(
                if (hazeState != null) Modifier.hazeEffect(
                    state = hazeState,
                    style = dev.chrisbanes.haze.HazeStyle(
                        blurRadius = 40.dp,
                        tint = dev.chrisbanes.haze.HazeTint(Color.Black.copy(alpha = 0.4f))
                    )
                ) else Modifier
            )
            .background(if (hazeState != null) Color.Black.copy(alpha = 0.5f) else Color(0xFF0F0F14).copy(alpha = 0.98f))
            .clickable(enabled = false) {}
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.weight(1f))

            androidx.compose.foundation.pager.HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth().height(360.dp)
            ) { page ->
                val info = pages[page]
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val isCurrentPage = pagerState.currentPage == page
                    val scale by animateFloatAsState(if (isCurrentPage) 1f else 0.8f, tween(400, easing = FastOutSlowInEasing))
                    val alpha by animateFloatAsState(if (isCurrentPage) 1f else 0f, tween(400))

                    Box(modifier = Modifier.graphicsLayer { this.scaleX = scale; this.scaleY = scale; this.alpha = alpha }) {
                        if (info.image != null) {
                            Image(
                                painterResource(info.image),
                                contentDescription = null,
                                modifier = Modifier.size(160.dp).clip(CircleShape).shadow(12.dp, CircleShape)
                            )
                        } else if (info.iconRes != null) {
                            Box(
                                modifier = Modifier
                                    .size(160.dp)
                                    .shadow(elevation = 16.dp, shape = CircleShape, ambientColor = Color.Black.copy(alpha = 0.3f), spotColor = Color.Black.copy(alpha = 0.3f))
                                    .background(
                                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                            listOf(Color(0xFF44A6FF), Color(0xFF007AFF))
                                        ),
                                        shape = CircleShape
                                    )
                                    .border(1.dp, Color.White.copy(0.3f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painterResource(info.iconRes), 
                                    contentDescription = null, 
                                    modifier = Modifier.size(72.dp).graphicsLayer { shadowElevation = 4f }, 
                                    tint = Color.White
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(48.dp))
                    Text(
                        text = info.title,
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = InterFontFamily,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = info.subtitle,
                        color = Color.White.copy(alpha=0.75f),
                        fontSize = 17.sp,
                        textAlign = TextAlign.Center,
                        fontFamily = InterFontFamily,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        lineHeight = 24.sp
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            // Progress Dots
            Row(
                modifier = Modifier.padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                pages.indices.forEach { index ->
                    val isSelected = pagerState.currentPage == index
                    val color by animateColorAsState(if (isSelected) Color.White else Color.White.copy(alpha = 0.2f), tween(300))
                    val width by animateFloatAsState(if (isSelected) 24f else 8f, tween(300))
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .height(8.dp)
                            .width(width.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }

            // Action Buttons
            Button(
                onClick = {
                    if (pagerState.currentPage < pages.size - 1) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    } else {
                        onDismiss()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = if (pagerState.currentPage < pages.size - 1) "Continue" else "Get Started",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    fontFamily = InterFontFamily
                )
            }
            Spacer(Modifier.height(16.dp))
            if (pagerState.currentPage < pages.size - 1) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.height(48.dp)
                ) {
                    Text("Skip Tour", color = Color.White.copy(alpha = 0.6f), fontSize = 16.sp, fontFamily = InterFontFamily, fontWeight = FontWeight.Medium)
                }
            } else {
                Spacer(Modifier.height(48.dp))
            }
        }
    }
}

"""
        content = content.replace(old_composable, new_composable)
        with open(file_path, "w", encoding="utf-8") as f:
            f.write(content)
        print("Updated OnboardingScreen successfully.")
else:
    print("Could not find OnboardingScreen.")

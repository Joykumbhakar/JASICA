import re

file_path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

# 1. Update pages
old_pages = """    val pages = listOf(
        OnboardingPageInfo("Welcome to Jasica", "Your intelligent voice assistant for complete digital and hardware control.", null, R.drawable.jasica),
        OnboardingPageInfo("Voice Commands", "Say a command or tap the mic to control your LED 1, LED 2, LED 3, and more natively.", R.drawable.fluentui_system_icons_mic, null),
        OnboardingPageInfo("Manual Override", "Access the quick-switch panel from the top right home icon to toggle hardware without speaking.", R.drawable.fluentui_system_icons_home, null),
        OnboardingPageInfo("Stay Connected", "Pair your Bluetooth smart hub via the top right icon to get started.", R.drawable.fluentui_system_icons_phone_laptop, null)
    )"""

new_pages = """    val pages = listOf(
        OnboardingPageInfo("Welcome to Jasica", "Your intelligent voice assistant for complete digital and hardware control.", null, R.drawable.jasica),
        OnboardingPageInfo("Voice Commands", "Say a command or tap the mic to control your LED 1, LED 2, LED 3, and more natively.", null, R.drawable.tour_voice),
        OnboardingPageInfo("Manual Override", "Access the quick-switch panel from the top right home icon to toggle hardware without speaking.", null, R.drawable.tour_manual),
        OnboardingPageInfo("Stay Connected", "Pair your Bluetooth smart hub via the top right icon to get started.", null, R.drawable.tour_connect)
    )"""

content = content.replace(old_pages, new_pages)

# 2. Update Image display shape to rounded rectangle
old_image_box = """                        if (info.image != null) {
                            Image(
                                painterResource(info.image),
                                contentDescription = null,
                                modifier = Modifier.size(160.dp).clip(CircleShape).shadow(12.dp, CircleShape)
                            )
                        } else"""

new_image_box = """                        if (info.image != null) {
                            Image(
                                painterResource(info.image),
                                contentDescription = null,
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                modifier = Modifier.size(220.dp).shadow(24.dp, RoundedCornerShape(40.dp)).clip(RoundedCornerShape(40.dp))
                            )
                        } else"""

content = content.replace(old_image_box, new_image_box)

# 3. Update background to Solid Color, remove haze effect
old_box_start = """    Box(
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
    ) {"""

new_box_start = """    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0C))
            .clickable(enabled = false) {}
    ) {"""

content = content.replace(old_box_start, new_box_start)

# Save
with open(file_path, "w", encoding="utf-8") as f:
    f.write(content)
print("Updated OnboardingScreen background and images.")

import re

file_path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

# 1. Restore mic.png in JasicaScreen and VoiceCalibrationScreen
content = content.replace("R.drawable.fluentui_system_icons_mic", "R.drawable.mic")

# 2. Update Onboarding pages to use the new tour graphics
old_pages = """    val pages = listOf(
        OnboardingPageInfo("Welcome to Jasica", "Your intelligent voice assistant for complete digital and hardware control.", null, R.drawable.jasica),
        OnboardingPageInfo("Voice Commands", "Say a command or tap the mic to control your LEDs and devices natively.", R.drawable.mic, null),
        OnboardingPageInfo("Manual Override", "Access the quick-switch panel from the top right home icon to toggle hardware without speaking.", R.drawable.fluentui_system_icons_home, null),
        OnboardingPageInfo("Stay Connected", "Pair your Bluetooth smart hub via the top right icon to get started.", R.drawable.fluentui_system_icons_phone_laptop, null)
    )"""

new_pages = """    val pages = listOf(
        OnboardingPageInfo("Welcome to Jasica", "Your intelligent voice assistant for complete digital and hardware control.", null, R.drawable.jasica),
        OnboardingPageInfo("Voice Commands", "Say a command or tap the mic to control your LEDs and devices natively.", null, R.drawable.tour_voice),
        OnboardingPageInfo("Manual Override", "Access the quick-switch panel from the top right home icon to toggle hardware without speaking.", null, R.drawable.tour_manual),
        OnboardingPageInfo("Stay Connected", "Pair your Bluetooth smart hub via the top right icon to get started.", null, R.drawable.tour_connect)
    )"""

if old_pages in content:
    content = content.replace(old_pages, new_pages)
else:
    print("WARNING: Could not find old_pages string block!")

with open(file_path, "w", encoding="utf-8") as f:
    f.write(content)
print("Done fixing all.")

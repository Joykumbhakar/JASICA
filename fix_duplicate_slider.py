import os

path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

# Let's count how many times AppleJellySlider is defined
import re

matches = [m.start() for m in re.finditer(r'@OptIn\(ExperimentalMaterial3Api::class\)\n@Composable\nfun AppleJellySlider', text)]
print(f"Found {len(matches)} occurrences of AppleJellySlider")

if len(matches) > 1:
    # Delete the first one. Let's find its end.
    start_index = matches[0]
    end_index = text.find("@OptIn(ExperimentalMaterial3Api::class)\n@Composable\nfun AppleJellySlider", start_index + 100)
    
    if end_index != -1:
        text = text[:start_index] + text[end_index:]
        with open(path, "w", encoding="utf-8") as f:
            f.write(text)
        print("Removed duplicate AppleJellySlider")
    else:
        print("Could not find end of first occurrence")

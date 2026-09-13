import os
import re

path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

matches = [m.start() for m in re.finditer(r'fun AppleJellySlider\(', text)]
print(f"Found {len(matches)} occurrences of fun AppleJellySlider")

if len(matches) > 1:
    # We want to keep the LAST one (which has the zoom logic). 
    # Let's remove the FIRST one completely.
    # The first one starts around matches[0]. Let's find the @Composable before it.
    start_idx = text.rfind("@Composable", 0, matches[0])
    if start_idx == -1:
        start_idx = matches[0]
        
    # The first one ends where the next @Composable starts.
    end_idx = text.find("@Composable", matches[0] + 50)
    
    if end_idx != -1:
        text = text[:start_idx] + text[end_index:]
        with open(path, "w", encoding="utf-8") as f:
            f.write(text)
        print("Removed the duplicate!")

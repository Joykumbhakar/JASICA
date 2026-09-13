import os
import re

path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

matches = [m.start() for m in re.finditer(r'fun AppleJellySlider\(', text)]
print(f"Found {len(matches)} occurrences of fun AppleJellySlider")

if len(matches) > 1:
    start_idx = text.rfind("@Composable", 0, matches[0])
    # if it has OptIn, find that
    optin_idx = text.rfind("@OptIn", 0, matches[0])
    if optin_idx != -1 and optin_idx > start_idx - 100:
        start_idx = optin_idx

    end_idx = text.find("@Composable", matches[0] + 50)
    optin_end_idx = text.find("@OptIn", matches[0] + 50)
    
    # We want the start of the next component. It could be OptIn or Composable.
    if optin_end_idx != -1 and optin_end_idx < end_idx:
        end_idx = optin_end_idx
        
    if end_idx != -1:
        text = text[:start_idx] + text[end_idx:]
        with open(path, "w", encoding="utf-8") as f:
            f.write(text)
        print("Removed the duplicate!")

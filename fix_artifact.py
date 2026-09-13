import re
path = r"C:\Users\ss\.gemini\antigravity\brain\dd2f37c0-8d3e-4b44-858e-40e1193e2d80\release_notes.md"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

target = """### ?? Settings Rows — Custom Icons
- **"Use Portfolio API Key"** row now uses `maclaps.png` (no tint, transparent background).
- **"AI Model Engine"** row uses the custom JASICA AI Waves SVG icon (`ic_ai_waves`)."""

replacement = """### ?? Settings Rows — Custom Icons
- **"Use Portfolio API Key"** row now uses `maclaps.png` (no tint, transparent background).
- **"AI Model Engine"** row uses the custom JASICA AI Waves SVG icon (`ic_ai_waves`).
- Added **"Check For New Update"** option to easily find newer app versions.
- Added **"Current Version Release Notes"** option to view exactly what changed in the current build."""

if target in text:
    text = text.replace(target, replacement)
    with open(path, "w", encoding="utf-8") as f:
        f.write(text)
    print("Updated release notes artifact")
else:
    print("Could not find target in release notes")

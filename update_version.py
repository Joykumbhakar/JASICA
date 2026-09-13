import os

path = r"E:\Controller\app\build.gradle.kts"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

text = text.replace('versionCode = 12', 'versionCode = 13')
text = text.replace('versionName = "1.2.13.09.2026-beta"', 'versionName = "1.2.14.09.2026-beta"')

with open(path, "w", encoding="utf-8") as f:
    f.write(text)
print("Updated Android app version to 1.2.14.09.2026")

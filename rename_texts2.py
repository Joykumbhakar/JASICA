import re

file_path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

content = content.replace('"Turn off the light", "Turn on all", "Turn on the PC"', '"Turn off LED 3", "Turn on all", "Turn on LED 1"')
content = content.replace('lowerText.contains("pc") || lowerText.contains("computer")', 'lowerText.contains("led 1") || lowerText.contains("led one")')
content = content.replace('lowerText.contains("light") || lowerText.contains("room")', 'lowerText.contains("led 3") || lowerText.contains("led three")')

with open(file_path, "w", encoding="utf-8") as f:
    f.write(content)
print("Done.")

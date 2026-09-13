import re

file_path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

content = content.replace('PC / Computer', 'LED 1')
content = content.replace('RGB Lights', 'LED 2')
content = content.replace('Room Light', 'LED 3')
content = content.replace('Smart Plug', 'LED 4')
content = content.replace('Ceiling Fan', 'LED 5')
content = content.replace('Air Conditioner', 'LED 6')

with open(file_path, "w", encoding="utf-8") as f:
    f.write(content)
print("Done.")

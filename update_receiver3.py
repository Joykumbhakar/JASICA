import re
path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

target = """                        val command = if (isOn) pinOff else pinOn
                        
                        sendRawCommand(command)"""

replacement = """                        val command = if (isOn) pinOff else pinOn
                        
                        processCommandAndSync(command)"""

if target in text:
    text = text.replace(target, replacement)
    with open(path, "w", encoding="utf-8") as f:
        f.write(text)
    print("Fixed processCommandAndSync")
else:
    print("Could not find target")

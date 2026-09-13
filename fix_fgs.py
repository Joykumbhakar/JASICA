import re
path = r"E:\Controller\app\src\main\java\com\bristi\controller\FloatingControlService.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

target = """        startForeground(1003, notification)"""
replacement = """        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(1003, notification, android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(1003, notification)
        }"""

if target in text:
    text = text.replace(target, replacement)
    with open(path, "w", encoding="utf-8") as f:
        f.write(text)
    print("Fixed FGS type")
else:
    print("Could not find target")

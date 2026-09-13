import re
path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

target = """        registerReceiver(quickAccessReceiver, filter, Context.RECEIVER_NOT_EXPORTED)"""

replacement = """        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(quickAccessReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(quickAccessReceiver, filter)
        }"""

if target in text:
    text = text.replace(target, replacement)
    with open(path, "w", encoding="utf-8") as f:
        f.write(text)
    print("Fixed registerReceiver flag")
else:
    print("Could not find target")

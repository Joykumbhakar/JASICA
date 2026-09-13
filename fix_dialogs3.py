import re

file_path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

# Replace AppleRestartDialog usage
old_restart_usage = re.compile(r'AppleRestartDialog\(.*?\}\s*\)', re.DOTALL)
new_restart_usage = """AppleDialog(
                title = "Restart Required",
                message = "Your new settings have been saved. An app restart is recommended to apply all configurations.",
                primaryButtonText = "Restart Now",
                onPrimaryClick = {
                    showRestartDialog = false
                    context.startActivity(android.content.Intent(context, MainActivity::class.java).apply {
                        addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    })
                    Runtime.getRuntime().exit(0)
                },
                secondaryButtonText = "Later",
                onSecondaryClick = { showRestartDialog = false },
                layout = "horizontal",
                onDismiss = { showRestartDialog = false }
            )"""

content = old_restart_usage.sub(new_restart_usage, content)

with open(file_path, "w", encoding="utf-8") as f:
    f.write(content)
print("Done fixing restart usage.")

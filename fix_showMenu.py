import re

file_path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

# Remove from Row
old_row = """                // Header Icons Container
                Row(verticalAlignment = Alignment.CenterVertically) {
                    var showMenu by remember { mutableStateOf(false) }

                    // Bluetooth Icon"""
new_row = """                // Header Icons Container
                Row(verticalAlignment = Alignment.CenterVertically) {

                    // Bluetooth Icon"""
content = content.replace(old_row, new_row)

# Add to top level
old_top = """    val context = androidx.compose.ui.platform.LocalContext.current
    var updateNotification by remember { mutableStateOf<UpdateNotification?>(null) }"""
new_top = """    val context = androidx.compose.ui.platform.LocalContext.current
    var updateNotification by remember { mutableStateOf<UpdateNotification?>(null) }
    var showMenu by remember { mutableStateOf(false) }"""
content = content.replace(old_top, new_top)

with open(file_path, "w", encoding="utf-8") as f:
    f.write(content)

print("Fixed showMenu scope")

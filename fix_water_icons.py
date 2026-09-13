import re

file_path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

# 1. Fix Water Reminder bug (wrong key when disabling, missing stopAlarm)
old_password_check = """                    val correctPassword = sharedPrefs.getString("WATER_REMINDER_PASSWORD", "0000") ?: "0000"
                    if (passwordInput == correctPassword) {
                        showPasswordDialog = false
                        passwordError = false
                        passwordInput = ""
                        sharedPrefs.edit().putBoolean("WATER_REMINDER_ENABLED", false).apply()
                    } else {"""
new_password_check = """                    val correctPassword = sharedPrefs.getString("WATER_REMINDER_PASSWORD", "0000") ?: "0000"
                    if (passwordInput == correctPassword) {
                        showPasswordDialog = false
                        passwordError = false
                        passwordInput = ""
                        waterReminderInput = false
                        sharedPrefs.edit().putBoolean("WATER_REMINDER", false).apply()
                        WaterReminderManager.stopAlarm(context)
                        android.widget.Toast.makeText(context, "Water Reminder Disabled", android.widget.Toast.LENGTH_SHORT).show()
                    } else {"""
content = content.replace(old_password_check, new_password_check)

# 2. Add Settings.png to settings screen header
old_settings_header = """            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp, start = 8.dp, end = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    "Settings",
                    color = textPrimary,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = InterFontFamily,
                    letterSpacing = (-1).sp
                )"""
new_settings_header = """            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp, start = 8.dp, end = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = R.drawable.settings_header),
                        contentDescription = "Settings Icon",
                        modifier = Modifier.size(36.dp).padding(end = 8.dp)
                    )
                    Text(
                        "Settings",
                        color = textPrimary,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = InterFontFamily,
                        letterSpacing = (-1).sp
                    )
                }"""
content = content.replace(old_settings_header, new_settings_header)

# 3. Add AppleMenuItemImage and use it for Arduino Code
apple_menu_item_code = """fun AppleMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }"""

new_apple_menu_item_image = """fun AppleMenuItemImage(
    painter: androidx.compose.ui.graphics.painter.Painter,
    text: String,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                androidx.compose.foundation.gestures.detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = { onClick() }
                )
            }
            .background(if (isPressed) Color.Black.copy(alpha=0.1f) else Color.Transparent)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        androidx.compose.foundation.Image(painter, contentDescription = null, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(16.dp))
        Text(text, color = Color.Black, fontSize = 16.sp, fontFamily = InterFontFamily)
    }
}

fun AppleMenuItem("""
content = content.replace(apple_menu_item_code, new_apple_menu_item_image)

old_arduino_menu = """AppleMenuItem(icon = Icons.Rounded.Code, text = "Arduino Code", onClick = { showMenu = false; onArduinoCodeTap() })"""
new_arduino_menu = """AppleMenuItemImage(painter = androidx.compose.ui.res.painterResource(id = R.drawable.arduino_ide), text = "Arduino Code", onClick = { showMenu = false; onArduinoCodeTap() })"""
content = content.replace(old_arduino_menu, new_arduino_menu)


with open(file_path, "w", encoding="utf-8") as f:
    f.write(content)

# 4. Modify AndroidManifest to add USE_EXACT_ALARM
manifest_path = r"E:\Controller\app\src\main\AndroidManifest.xml"
with open(manifest_path, "r", encoding="utf-8") as f:
    manifest = f.read()

manifest = manifest.replace(
    '<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />',
    '<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />\n    <uses-permission android:name="android.permission.USE_EXACT_ALARM" />'
)

with open(manifest_path, "w", encoding="utf-8") as f:
    f.write(manifest)

print("Fixes applied successfully!")

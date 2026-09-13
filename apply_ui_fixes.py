import re

file_path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

# 1. Add DashboardRoutineButtonRes
btn_res_code = """
@Composable
fun DashboardRoutineButtonRes(modifier: Modifier = Modifier, title: String, iconRes: Int, onClick: () -> Unit) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(listOf(Color(0xFF4A00E0).copy(alpha = 0.4f), Color(0xFF1E32AA).copy(alpha = 0.4f))))
            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(24.dp))
            .clickable { onClick() }
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        androidx.compose.foundation.Image(painter = androidx.compose.ui.res.painterResource(id = iconRes), contentDescription = null, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, fontFamily = InterFontFamily)
    }
}
"""
content = content.replace("@Composable\nfun DashboardRoutineButton(", btn_res_code + "\n@Composable\nfun DashboardRoutineButton(")

# 2. Update Arduino Code button usage
content = content.replace(
    'DashboardRoutineButton(modifier = Modifier.weight(1f), title = "Arduino Code", icon = Icons.Outlined.DateRange)',
    'DashboardRoutineButtonRes(modifier = Modifier.weight(1f), title = "Arduino Code", iconRes = R.drawable.arduino_ide)'
)

# 3. Update Settings Header
old_settings_header = """            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp, start = 8.dp, end = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    "Settings","""
new_settings_header = """            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp, start = 8.dp, end = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = R.drawable.settings_icon),
                        contentDescription = "Settings",
                        modifier = Modifier.size(38.dp).padding(end = 12.dp)
                    )
                    Text(
                        "Settings","""
content = content.replace(old_settings_header, new_settings_header)
# Need to close the Row we opened in header
old_settings_done = """                        }.padding(horizontal = 8.dp, vertical = 6.dp)
                )
            }"""
new_settings_done = """                        }.padding(horizontal = 8.dp, vertical = 6.dp)
                )
            }"""
# Wait, I just nested the Text inside a Row. The Text is followed by the Done button in the parent Row. 
# Let me do it via regex exactly around the "Settings" text.
old_settings_header_full = """            Row(
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
new_settings_header_full = """            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp, start = 8.dp, end = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = R.drawable.settings_icon),
                        contentDescription = "Settings",
                        modifier = Modifier.size(38.dp).padding(end = 12.dp)
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
content = content.replace(old_settings_header_full, new_settings_header_full)


# 4. Device Control Card Icon -> robot.png
# In DeviceControlCard
#                 Icon(
#                     imageVector = Icons.Outlined.Home,
#                     contentDescription = null,
#                     tint = if (isChecked) Color.White else iconColor,
#                     modifier = Modifier.size(24.dp)
#                 )
old_device_icon = """                Icon(
                    imageVector = Icons.Outlined.Home,
                    contentDescription = null,
                    tint = if (isChecked) Color.White else iconColor,
                    modifier = Modifier.size(24.dp)
                )"""
new_device_icon = """                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = R.drawable.robot),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    colorFilter = if (isChecked) androidx.compose.ui.graphics.ColorFilter.tint(Color.White) else androidx.compose.ui.graphics.ColorFilter.tint(iconColor)
                )"""
content = content.replace(old_device_icon, new_device_icon)

with open(file_path, "w", encoding="utf-8") as f:
    f.write(content)
print("Applied UI fixes")

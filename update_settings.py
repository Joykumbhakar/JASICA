import re

file_path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

# 1. Fix LucideIconBox
old_icon_box = """fun LucideIconBox(
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .size(30.dp)
            .clip(RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}"""

new_icon_box = """fun LucideIconBox(
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .size(30.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}"""
content = content.replace(old_icon_box, new_icon_box)

# 2. Fix AppleSettingsRow divider padding
old_row = """        Column(modifier = Modifier.weight(1f).padding(vertical = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth().padding(end = 16.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, color = titleColor, fontSize = 16.sp, fontWeight = FontWeight.Medium, fontFamily = InterFontFamily)
                    if (subtitle != null) {
                        Spacer(Modifier.height(2.dp))
                        Text(subtitle, color = subColor, fontSize = 13.sp, fontFamily = InterFontFamily)
                    }
                }
                if (control != null) {
                    Spacer(Modifier.width(12.dp))
                    control()
                }
            }
            if (showDivider) {
                androidx.compose.material3.HorizontalDivider(modifier = Modifier.padding(top = 12.dp), color = dividerColor, thickness = 0.5.dp)
            }
        }"""

new_row = """        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth().padding(end = 16.dp, top = 12.dp, bottom = 12.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, color = titleColor, fontSize = 16.sp, fontWeight = FontWeight.Medium, fontFamily = InterFontFamily)
                    if (subtitle != null) {
                        Spacer(Modifier.height(2.dp))
                        Text(subtitle, color = subColor, fontSize = 13.sp, fontFamily = InterFontFamily)
                    }
                }
                if (control != null) {
                    Spacer(Modifier.width(12.dp))
                    control()
                }
            }
            if (showDivider) {
                androidx.compose.material3.HorizontalDivider(color = dividerColor, thickness = 0.5.dp)
            }
        }"""
content = content.replace(old_row, new_row)

# 3. Enhance API Key field inside AI Group
old_key_field = """                        if (!adminKeyInput) {
                            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
                                Text("Custom Gemini API Key", color = textSecondary, fontSize = 13.sp, fontFamily = InterFontFamily)
                                Spacer(Modifier.height(6.dp))
                                androidx.compose.material3.OutlinedTextField(
                                    value = geminiKeyInput,
                                    onValueChange = { 
                                        geminiKeyInput = it
                                        apiKeyInput = it
                                        sharedPrefs.edit().putString("GEMINI_API_KEY", it.trim()).apply()
                                    },
                                    placeholder = { Text("AIzaSy...", color = textSecondary.copy(0.5f)) },
                                    visualTransformation = if (showApiKey) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                                    trailingIcon = {
                                        TextButton(onClick = { showApiKey = !showApiKey }) {
                                            Text(if (showApiKey) "Hide" else "Show", color = Color(0xFF007AFF), fontSize = 12.sp)
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true,
                                    colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF007AFF),
                                        unfocusedBorderColor = cardBorder
                                    )
                                )
                            }
                        }"""

new_key_field = """                        if (!adminKeyInput) {
                            AppleSettingsRow(
                                title = "Custom API Key",
                                icon = { Icon(Icons.Rounded.Key, contentDescription = null, modifier = Modifier.size(22.dp), tint = Color.White) },
                                iconBgColor = Color(0xFF8E8E93),
                                showDivider = false,
                                isDark = darkModeInput,
                                control = {
                                    androidx.compose.foundation.text.BasicTextField(
                                        value = geminiKeyInput,
                                        onValueChange = { 
                                            geminiKeyInput = it
                                            apiKeyInput = it
                                            sharedPrefs.edit().putString("GEMINI_API_KEY", it.trim()).apply()
                                        },
                                        textStyle = androidx.compose.ui.text.TextStyle(
                                            color = textSecondary,
                                            fontSize = 16.sp,
                                            fontFamily = InterFontFamily,
                                            textAlign = androidx.compose.ui.text.style.TextAlign.End
                                        ),
                                        visualTransformation = if (showApiKey) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                                        modifier = Modifier.weight(1f).padding(end = 8.dp),
                                        singleLine = true,
                                        decorationBox = { innerTextField ->
                                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                                                if (geminiKeyInput.isEmpty()) {
                                                    Text("AIzaSy...", color = textSecondary.copy(alpha=0.3f), fontSize = 16.sp)
                                                }
                                                innerTextField()
                                            }
                                        }
                                    )
                                    Text(
                                        text = if (showApiKey) "Hide" else "Show",
                                        color = Color(0xFF007AFF),
                                        fontSize = 15.sp,
                                        modifier = Modifier.clickable { showApiKey = !showApiKey }
                                    )
                                }
                            )
                        }"""
content = content.replace(old_key_field, new_key_field)

with open(file_path, "w", encoding="utf-8") as f:
    f.write(content)

print("Updated settings UI")

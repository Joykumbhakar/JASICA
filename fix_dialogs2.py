import re

file_path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(file_path, "r", encoding="utf-8") as f:
    lines = f.readlines()

apple_dialog_code = """@Composable
fun AppleDialog(
    title: String,
    message: String,
    primaryButtonText: String,
    onPrimaryClick: () -> Unit,
    primaryIsDestructive: Boolean = false,
    secondaryButtonText: String? = null,
    onSecondaryClick: (() -> Unit)? = null,
    layout: String = "horizontal",
    onDismiss: () -> Unit,
    customContent: @Composable (() -> Unit)? = null
) {
    val isSystemInDarkTheme = androidx.compose.foundation.isSystemInDarkTheme()
    
    var show by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { show = true }
    
    val scale by animateFloatAsState(
        targetValue = if (show) 1f else 1.1f,
        animationSpec = androidx.compose.animation.core.tween(250, easing = androidx.compose.animation.core.FastOutSlowInEasing),
        label = "dialogScale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (show) 1f else 0f,
        animationSpec = androidx.compose.animation.core.tween(250, easing = androidx.compose.animation.core.LinearOutSlowInEasing),
        label = "dialogAlpha"
    )

    val dialogBg = if (isSystemInDarkTheme) Color(0xEB2C2C2E) else Color(0xEBFFFFFF)
    val titleColor = if (isSystemInDarkTheme) Color.White else Color.Black
    val dividerColor = if (isSystemInDarkTheme) Color(0x40545458) else Color(0x203C3C43)
    val blueColor = if (isSystemInDarkTheme) Color(0xFF0A84FF) else Color(0xFF007AFF)
    val redColor = if (isSystemInDarkTheme) Color(0xFFFF453A) else Color(0xFFFF3B30)

    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f * alpha))
                .clickable(
                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                    indication = null
                ) { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .width(270.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        this.alpha = alpha
                    }
                    .clip(RoundedCornerShape(14.dp))
                    .background(dialogBg)
                    .clickable(
                        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                        indication = null
                    ) {},
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier.padding(top = 18.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = title,
                        color = titleColor,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = InterFontFamily,
                        textAlign = TextAlign.Center,
                        letterSpacing = (-0.4).sp,
                        lineHeight = 22.sp
                    )
                    if (message.isNotBlank()) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = message,
                            color = titleColor,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal,
                            fontFamily = InterFontFamily,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                    }
                    if (customContent != null) {
                        Spacer(Modifier.height(12.dp))
                        customContent()
                    }
                }

                androidx.compose.material3.HorizontalDivider(color = dividerColor, thickness = 0.5.dp)

                if (layout == "horizontal" && secondaryButtonText != null) {
                    Row(modifier = Modifier.fillMaxWidth().height(44.dp)) {
                        Box(
                            modifier = Modifier.weight(1f).fillMaxHeight().clickable { onSecondaryClick?.invoke() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = secondaryButtonText,
                                color = blueColor,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Normal,
                                fontFamily = InterFontFamily
                            )
                        }
                        androidx.compose.material3.VerticalDivider(color = dividerColor, thickness = 0.5.dp)
                        Box(
                            modifier = Modifier.weight(1f).fillMaxHeight().clickable { onPrimaryClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = primaryButtonText,
                                color = if (primaryIsDestructive) redColor else blueColor,
                                fontSize = 17.sp,
                                fontWeight = if (primaryIsDestructive) FontWeight.Normal else FontWeight.SemiBold,
                                fontFamily = InterFontFamily
                            )
                        }
                    }
                } else {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(44.dp).clickable { onPrimaryClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = primaryButtonText,
                                color = if (primaryIsDestructive) redColor else blueColor,
                                fontSize = 17.sp,
                                fontWeight = if (primaryIsDestructive) FontWeight.Normal else FontWeight.SemiBold,
                                fontFamily = InterFontFamily
                            )
                        }
                        if (secondaryButtonText != null) {
                            androidx.compose.material3.HorizontalDivider(color = dividerColor, thickness = 0.5.dp)
                            Box(
                                modifier = Modifier.fillMaxWidth().height(44.dp).clickable { onSecondaryClick?.invoke() },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = secondaryButtonText,
                                    color = blueColor,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = InterFontFamily
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
"""

new_lines = [line + '\n' for line in apple_dialog_code.split('\n')]
# Important: don't double up \n if apple_dialog_code already has them. split() removes \n.
# Actually, let's just join the file back to string, then replace string.
content = "".join(lines[:4678]) + apple_dialog_code + "\n" + "".join(lines[4797:])

# Now replace the usages exactly as before!
# Restart Dialog Usage
old_restart_usage = """        if (showRestartDialog) {
            AppleRestartDialog(
                onDismiss = { showRestartDialog = false },
                onRestart = {
                    showRestartDialog = false
                    context.startActivity(android.content.Intent(context, MainActivity::class.java).apply {
                        addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    })
                    Runtime.getRuntime().exit(0)
                }
            )
        }"""
new_restart_usage = """        if (showRestartDialog) {
            AppleDialog(
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
            )
        }"""
content = content.replace(old_restart_usage, new_restart_usage)

# Reset Confirmation Dialog Usage
old_reset_usage = re.compile(r'// Reset Confirmation Dialog.*?if\s*\(showResetConfirmDialog\)\s*\{\s*AlertDialog\(.*?\}\s*\)\s*\}', re.DOTALL)
new_reset_usage = """// Reset Confirmation Dialog
        if (showResetConfirmDialog) {
            AppleDialog(
                title = "Reset Hardware Config?",
                message = "All device names, commands, and pins will be reset to default values. This action cannot be undone.",
                primaryButtonText = "Reset",
                primaryIsDestructive = true,
                onPrimaryClick = {
                    DEFAULT_DEVICES.forEach { dev ->
                        sharedPrefs.edit()
                            .remove("DEV_${dev.id}_NAME")
                            .remove("DEV_${dev.id}_ON_CMD")
                            .remove("DEV_${dev.id}_OFF_CMD")
                            .remove("DEV_${dev.id}_PIN_ON")
                            .remove("DEV_${dev.id}_PIN_OFF")
                            .apply()
                    }
                    showResetConfirmDialog = false
                    android.widget.Toast.makeText(context, "Devices Reset to Defaults", android.widget.Toast.LENGTH_SHORT).show()
                },
                secondaryButtonText = "Cancel",
                onSecondaryClick = { showResetConfirmDialog = false },
                layout = "vertical",
                onDismiss = { showResetConfirmDialog = false }
            )
        }"""
content = old_reset_usage.sub(new_reset_usage, content)

# Water Reminder Dialog Usage
old_water_usage = re.compile(r'// Water reminder password dialog.*?if\s*\(showPasswordDialog\)\s*\{\s*AlertDialog\(.*?\}\s*\)\s*\}', re.DOTALL)
new_water_usage = """// Water reminder password dialog
        if (showPasswordDialog) {
            AppleDialog(
                title = "Enter Admin Password",
                message = "A password is required to turn off the water reminder.",
                primaryButtonText = "Submit",
                onPrimaryClick = {
                    val correctPassword = sharedPrefs.getString("WATER_REMINDER_PASSWORD", "0000") ?: "0000"
                    if (passwordInput == correctPassword) {
                        showPasswordDialog = false
                        passwordError = false
                        passwordInput = ""
                        sharedPrefs.edit().putBoolean("WATER_REMINDER_ENABLED", false).apply()
                    } else {
                        passwordError = true
                    }
                },
                secondaryButtonText = "Cancel",
                onSecondaryClick = { 
                    showPasswordDialog = false
                    passwordError = false
                    passwordInput = ""
                },
                layout = "horizontal",
                onDismiss = { 
                    showPasswordDialog = false
                    passwordError = false
                    passwordInput = ""
                },
                customContent = {
                    androidx.compose.material3.OutlinedTextField(
                        value = passwordInput,
                        onValueChange = { passwordInput = it; passwordError = false },
                        placeholder = { Text("Password", color = textSecondary.copy(alpha=0.5f), fontFamily = InterFontFamily) },
                        isError = passwordError,
                        textStyle = androidx.compose.ui.text.TextStyle(color = textPrimary, fontFamily = InterFontFamily),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Password),
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF007AFF),
                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    )
                    if (passwordError) {
                        Text("Incorrect password", color = Color(0xFFFF3B30), fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp), fontFamily = InterFontFamily)
                    }
                }
            )
        }"""
content = old_water_usage.sub(new_water_usage, content)

with open(file_path, "w", encoding="utf-8") as f:
    f.write(content)
print("Done fixing dialogs securely.")

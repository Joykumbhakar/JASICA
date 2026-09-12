import re
with open(r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt", "r", encoding="utf-8") as f:
    text = f.read()

old_menu = """                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(Color(0xFF1E1E2E))
                        ) {
                            DropdownMenuItem(
                                text = { Text("Manual Controls", color = Color.White, fontFamily = InterFontFamily) },
                                onClick = { showMenu = false; onManualControlsTap() },
                                leadingIcon = { Icon(Icons.Outlined.Home, contentDescription = null, tint = JasicaWhite) }
                            )
                            DropdownMenuItem(
                                text = { Text("Chat History", color = Color.White, fontFamily = InterFontFamily) },
                                onClick = { showMenu = false; onHistoryTap() },
                                leadingIcon = { Icon(Icons.Outlined.History, contentDescription = null, tint = JasicaWhite) }
                            )
                            DropdownMenuItem(
                                text = { Text("Arduino Code", color = Color.White, fontFamily = InterFontFamily) },
                                onClick = { showMenu = false; onArduinoCodeTap() },
                                leadingIcon = { Icon(painterResource(id = android.R.drawable.ic_menu_edit), contentDescription = null, tint = JasicaWhite) }
                            )
                            DropdownMenuItem(
                                text = { Text("Settings", color = Color.White, fontFamily = InterFontFamily) },
                                onClick = { showMenu = false; onSettingsTap() },
                                leadingIcon = { Icon(Icons.Outlined.Settings, contentDescription = null, tint = JasicaWhite) }
                            )
                        }"""

new_menu = """                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(Color(0xFF1E1E2E))
                        ) {
                            DropdownMenuItem(
                                text = { Text("Manual Controls", color = Color.White, fontFamily = InterFontFamily) },
                                onClick = { showMenu = false; onManualControlsTap() },
                                leadingIcon = { Image(painter = painterResource(id = R.drawable.ic_home_mac), contentDescription = null, modifier = Modifier.size(24.dp)) }
                            )
                            DropdownMenuItem(
                                text = { Text("Chat History", color = Color.White, fontFamily = InterFontFamily) },
                                onClick = { showMenu = false; onHistoryTap() },
                                leadingIcon = { Image(painter = painterResource(id = R.drawable.ic_terminal_mac), contentDescription = null, modifier = Modifier.size(24.dp)) }
                            )
                            DropdownMenuItem(
                                text = { Text("Arduino Code", color = Color.White, fontFamily = InterFontFamily) },
                                onClick = { showMenu = false; onArduinoCodeTap() },
                                leadingIcon = { Image(painter = painterResource(id = R.drawable.ic_apple_mac), contentDescription = null, modifier = Modifier.size(24.dp)) }
                            )
                            DropdownMenuItem(
                                text = { Text("Settings", color = Color.White, fontFamily = InterFontFamily) },
                                onClick = { showMenu = false; onSettingsTap() },
                                leadingIcon = { Image(painter = painterResource(id = R.drawable.ic_settings_mac), contentDescription = null, modifier = Modifier.size(24.dp)) }
                            )
                        }"""

if old_menu in text:
    text = text.replace(old_menu, new_menu)
else:
    print("Old menu not found!")

# Make sure Image and R are imported or not needed. Image is probably imported, R needs to be there.
# It should be imported.

with open(r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt", "w", encoding="utf-8") as f:
    f.write(text)

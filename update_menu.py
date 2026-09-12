import re
import os

file_path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

# 1. Remove DropdownMenu from the Header
old_header_box = """                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                painter = painterResource(id = android.R.drawable.ic_menu_more),
                                contentDescription = "More Options",
                                tint = JasicaWhite
                            )
                        }
                        
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(Color(0xFF1E1E2E))
                        ) {
                            DropdownMenuItem(
                                text = { Text("Manual Controls", color = Color.White, fontFamily = InterFontFamily) },
                                onClick = { showMenu = false; onManualControlsTap() },
                                leadingIcon = { Icon(Icons.Rounded.Home, contentDescription = null, modifier = Modifier.size(24.dp), tint = JasicaWhite) }
                            )
                            DropdownMenuItem(
                                text = { Text("Chat History", color = Color.White, fontFamily = InterFontFamily) },
                                onClick = { showMenu = false; onHistoryTap() },
                                leadingIcon = { Icon(Icons.Rounded.History, contentDescription = null, modifier = Modifier.size(24.dp), tint = JasicaWhite) }
                            )
                            DropdownMenuItem(
                                text = { Text("Arduino Code", color = Color.White, fontFamily = InterFontFamily) },
                                onClick = { showMenu = false; onArduinoCodeTap() },
                                leadingIcon = { Icon(Icons.Rounded.Code, contentDescription = null, modifier = Modifier.size(24.dp), tint = JasicaWhite) }
                            )
                            DropdownMenuItem(
                                text = { Text("Settings", color = Color.White, fontFamily = InterFontFamily) },
                                onClick = { showMenu = false; onSettingsTap() },
                                leadingIcon = { Icon(Icons.Rounded.Settings, contentDescription = null, modifier = Modifier.size(24.dp), tint = JasicaWhite) }
                            )
                        }
                    }"""

new_header_box = """                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                painter = painterResource(id = android.R.drawable.ic_menu_more),
                                contentDescription = "More Options",
                                tint = JasicaWhite
                            )
                        }
                    }"""
content = content.replace(old_header_box, new_header_box)


# 2. Add AnimatedVisibility Menu at the end of the Overlays section
old_overlays = """        if (showDialog) {
            androidx.activity.compose.BackHandler { onDismissDialog() }
            DeviceSelectionDialog(pairedDevices, availableDevices, isScanning, connectedDeviceAddress, hazeState, onDeviceSelect, onScanTap, onDismissDialog)
        }"""

new_overlays = """        if (showDialog) {
            androidx.activity.compose.BackHandler { onDismissDialog() }
            DeviceSelectionDialog(pairedDevices, availableDevices, isScanning, connectedDeviceAddress, hazeState, onDeviceSelect, onScanTap, onDismissDialog)
        }
        
        if (showMenu) {
            Box(modifier = Modifier.fillMaxSize().pointerInput(Unit) { detectTapGestures { showMenu = false } })
        }
        
        androidx.compose.animation.AnimatedVisibility(
            visible = showMenu,
            enter = fadeIn(animationSpec = tween(250)) + scaleIn(initialScale = 0.9f, transformOrigin = androidx.compose.ui.graphics.TransformOrigin(1f, 0f), animationSpec = tween(250)),
            exit = fadeOut(animationSpec = tween(200)) + scaleOut(targetScale = 0.9f, transformOrigin = androidx.compose.ui.graphics.TransformOrigin(1f, 0f), animationSpec = tween(200)),
            modifier = Modifier.align(Alignment.TopEnd).padding(top = 64.dp, end = 24.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(220.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .then(
                        if (hazeState != null) Modifier.hazeEffect(
                            state = hazeState,
                            style = dev.chrisbanes.haze.HazeStyle(
                                blurRadius = 24.dp,
                                tint = dev.chrisbanes.haze.HazeTint(Color(0xFFF2F2F7).copy(alpha=0.6f))
                            )
                        ) else Modifier.background(Color(0xFFF2F2F7).copy(alpha = 0.95f))
                    )
            ) {
                Column {
                    AppleMenuItem(icon = Icons.Rounded.Home, text = "Manual Controls", onClick = { showMenu = false; onManualControlsTap() })
                    androidx.compose.material3.HorizontalDivider(color = Color.Black.copy(alpha=0.1f), thickness = 0.5.dp, modifier = Modifier.padding(start = 44.dp))
                    AppleMenuItem(icon = Icons.Rounded.History, text = "Chat History", onClick = { showMenu = false; onHistoryTap() })
                    androidx.compose.material3.HorizontalDivider(color = Color.Black.copy(alpha=0.1f), thickness = 0.5.dp, modifier = Modifier.padding(start = 44.dp))
                    AppleMenuItem(icon = Icons.Rounded.Code, text = "Arduino Code", onClick = { showMenu = false; onArduinoCodeTap() })
                    androidx.compose.material3.HorizontalDivider(color = Color.Black.copy(alpha=0.1f), thickness = 0.5.dp, modifier = Modifier.padding(start = 44.dp))
                    AppleMenuItem(icon = Icons.Rounded.Settings, text = "Settings", onClick = { showMenu = false; onSettingsTap() })
                }
            }
        }"""
content = content.replace(old_overlays, new_overlays)


# 3. Add AppleMenuItem Composable at the very end of the file
apple_menu_item = """
@Composable
fun AppleMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
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
        Icon(imageVector = icon, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Text(text, color = Color.Black, fontSize = 16.sp, fontFamily = InterFontFamily, fontWeight = FontWeight.Medium)
    }
}
"""
content += apple_menu_item

with open(file_path, "w", encoding="utf-8") as f:
    f.write(content)

print("Updated Main Menu to Apple iOS Context Menu style!")

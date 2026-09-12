import re

with open(r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt", "r", encoding="utf-8") as f:
    content = f.read()

content = content.replace("\r\n", "\n")

def replace_block(content, start_str, end_str, new_block, name):
    start_idx = content.find(start_str)
    if start_idx == -1:
        print(f"Error: {name} start_str not found!")
        return content
    
    end_idx = content.find(end_str, start_idx)
    if end_idx == -1:
        print(f"Error: {name} end_str not found!")
        return content
        
    return content[:start_idx] + new_block + content[end_idx:]

# 1. JasicaGraphicalDialogPanel
dialog_panel = """@Composable
fun JasicaGraphicalDialogPanel(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xE61C1C1E))
                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                content()
            }
        }
    }
}
"""
content = replace_block(content, "@Composable\nfun JasicaGraphicalDialogPanel(", "\n// ─────────────────────────────────────────────────────────────────────────────\n//  Dialogs (Adapted with Orange Theme & High Contrast text)", dialog_panel, "JasicaGraphicalDialogPanel")

# 2. ArduinoCodeScreen
arduino_screen = """@Composable
fun ArduinoCodeScreen(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current

    var selectedBoard by remember { mutableStateOf(0) }
    var copied by remember { mutableStateOf(false) }

    val boards = listOf("Arduino UNO", "ESP32 Dev v1")
    val codes = listOf(ARDUINO_UNO_CODE, ESP32_CODE)

    LaunchedEffect(copied) {
        if (copied) {
            kotlinx.coroutines.delay(2000)
            copied = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable(enabled = false) {}
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 54.dp, start = 16.dp, end = 16.dp, bottom = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp, start = 8.dp, end = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Firmware",
                    color = Color.White,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = InterFontFamily
                )
                TextButton(onClick = onDismiss) {
                    Text("Done", color = Color(0xFF0A84FF), fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF1C1C1E))
                    .padding(2.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                boards.forEachIndexed { index, name ->
                    val isSelected = selectedBoard == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) Color(0xFF636366) else Color.Transparent)
                            .clickable { selectedBoard = index },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = name,
                            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.5f),
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                            fontFamily = InterFontFamily
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1C1C1E))
                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
            ) {
                androidx.compose.foundation.text.selection.SelectionContainer {
                    Text(
                        text = codes[selectedBoard],
                        color = Color(0xFF5AC8FA),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .horizontalScroll(rememberScrollState())
                            .padding(16.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(codes[selectedBoard]))
                    copied = true
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (copied) Color(0xFF34C759) else Color(0xFF0A84FF)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    imageVector = if (copied) Icons.Rounded.Check else androidx.compose.material.icons.outlined.Info,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = if (copied) "COPIED TO CLIPBOARD" else "COPY FIRMWARE CODE",
                    color = Color.White,
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
"""
content = replace_block(content, "@Composable\nfun ArduinoCodeScreen(", "\n// ─────────────────────────────────────────────────────────────────────────────\n//  Dashboard Content", arduino_screen, "ArduinoCodeScreen")

# 3. ChatHistoryScreen
chat_screen = """@Composable
fun ChatHistoryScreen(history: List<ChatMessage>, onDismiss: () -> Unit) {
    val listState = rememberLazyListState()

    LaunchedEffect(history.size) {
        if (history.isNotEmpty()) {
            listState.animateScrollToItem(history.size - 1)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable(enabled = false) {}
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 54.dp, start = 16.dp, end = 16.dp, bottom = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp, start = 8.dp, end = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recents",
                    color = Color.White,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = InterFontFamily
                )
                TextButton(onClick = onDismiss) {
                    Text("Done", color = Color(0xFF0A84FF), fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (history.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No recent interactions.",
                        color = Color.White.copy(alpha = 0.5f),
                        fontFamily = InterFontFamily,
                        fontSize = 14.sp
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(history) { message ->
                        ChatBubble(message)
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.isUser
    val alignment = if (isUser) Alignment.End else Alignment.Start
    val bubbleColor = if (isUser) Color(0xFF0A84FF) else Color(0xFF2C2C2E)
    val textColor = Color.White

    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
        horizontalAlignment = alignment
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(RoundedCornerShape(
                    topStart = 20.dp,
                    topEnd = 20.dp,
                    bottomStart = if (isUser) 20.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 20.dp
                ))
                .background(bubbleColor)
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            MessageFormattedText(message.text, textColor)
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = message.time,
            color = Color.White.copy(alpha = 0.3f),
            fontSize = 10.sp,
            fontFamily = InterFontFamily,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}
"""
content = replace_block(content, "@Composable\nfun ChatHistoryScreen(", "\n@Composable\nfun MessageFormattedText(", chat_screen, "ChatHistoryScreen")

# 4. ManualControlsScreen
controls_screen = """@Composable
fun ManualControlsScreen(deviceStates: Map<String, Boolean>, onDismiss: () -> Unit, onSendCommand: (String) -> Unit) {
    val devices = listOf(
        ManualDevice("dev1", "1st LED", "a", "A"),
        ManualDevice("dev2", "2nd LED", "b", "B"),
        ManualDevice("dev3", "3rd LED", "c", "C"),
        ManualDevice("dev4", "4th LED", "d", "D"),
        ManualDevice("dev5", "5th LED", "e", "E"),
        ManualDevice("dev6", "6th LED", "f", "F")
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(enabled = false) {}
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 54.dp, start = 20.dp, end = 20.dp, bottom = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Control Center",
                    color = Color.White,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = InterFontFamily
                )
                TextButton(onClick = onDismiss) {
                    Text("Done", color = Color(0xFF0A84FF), fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Text(
                text = "Tap to toggle hardware manually.",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 14.sp,
                fontFamily = InterFontFamily,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(devices.chunked(2)) { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        for (device in rowItems) {
                            DeviceControlCard(
                                modifier = Modifier.weight(1f),
                                device = device,
                                isChecked = deviceStates[device.id] == true,
                                onSendCommand = onSendCommand
                            )
                        }
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DeviceControlCard(
    modifier: Modifier = Modifier,
    device: ManualDevice,
    isChecked: Boolean,
    onSendCommand: (String) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val cardScale by animateFloatAsState(
        targetValue = if (isChecked) 1f else 0.98f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    val deviceIcon = when (device.id) {
        "a" -> "💻"
        "b" -> "🌈"
        "c" -> "💡"
        "d" -> "🔌"
        "e" -> "🌀"
        "f" -> "❄️"
        else -> "⚙️"
    }

    Box(
        modifier = modifier
            .scale(cardScale)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(24.dp))
            .background(if (isChecked) Color.White else Color(0xFF1C1C1E))
            .clickable {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onSendCommand(if (!isChecked) device.cmdOn else device.cmdOff)
            }
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (isChecked) Color(0xFF0A84FF) else Color.White.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = deviceIcon, fontSize = 20.sp)
                }
                
                if (isChecked) {
                    Text("ON", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
                }
            }

            Column {
                Text(
                    text = device.name,
                    color = if (isChecked) Color.Black else Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = InterFontFamily,
                    fontSize = 15.sp,
                    lineHeight = 18.sp
                )
                Text(
                    text = if (isChecked) "Running" else "Off",
                    color = if (isChecked) Color.Black.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.4f),
                    fontFamily = InterFontFamily,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}
"""
content = replace_block(content, "@Composable\nfun ManualControlsScreen(", "\n// ─────────────────────────────────────────────────────────────────────────────\n//  Quick Action Chips", controls_screen, "ManualControlsScreen")

with open(r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt", "w", encoding="utf-8") as f:
    f.write(content)

print("Done")

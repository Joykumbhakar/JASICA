import re

file_path = r'e:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    lines = f.readlines()

start_idx = -1
for i, line in enumerate(lines):
    if '@Composable' in line and 'fun SettingsDialog(' in lines[i+1]:
        start_idx = i
        break
    if 'fun SettingsDialog(' in line:
        if '@Composable' in lines[i-1]:
            start_idx = i-1
            if '@OptIn' in lines[i-2]:
                start_idx = i-2
        break

if start_idx != -1:
    end_idx = start_idx
    brace_count = 0
    found_open = False
    for i in range(start_idx, len(lines)):
        line = lines[i]
        if '{' in line:
            brace_count += line.count('{')
            found_open = True
        if '}' in line:
            brace_count -= line.count('}')
        if found_open and brace_count == 0:
            end_idx = i
            break
    
    print(f"Replacing from line {start_idx} to {end_idx}")
    
    new_screen = '''@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    currentApiKey: String,
    currentModel: String,
    isWakeWordMode: Boolean,
    sharedPrefs: SharedPreferences,
    onDismiss: () -> Unit,
    onSave: (String, String, Boolean) -> Unit
) {
    var apiKeyInput by remember { mutableStateOf(currentApiKey) }
    var selectedModel by remember { mutableStateOf(currentModel) }
    var wakeWordInput by remember { mutableStateOf(isWakeWordMode) }
    
    var currentTab by remember { mutableStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(JasicaDarkGray)
            .padding(16.dp)
            .statusBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Outlined.Close, contentDescription = "Close", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Settings",
                        color = Color.White,
                        fontFamily = InterFontFamily,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Button(
                    onClick = { onSave(apiKeyInput, selectedModel, wakeWordInput) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B00)),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("SAVE", color = Color.White, fontFamily = InterFontFamily, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(16.dp))

            TabRow(
                selectedTabIndex = currentTab,
                containerColor = Color.Transparent,
                contentColor = JasicaOrange,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[currentTab]),
                        color = JasicaOrange
                    )
                }
            ) {
                Tab(selected = currentTab == 0, onClick = { currentTab = 0 }, text = { Text("AI", color = if (currentTab == 0) JasicaOrange else Color.White) })
                Tab(selected = currentTab == 1, onClick = { currentTab = 1 }, text = { Text("Devices", color = if (currentTab == 1) JasicaOrange else Color.White) })
            }

            Spacer(Modifier.height(16.dp))
            
            Box(modifier = Modifier.weight(1f)) {
                if (currentTab == 0) {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        Text("API KEY", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp, fontFamily = InterFontFamily)
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = apiKeyInput,
                            onValueChange = { apiKeyInput = it },
                            singleLine = true,
                            textStyle = TextStyle(color = Color.White, fontFamily = InterFontFamily, fontSize = 14.sp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.4f),
                                cursorColor = Color.White
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(24.dp))

                        Text("AI MODEL", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp, fontFamily = InterFontFamily)
                        Spacer(Modifier.height(8.dp))

                        AiModelsList.forEach { modelName ->
                            val isSelected = selectedModel == modelName
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) Color.White.copy(alpha = 0.2f) else Color.Transparent)
                                    .border(1.dp, if (isSelected) Color.White else Color.White.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                    .clickable { selectedModel = modelName }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(if (isSelected) Color.White else Color.Transparent))
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    text = modelName,
                                    color = Color.White,
                                    fontFamily = InterFontFamily,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }

                        Spacer(Modifier.height(20.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.1f))
                                .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                .clickable { wakeWordInput = !wakeWordInput }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Hands-Free Wake Word", color = Color.White, fontFamily = InterFontFamily, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Switch(
                                checked = wakeWordInput,
                                onCheckedChange = { wakeWordInput = it },
                                colors = SwitchDefaults.colors(checkedTrackColor = Color.White, checkedThumbColor = JasicaPurple, uncheckedThumbColor = Color.LightGray)
                            )
                        }
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxHeight()) {
                        items(DEFAULT_DEVICES.size) { index ->
                            val dev = DEFAULT_DEVICES[index]
                            var name by remember { mutableStateOf(sharedPrefs.getString("DEV_${dev.id}_NAME", dev.defaultName) ?: dev.defaultName) }
                            var onCmd by remember { mutableStateOf(sharedPrefs.getString("DEV_${dev.id}_ON_CMD", dev.defaultOnCmd) ?: dev.defaultOnCmd) }
                            var offCmd by remember { mutableStateOf(sharedPrefs.getString("DEV_${dev.id}_OFF_CMD", dev.defaultOffCmd) ?: dev.defaultOffCmd) }
                            var pinOn by remember { mutableStateOf(sharedPrefs.getString("DEV_${dev.id}_PIN_ON", dev.defaultPinOn) ?: dev.defaultPinOn) }
                            var pinOff by remember { mutableStateOf(sharedPrefs.getString("DEV_${dev.id}_PIN_OFF", dev.defaultPinOff) ?: dev.defaultPinOff) }

                            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).clip(RoundedCornerShape(12.dp)).background(Color.White.copy(alpha = 0.05f)).padding(12.dp)) {
                                OutlinedTextField(value = name, onValueChange = { name = it; sharedPrefs.edit().putString("DEV_${dev.id}_NAME", it).apply() }, label = { Text("Device Name", color = Color.White.copy(0.7f)) }, textStyle = TextStyle(color = Color.White), modifier = Modifier.fillMaxWidth())
                                Spacer(Modifier.height(8.dp))
                                OutlinedTextField(value = onCmd, onValueChange = { onCmd = it; sharedPrefs.edit().putString("DEV_${dev.id}_ON_CMD", it).apply() }, label = { Text("Turn On Command", color = Color.White.copy(0.7f)) }, textStyle = TextStyle(color = Color.White), modifier = Modifier.fillMaxWidth())
                                Spacer(Modifier.height(8.dp))
                                OutlinedTextField(value = offCmd, onValueChange = { offCmd = it; sharedPrefs.edit().putString("DEV_${dev.id}_OFF_CMD", it).apply() }, label = { Text("Turn Off Command", color = Color.White.copy(0.7f)) }, textStyle = TextStyle(color = Color.White), modifier = Modifier.fillMaxWidth())
                                Spacer(Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(value = pinOn, onValueChange = { pinOn = it; sharedPrefs.edit().putString("DEV_${dev.id}_PIN_ON", it).apply() }, label = { Text("ON Pin (MCU)", color = Color.White.copy(0.7f)) }, textStyle = TextStyle(color = Color.White), modifier = Modifier.weight(1f))
                                    OutlinedTextField(value = pinOff, onValueChange = { pinOff = it; sharedPrefs.edit().putString("DEV_${dev.id}_PIN_OFF", it).apply() }, label = { Text("OFF Pin (MCU)", color = Color.White.copy(0.7f)) }, textStyle = TextStyle(color = Color.White), modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
'''
    
    lines[start_idx:end_idx+1] = [new_screen + '\n']
    
    content = "".join(lines)
    content = content.replace('SettingsDialog(', 'SettingsScreen(')
    
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)
else:
    print("SettingsDialog not found in lines.")

import re

file_path = r'e:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# JasicaDashboardContent update
old_matrix = '''        val matrixDevices = listOf(
            Pair("a", "PC Hub"), Pair("b", "RGB"), Pair("c", "Room"),
            Pair("d", "Plug"), Pair("e", "Fan"), Pair("f", "AC Unit")
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White.copy(alpha = 0.05f))
                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                matrixDevices.chunked(3).forEach { row ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        row.forEach { (id, name) ->
                            val isOn = deviceStates[id] == true
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(if (isOn) Color(0xFF00E676) else Color.DarkGray)
                                        .blur(if (isOn) 2.dp else 0.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(name, color = if (isOn) Color.White else Color.White.copy(alpha = 0.5f), fontSize = 13.sp, fontFamily = InterFontFamily)
                            }
                        }
                    }
                }
            }
        }'''

new_matrix = '''        // Read dynamic devices for matrix
        val sharedPrefs = LocalContext.current.getSharedPreferences("JasicaSettings", Context.MODE_PRIVATE)
        val matrixDevices = DEFAULT_DEVICES.map { dev ->
            val name = sharedPrefs.getString("DEV__NAME", dev.defaultName) ?: dev.defaultName
            val pinOn = sharedPrefs.getString("DEV__PIN_ON", dev.defaultPinOn) ?: dev.defaultPinOn
            val pinOff = sharedPrefs.getString("DEV__PIN_OFF", dev.defaultPinOff) ?: dev.defaultPinOff
            Triple(dev.id, name, Pair(pinOn, pinOff))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White.copy(alpha = 0.05f))
                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                matrixDevices.chunked(3).forEach { row ->
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        row.forEach { (devId, name, pins) ->
                            val (pinOnCmd, pinOffCmd) = pins
                            val isOn = deviceStates[devId] == true
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {
                                onSendRawCommand(if (isOn) pinOffCmd else pinOnCmd)
                            }) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(if (isOn) Color(0xFF00E676) else Color.DarkGray)
                                        .blur(if (isOn) 2.dp else 0.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(name, color = if (isOn) Color.White else Color.White.copy(alpha = 0.5f), fontSize = 13.sp, fontFamily = InterFontFamily)
                            }
                        }
                    }
                }
            }
        }'''

content = content.replace(old_matrix, new_matrix)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

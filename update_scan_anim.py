import re

file_path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

# Replace Header scanning animation
header_old = """                    if (isScanning) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color(0xFF007AFF),
                            strokeWidth = 2.5.dp
                        )
                    }"""

header_new = """                    if (isScanning) {
                        val infiniteTransition = rememberInfiniteTransition(label = "radar")
                        val scale by infiniteTransition.animateFloat(
                            initialValue = 0.2f,
                            targetValue = 2.5f,
                            animationSpec = infiniteRepeatable(tween(1200, easing = LinearOutSlowInEasing), RepeatMode.Restart),
                            label = "scale"
                        )
                        val alpha by infiniteTransition.animateFloat(
                            initialValue = 1f,
                            targetValue = 0f,
                            animationSpec = infiniteRepeatable(tween(1200, easing = LinearOutSlowInEasing), RepeatMode.Restart),
                            label = "alpha"
                        )
                        Box(modifier = Modifier.size(28.dp), contentAlignment = Alignment.Center) {
                            Icon(Icons.Outlined.Bluetooth, contentDescription = null, tint = Color(0xFF007AFF), modifier = Modifier.size(16.dp))
                            Box(modifier = Modifier
                                .matchParentSize()
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                    this.alpha = alpha
                                }
                                .border(1.5.dp, Color(0xFF007AFF), CircleShape)
                            )
                        }
                    }"""

content = content.replace(header_old, header_new)

# Replace "OTHER DEVICES" section logic
list_old = """                    if (availableDevices.isEmpty() && !isScanning) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp), contentAlignment = Alignment.Center) {
                                Text("No devices found.", color = Color.Black.copy(alpha = 0.4f), fontSize = 15.sp, fontFamily = InterFontFamily)
                            }
                        }
                    } else if (availableDevices.isNotEmpty()) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.White)
                            ) {
                                availableDevices.forEachIndexed { index, device ->
                                    val name = try { device.name ?: "Unknown Signal" } catch (e: SecurityException) { "Unknown Signal" }
                                    DeviceListItem(name, device.address, device.address == connectedDeviceAddress) { onDeviceSelect(device) }
                                    if (index < availableDevices.size - 1) {
                                        HorizontalDivider(modifier = Modifier.padding(start = 16.dp), color = Color.Black.copy(alpha = 0.05f), thickness = 1.dp)
                                    }
                                }
                            }
                        }
                    }"""

list_new = """                    if (availableDevices.isEmpty() && !isScanning) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp), contentAlignment = Alignment.Center) {
                                Text("No devices found.", color = Color.Black.copy(alpha = 0.4f), fontSize = 15.sp, fontFamily = InterFontFamily)
                            }
                        }
                    } else if (availableDevices.isNotEmpty() || isScanning) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.White)
                            ) {
                                availableDevices.forEachIndexed { index, device ->
                                    val name = try { device.name ?: "Unknown Signal" } catch (e: SecurityException) { "Unknown Signal" }
                                    DeviceListItem(name, device.address, device.address == connectedDeviceAddress) { onDeviceSelect(device) }
                                    if (index < availableDevices.size - 1 || isScanning) {
                                        HorizontalDivider(modifier = Modifier.padding(start = 16.dp), color = Color.Black.copy(alpha = 0.05f), thickness = 1.dp)
                                    }
                                }
                                
                                if (isScanning) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        val infiniteTransition = rememberInfiniteTransition(label="scan_text")
                                        val alpha by infiniteTransition.animateFloat(
                                            initialValue = 0.3f,
                                            targetValue = 1f,
                                            animationSpec = infiniteRepeatable(
                                                animation = tween(800, easing = LinearEasing),
                                                repeatMode = RepeatMode.Reverse
                                            ),
                                            label="alpha"
                                        )
                                        Text(
                                            "Searching...",
                                            color = Color.Black.copy(alpha = alpha),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Medium,
                                            fontFamily = InterFontFamily,
                                            modifier = Modifier.weight(1f)
                                        )
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(18.dp),
                                            color = Color.Black.copy(alpha=0.3f),
                                            strokeWidth = 2.dp
                                        )
                                    }
                                }
                            }
                        }
                    }"""

content = content.replace(list_old, list_new)

with open(file_path, "w", encoding="utf-8") as f:
    f.write(content)

print("Updated scanning animations.")

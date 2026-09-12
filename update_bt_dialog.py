import re

FILE = r'E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt'
with open(FILE, 'r', encoding='utf-8') as f:
    content = f.read()

new_ui = """fun DeviceSelectionDialog(
    pairedDevices: List<BluetoothDevice>,
    availableDevices: List<BluetoothDevice>,
    isScanning: Boolean,
    hazeState: dev.chrisbanes.haze.HazeState?,
    onDeviceSelect: (BluetoothDevice) -> Unit,
    onScanTap: () -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(
                if (hazeState != null) Modifier.hazeEffect(
                    state = hazeState,
                    style = dev.chrisbanes.haze.HazeStyle(
                        blurRadius = 24.dp,
                        tint = dev.chrisbanes.haze.HazeTint(Color.Black.copy(alpha=0.6f))
                    )
                ) else Modifier.background(Color.Black.copy(alpha = 0.6f))
            )
            .pointerInput(Unit) { detectTapGestures(onTap = { onDismiss() }) },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
                .pointerInput(Unit) { detectTapGestures { /* consume */ } }
        ) {
            JasicaGraphicalDialogPanel(hazeState = null) {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 8.dp)) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Bluetooth Devices",
                            color = Color.White,
                            fontFamily = InterFontFamily,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (isScanning) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Color(0xFF34C759),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Button(
                                onClick = onScanTap,
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                                modifier = Modifier.height(32.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha=0.15f))
                            ) {
                                Text("SCAN", color = Color.White, fontFamily = InterFontFamily, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    LazyColumn(
                        modifier = Modifier.heightIn(max = 400.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (pairedDevices.isNotEmpty()) {
                            item {
                                Text(
                                    "PAIRED DEVICES",
                                    color = Color.White.copy(alpha = 0.5f),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = InterFontFamily,
                                    modifier = Modifier.padding(bottom = 4.dp, top = 4.dp)
                                )
                            }
                            items(pairedDevices, key = { "paired_" + it.address }) { device ->
                                val name = try { device.name ?: "Unknown Device" } catch (e: SecurityException) { "Unknown Device" }
                                DeviceListItem(name, device.address, true) { onDeviceSelect(device) }
                            }
                        }

                        item {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "AVAILABLE DEVICES",
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = InterFontFamily,
                                modifier = Modifier.padding(bottom = 4.dp, top = 4.dp)
                            )
                        }

                        if (availableDevices.isEmpty() && !isScanning) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp), contentAlignment = Alignment.Center) {
                                    Text("No devices found.", color = Color.White.copy(alpha=0.4f), fontSize = 14.sp, fontFamily = InterFontFamily)
                                }
                            }
                        } else {
                            items(availableDevices, key = { "avail_" + it.address }) { device ->
                                val name = try { device.name ?: "Unknown Signal" } catch (e: SecurityException) { "Unknown Signal" }
                                DeviceListItem(name, device.address, false) { onDeviceSelect(device) }
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    // Close Button
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f))
                    ) {
                        Text("Close", color = Color.White, fontFamily = InterFontFamily, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
fun DeviceListItem(name: String, address: String, isPaired: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.08f))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Bluetooth,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = InterFontFamily,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            Text(
                text = address,
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 12.sp,
                fontFamily = InterFontFamily
            )
        }
        if (isPaired) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = null,
                tint = Color(0xFF34C759).copy(alpha = 0.8f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}"""

match = re.search(r'fun DeviceSelectionDialog\(pairedDevices.*?fun DeviceListItem.*?\}\n\}', content, re.DOTALL)
if match:
    content = content[:match.start()] + new_ui + content[match.end():]
    with open(FILE, 'w', encoding='utf-8', newline='') as f:
        f.write(content)
    print("DeviceSelectionDialog and DeviceListItem updated successfully!")
else:
    print("Regex failed to match.")

import re
import os

file_path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

# 1. Add connectedDeviceAddress to MainActivity
var_decl = "    private val connectedDeviceName = mutableStateOf<String?>(null)\n    private val connectedDeviceAddress = mutableStateOf<String?>(null)"
content = content.replace("    private val connectedDeviceName = mutableStateOf<String?>(null)", var_decl)

# 2. Update all places where connectedDeviceName.value = ...
content = content.replace(
    "connectedDeviceName.value = null\n",
    "connectedDeviceName.value = null\n                            connectedDeviceAddress.value = null\n"
)
content = content.replace(
    'connectedDeviceName.value = try { device.name ?: "BLE Device" } catch (e: SecurityException) { "BLE Device" }',
    'connectedDeviceName.value = try { device.name ?: "BLE Device" } catch (e: SecurityException) { "BLE Device" }\n                            connectedDeviceAddress.value = device.address'
)
content = content.replace(
    'connectedDeviceName.value = try { device.name ?: "BT Device" } catch (e: SecurityException) { "BT Device" }',
    'connectedDeviceName.value = try { device.name ?: "BT Device" } catch (e: SecurityException) { "BT Device" }\n                    connectedDeviceAddress.value = device.address'
)
content = content.replace(
    'connectedDeviceName = null,',
    'connectedDeviceName = null,\n            connectedDeviceAddress = null,'
)

# 3. Add to JasicaScreen signature
content = content.replace(
    "    connectedDeviceName : String?,",
    "    connectedDeviceName : String?,\n    connectedDeviceAddress : String?,"
)

# 4. Add to JasicaScreen call
content = content.replace(
    "                connectedDeviceName = connectedDeviceName.value,",
    "                connectedDeviceName = connectedDeviceName.value,\n                connectedDeviceAddress = connectedDeviceAddress.value,"
)

# 5. Add to JasicaScreen parameters for DeviceSelectionDialog
content = content.replace(
    "DeviceSelectionDialog(pairedDevices, availableDevices, isScanning, hazeState, onDeviceSelect, onScanTap, onDismissDialog)",
    "DeviceSelectionDialog(pairedDevices, availableDevices, isScanning, connectedDeviceAddress, hazeState, onDeviceSelect, onScanTap, onDismissDialog)"
)

# 6. Replace DeviceSelectionDialog and DeviceListItem implementations
old_dialog_pattern = re.compile(r'@SuppressLint\("MissingPermission"\)\n@Composable\nfun DeviceSelectionDialog\(.*?fun DeviceListItem\(.*?\}\n\}\n', re.DOTALL)

new_dialog_code = """@SuppressLint("MissingPermission")
@Composable
fun DeviceSelectionDialog(
    pairedDevices: List<BluetoothDevice>,
    availableDevices: List<BluetoothDevice>,
    isScanning: Boolean,
    connectedDeviceAddress: String?,
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
                        tint = dev.chrisbanes.haze.HazeTint(Color.Black.copy(alpha=0.4f))
                    )
                ) else Modifier.background(Color.Black.copy(alpha = 0.5f))
            )
            .pointerInput(Unit) { detectTapGestures(onTap = { onDismiss() }) },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF1C1C1E).copy(alpha = 0.95f))
                .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(24.dp))
                .pointerInput(Unit) { detectTapGestures { /* consume taps */ } }
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(top = 20.dp, bottom = 16.dp, start = 20.dp, end = 20.dp)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Bluetooth",
                        color = Color.White,
                        fontFamily = InterFontFamily,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp
                    )
                    if (isScanning) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White.copy(alpha = 0.15f))
                                .clickable { onScanTap() }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text("Scan", color = Color.White, fontFamily = InterFontFamily, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                LazyColumn(
                    modifier = Modifier.heightIn(max = 420.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (pairedDevices.isNotEmpty()) {
                        item {
                            Text(
                                "MY DEVICES",
                                color = Color.White.copy(alpha = 0.5f),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                fontFamily = InterFontFamily,
                                modifier = Modifier.padding(bottom = 6.dp, start = 8.dp)
                            )
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.White.copy(alpha = 0.05f))
                            ) {
                                pairedDevices.forEachIndexed { index, device ->
                                    val name = try { device.name ?: "Unknown Device" } catch (e: SecurityException) { "Unknown Device" }
                                    DeviceListItem(name, device.address, device.address == connectedDeviceAddress) { onDeviceSelect(device) }
                                    if (index < pairedDevices.size - 1) {
                                        HorizontalDivider(modifier = Modifier.padding(start = 56.dp), color = Color.White.copy(alpha = 0.05f), thickness = 1.dp)
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            "OTHER DEVICES",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = InterFontFamily,
                            modifier = Modifier.padding(bottom = 6.dp, start = 8.dp, top = if (pairedDevices.isEmpty()) 0.dp else 8.dp)
                        )
                    }

                    if (availableDevices.isEmpty() && !isScanning) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp), contentAlignment = Alignment.Center) {
                                Text("No devices found.", color = Color.White.copy(alpha = 0.4f), fontSize = 15.sp, fontFamily = InterFontFamily)
                            }
                        }
                    } else if (availableDevices.isNotEmpty()) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.White.copy(alpha = 0.05f))
                            ) {
                                availableDevices.forEachIndexed { index, device ->
                                    val name = try { device.name ?: "Unknown Signal" } catch (e: SecurityException) { "Unknown Signal" }
                                    DeviceListItem(name, device.address, device.address == connectedDeviceAddress) { onDeviceSelect(device) }
                                    if (index < availableDevices.size - 1) {
                                        HorizontalDivider(modifier = Modifier.padding(start = 56.dp), color = Color.White.copy(alpha = 0.05f), thickness = 1.dp)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Close Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = 0.1f))
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Text("Close", color = Color.White, fontFamily = InterFontFamily, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun DeviceListItem(name: String, address: String, isConnected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = InterFontFamily,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }
        if (isConnected) {
            Text(
                text = "Connected",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 15.sp,
                fontFamily = InterFontFamily,
                modifier = Modifier.padding(end = 8.dp)
            )
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = null,
                tint = Color(0xFF0A84FF),
                modifier = Modifier.size(18.dp)
            )
        } else {
            Text(
                text = "Not Connected",
                color = Color.White.copy(alpha = 0.3f),
                fontSize = 15.sp,
                fontFamily = InterFontFamily
            )
        }
    }
}
"""

content = old_dialog_pattern.sub(new_dialog_code, content)

with open(file_path, "w", encoding="utf-8") as f:
    f.write(content)

print("Updated device dialog design.")

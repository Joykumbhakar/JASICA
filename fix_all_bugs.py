import re

with open(r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt", "r", encoding="utf-8") as f:
    text = f.read()

changes = 0

# 1. Fix deprecated Icons.Outlined.List -> Icons.AutoMirrored.Outlined.List
text = text.replace("icon = Icons.Outlined.List,", "icon = Icons.AutoMirrored.Outlined.List,")
changes += 1

# 2. Fix deprecated Locale constructor -> Locale.forLanguageTag  
text = text.replace('tts.language = java.util.Locale("bn", "IN")', 'tts.language = java.util.Locale.forLanguageTag("bn-IN")')
changes += 1

# 3. Fix deprecated getParcelableExtra -> version-safe wrapper
# Replace the two usages with safe version
text = text.replace(
    'addDevice(intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE))',
    'addDevice(if (android.os.Build.VERSION.SDK_INT >= 33) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java) else @Suppress("DEPRECATION") intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE))'
)
text = text.replace(
    'val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)',
    'val device = if (android.os.Build.VERSION.SDK_INT >= 33) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java) else @Suppress("DEPRECATION") intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)'
)
changes += 1

# 4. Fix dangerous !! on pendingDevice (can NPE in a race condition)
text = text.replace(
    '''if (pendingDevice?.address == device.address) {
                                val devToConnect = pendingDevice!!
                                pendingDevice = null
                                proceedWithConnection(devToConnect)
                            }
                        } else if (state == BluetoothDevice.BOND_NONE && device != null) {
                            if (pendingDevice?.address == device.address) {
                                val devToConnect = pendingDevice!!
                                pendingDevice = null
                                proceedWithConnection(devToConnect)
                            }''',
    '''if (pendingDevice?.address == device.address) {
                                val devToConnect = pendingDevice ?: return
                                pendingDevice = null
                                proceedWithConnection(devToConnect)
                            }
                        } else if (state == BluetoothDevice.BOND_NONE && device != null) {
                            if (pendingDevice?.address == device.address) {
                                val devToConnect = pendingDevice ?: return
                                pendingDevice = null
                                proceedWithConnection(devToConnect)
                            }'''
)
changes += 1

# 5. Replace remaining Lucide icon CALLS (not definitions) with Fluent equivalents:

# 5a. LucideRotateCcw in Settings Saved dialog (line ~4330)
text = text.replace(
    'LucideRotateCcw(tint = Color(0xFF007AFF), modifier = Modifier.size(22.dp))',
    'Image(painterResource(id = R.drawable.fluentui_system_icons_arrow_clockwise_dashes), contentDescription = null, modifier = Modifier.size(22.dp))'
)
changes += 1

# 5b. LucideChevronUp / LucideChevronDown in device editor (lines ~4862/4864)
text = text.replace(
    'LucideChevronUp(tint = Color(0xFF007AFF))',
    'Icon(Icons.Filled.KeyboardArrowUp, contentDescription = null, tint = Color(0xFF007AFF))'
)
text = text.replace(
    'LucideChevronDown(tint = textSecondary)',
    'Icon(Icons.Filled.KeyboardArrowDown, contentDescription = null, tint = textSecondary)'
)
changes += 1

# 6. Fix BLE write - add try-catch for general Exception (not just SecurityException)
text = text.replace(
    'try { bleWriteChar?.value = payload; bluetoothGatt?.writeCharacteristic(bleWriteChar) } catch (e: SecurityException) {}',
    'try { bleWriteChar?.value = payload; bluetoothGatt?.writeCharacteristic(bleWriteChar) } catch (e: Exception) { Log.e("BLE", "Write failed: ${e.message}") }'
)
changes += 1

# 7. Make sure import for Icons.AutoMirrored exists
if "import androidx.compose.material.icons.automirrored" not in text.lower():
    text = text.replace(
        "import androidx.compose.material.icons.outlined.Search",
        "import androidx.compose.material.icons.outlined.Search\nimport androidx.compose.material.icons.automirrored.outlined.List"
    )
    # If the above didn't match, try a different import location
    if "automirrored" not in text.lower():
        text = text.replace(
            "import androidx.compose.material.icons.Icons",
            "import androidx.compose.material.icons.Icons\nimport androidx.compose.material.icons.automirrored.outlined.List"
        )
changes += 1

print(f"Applied {changes} fix categories.")

with open(r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt", "w", encoding="utf-8") as f:
    f.write(text)

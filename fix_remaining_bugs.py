import re

with open(r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt", "r", encoding="utf-8") as f:
    text = f.read()

fixes = 0

# 1. Fix second deprecated Locale("bn", "IN") at line ~2166
old_locale = 'tts.language = java.util.Locale("bn", "IN")'
new_locale = 'tts.language = java.util.Locale.forLanguageTag("bn-IN")'
count = text.count(old_locale)
text = text.replace(old_locale, new_locale)
fixes += count
print(f"  Fixed {count} remaining deprecated Locale constructors")

# 2. Fix classicSocket!! race condition (line ~2386)
text = text.replace(
    'if (classicSocket != null && classicSocket!!.isConnected)',
    'if (classicSocket?.isConnected == true)'
)
fixes += 1
print("  Fixed classicSocket!! null crash")

# 3. Add stopScans() and handler cleanup to onDestroy()
# Find the onDestroy method and add stopScans + handler cleanup
old_ondestroy_pattern = 'discoveryReceiver?.let { try { unregisterReceiver(it) } catch (e: Exception) {} }'
new_ondestroy = 'discoveryReceiver?.let { try { unregisterReceiver(it) } catch (e: Exception) {} }\n        try { stopScans() } catch (e: Exception) {}\n        mainHandler.removeCallbacksAndMessages(null)'
if 'stopScans()' not in text.split('onDestroy')[1].split('override fun')[0] if 'onDestroy' in text else '':
    text = text.replace(old_ondestroy_pattern, new_ondestroy, 1)
    fixes += 1
    print("  Added stopScans() and handler cleanup to onDestroy()")

# 4. Close streams in disconnectAll()
old_disconnect = 'if (classicSocket != null) { try { classicSocket?.close() } catch (e: IOException) {}; classicSocket = null }'
new_disconnect = 'if (classicOutStream != null) { try { classicOutStream?.close() } catch (e: IOException) {}; classicOutStream = null }\n        if (classicInStream != null) { try { classicInStream?.close() } catch (e: IOException) {}; classicInStream = null }\n        if (classicSocket != null) { try { classicSocket?.close() } catch (e: IOException) {}; classicSocket = null }'
if 'classicOutStream?.close()' not in text:
    text = text.replace(old_disconnect, new_disconnect, 1)
    fixes += 1
    print("  Added stream cleanup to disconnectAll()")

# 5. Add key to LazyColumn items in HistorySheet
text = text.replace(
    'items(history) { message ->',
    'items(history, key = { it.hashCode() }) { message ->'
)
fixes += 1
print("  Added key to HistorySheet LazyColumn")

# 6. Add key to DeviceDialog items
text = text.replace(
    'items(pairedDevices) { device -> DeviceListItem(device.name ?: "Unknown Device", device.address)',
    'items(pairedDevices, key = { it.address }) { device -> DeviceListItem(try { device.name ?: "Unknown Device" } catch (e: SecurityException) { "Unknown Device" }, device.address)'
)
text = text.replace(
    'items(availableDevices) { device -> DeviceListItem(device.name ?: "Unknown Signal", device.address)',
    'items(availableDevices, key = { it.address }) { device -> DeviceListItem(try { device.name ?: "Unknown Signal" } catch (e: SecurityException) { "Unknown Signal" }, device.address)'
)
fixes += 1
print("  Added keys and SecurityException guards to DeviceDialog LazyColumn")

# 7. Fix remember without keys for device config (add dev.id as key)
text = text.replace(
    'var name by remember { mutableStateOf(sharedPrefs.getString("DEV_${dev.id}_NAME"',
    'var name by remember(dev.id) { mutableStateOf(sharedPrefs.getString("DEV_${dev.id}_NAME"'
)
text = text.replace(
    'var onCmd by remember { mutableStateOf(sharedPrefs.getString("DEV_${dev.id}_ON_CMD"',
    'var onCmd by remember(dev.id) { mutableStateOf(sharedPrefs.getString("DEV_${dev.id}_ON_CMD"'
)
text = text.replace(
    'var offCmd by remember { mutableStateOf(sharedPrefs.getString("DEV_${dev.id}_OFF_CMD"',
    'var offCmd by remember(dev.id) { mutableStateOf(sharedPrefs.getString("DEV_${dev.id}_OFF_CMD"'
)
text = text.replace(
    'var pinOn by remember { mutableStateOf(sharedPrefs.getString("DEV_${dev.id}_PIN_ON"',
    'var pinOn by remember(dev.id) { mutableStateOf(sharedPrefs.getString("DEV_${dev.id}_PIN_ON"'
)
text = text.replace(
    'var pinOff by remember { mutableStateOf(sharedPrefs.getString("DEV_${dev.id}_PIN_OFF"',
    'var pinOff by remember(dev.id) { mutableStateOf(sharedPrefs.getString("DEV_${dev.id}_PIN_OFF"'
)
fixes += 1
print("  Added proper keys to remember{} blocks in device config")

# 8. Wrap device.type calls in try-catch for SecurityException
text = text.replace(
    'if (device.type == BluetoothDevice.DEVICE_TYPE_LE) connectBLE(device) else connectClassic(device)',
    'try { if (device.type == BluetoothDevice.DEVICE_TYPE_LE) connectBLE(device) else connectClassic(device) } catch (e: SecurityException) { connectClassic(device) }'
)
fixes += 1
print("  Added try-catch around device.type calls")

# 9. Wrap device.name in BLE/classic callbacks with try-catch
text = text.replace(
    'connectedDeviceName.value = device.name ?: "BLE Device"',
    'connectedDeviceName.value = try { device.name ?: "BLE Device" } catch (e: SecurityException) { "BLE Device" }'
)
text = text.replace(
    'connectedDeviceName.value = device.name ?: "BT Device"',
    'connectedDeviceName.value = try { device.name ?: "BT Device" } catch (e: SecurityException) { "BT Device" }'
)
fixes += 1
print("  Added SecurityException guards to device.name calls")

print(f"\nTotal: {fixes} fix categories applied.")

with open(r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt", "w", encoding="utf-8") as f:
    f.write(text)

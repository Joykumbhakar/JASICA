import os

def patch_service(path, is_floating=False):
    with open(path, "r", encoding="utf-8") as f:
        text = f.read()

    target = """        val intent = Intent("com.bristi.controller.SEND_QUICK_COMMAND").apply {
            putExtra("device_index", deviceIndex)
        }
        sendBroadcast(intent)"""

    if is_floating:
        target = """    private fun sendCommandBroadcast(deviceIndex: Int) {
        val intent = Intent("com.bristi.controller.SEND_QUICK_COMMAND")
        intent.putExtra("device_index", deviceIndex)
        sendBroadcast(intent)
    }"""
    
    replacement = """    private fun sendCommandBroadcast(deviceIndex: Int) {
        val prefs = getSharedPreferences("JasicaSettings", Context.MODE_PRIVATE)
        val devId = "dev$deviceIndex"
        val isOn = JasicaBluetoothManager.deviceStates[devId] ?: prefs.getBoolean("DEV_$devId", false)

        val defaultPinOn = when(deviceIndex) { 1->"A"; 2->"B"; 3->"C"; 4->"D"; 5->"E"; 6->"F"; else->"A" }
        val defaultPinOff = when(deviceIndex) { 1->"a"; 2->"b"; 3->"c"; 4->"d"; 5->"e"; 6->"f"; else->"a" }

        val pinOn = prefs.getString("DEV_${devId}_PIN_ON", defaultPinOn) ?: defaultPinOn
        val pinOff = prefs.getString("DEV_${devId}_PIN_OFF", defaultPinOff) ?: defaultPinOff

        val command = if (isOn) pinOff else pinOn
        JasicaBluetoothManager.sendRawCommand(command)

        // Update state optimistically
        JasicaBluetoothManager.deviceStates[devId] = !isOn
        prefs.edit().putBoolean("DEV_$devId", !isOn).apply()
    }"""
    
    if not is_floating:
        replacement = """        val devId = "dev$deviceIndex"
        val isOn = JasicaBluetoothManager.deviceStates[devId] ?: prefs.getBoolean("DEV_$devId", false)

        val defaultPinOn = when(deviceIndex) { 1->"A"; 2->"B"; 3->"C"; 4->"D"; 5->"E"; 6->"F"; else->"A" }
        val defaultPinOff = when(deviceIndex) { 1->"a"; 2->"b"; 3->"c"; 4->"d"; 5->"e"; 6->"f"; else->"a" }

        val pinOn = prefs.getString("DEV_${devId}_PIN_ON", defaultPinOn) ?: defaultPinOn
        val pinOff = prefs.getString("DEV_${devId}_PIN_OFF", defaultPinOff) ?: defaultPinOff

        val command = if (isOn) pinOff else pinOn
        JasicaBluetoothManager.sendRawCommand(command)

        // Update state optimistically
        JasicaBluetoothManager.deviceStates[devId] = !isOn
        prefs.edit().putBoolean("DEV_$devId", !isOn).apply()"""

    text = text.replace(target, replacement)
    
    with open(path, "w", encoding="utf-8") as f:
        f.write(text)

patch_service(r"E:\Controller\app\src\main\java\com\bristi\controller\DoubleTapService.kt")
patch_service(r"E:\Controller\app\src\main\java\com\bristi\controller\FloatingControlService.kt", is_floating=True)

print("Patched both background services")

import re

path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

# Add imports
imports = """
import com.bristi.controller.JasicaBluetoothManager.classicSocket
import com.bristi.controller.JasicaBluetoothManager.classicOutStream
import com.bristi.controller.JasicaBluetoothManager.classicInStream
import com.bristi.controller.JasicaBluetoothManager.isClassicConnected
import com.bristi.controller.JasicaBluetoothManager.bluetoothGatt
import com.bristi.controller.JasicaBluetoothManager.bleWriteChar
import com.bristi.controller.JasicaBluetoothManager.isBleConnected
import com.bristi.controller.JasicaBluetoothManager.isBtConnected
import com.bristi.controller.JasicaBluetoothManager.connectedDeviceName
import com.bristi.controller.JasicaBluetoothManager.connectedDeviceAddress
import com.bristi.controller.JasicaBluetoothManager.deviceStates
"""
text = text.replace("import android.os.Bundle", imports.strip() + "\nimport android.os.Bundle")

# Remove declarations
text = re.sub(r"// Classic Connection State.*?\n.*?private var isClassicConnected = false\n", "", text, flags=re.DOTALL)
text = re.sub(r"// BLE Connection State.*?\n.*?private var isBleConnected = false\n", "", text, flags=re.DOTALL)
text = re.sub(r"private val isBtConnected\s*=\s*mutableStateOf\(false\)\n", "", text)
text = re.sub(r"private val connectedDeviceName\s*=\s*mutableStateOf<String\?>\(null\)\n", "", text)
text = re.sub(r"private val connectedDeviceAddress\s*=\s*mutableStateOf<String\?>\(null\)\n", "", text)
text = re.sub(r"private val deviceStates\s*=\s*mutableStateMapOf<String, Boolean>\(\)\n", "", text)

# Remove disconnectAll() from onDestroy
destroy_func = """    override fun onDestroy() {
        super.onDestroy()
        stopScans()
        mainHandler.removeCallbacksAndMessages(null)
        activeBlinkJob?.cancel()
        activeTimerJobs.values.forEach { it.cancel() }
        activeTimerJobs.clear()
        activeTimerEndTimes.clear()
        stopEverything()
        if (::tts.isInitialized) tts.shutdown()
        if (::speechRecognizer.isInitialized) speechRecognizer.destroy()
        disconnectAll()
        discoveryReceiver?.let { try { unregisterReceiver(it) } catch (e: Exception) {} }
        try { stopScans() } catch (e: Exception) {}
        mainHandler.removeCallbacksAndMessages(null)
    }"""
new_destroy = destroy_func.replace("disconnectAll()", "// disconnectAll() // Removed to allow background Bluetooth persistence")
text = text.replace(destroy_func, new_destroy)

with open(path, "w", encoding="utf-8") as f:
    f.write(text)

print("Patched MainActivity.kt")

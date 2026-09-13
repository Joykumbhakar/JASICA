import os

manager_code = """package com.bristi.controller

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothSocket
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import java.io.InputStream
import java.io.OutputStream
import java.io.IOException

object JasicaBluetoothManager {
    // Classic Connection State
    var classicSocket: BluetoothSocket? = null
    var classicOutStream: OutputStream? = null
    var classicInStream: InputStream? = null
    var isClassicConnected = false

    // BLE Connection State
    var bluetoothGatt: BluetoothGatt? = null
    var bleWriteChar: BluetoothGattCharacteristic? = null
    var isBleConnected = false

    // State
    var isBtConnected = mutableStateOf(false)
    var connectedDeviceName = mutableStateOf<String?>(null)
    var connectedDeviceAddress = mutableStateOf<String?>(null)
    
    // Global Device States
    val deviceStates = mutableStateMapOf<String, Boolean>()

    fun sendRawCommand(command: String) {
        val payload = "$command\\n".toByteArray()
        if (isClassicConnected && classicOutStream != null) {
            try {
                classicOutStream?.write(payload)
                classicOutStream?.flush()
            } catch (e: IOException) {
                // Ignore for now, connection dropped
            }
        } else if (isBleConnected && bluetoothGatt != null && bleWriteChar != null) {
            try {
                if (android.os.Build.VERSION.SDK_INT >= 33) {
                    bluetoothGatt?.writeCharacteristic(bleWriteChar!!, payload, android.bluetooth.BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT)
                } else {
                    @Suppress("DEPRECATION")
                    bluetoothGatt?.writeCharacteristic(bleWriteChar!!)
                }
            } catch (e: SecurityException) {
            }
        }
    }
}
"""

with open(r"E:\Controller\app\src\main\java\com\bristi\controller\JasicaBluetoothManager.kt", "w", encoding="utf-8") as f:
    f.write(manager_code)

print("Updated JasicaBluetoothManager.kt")

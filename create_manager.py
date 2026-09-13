import os

manager_code = """package com.bristi.controller

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothSocket
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import java.io.InputStream
import java.io.OutputStream

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
    
    // Global Device States so it can be updated by background services
    val deviceStates = mutableStateMapOf<String, Boolean>()
}
"""

with open(r"E:\Controller\app\src\main\java\com\bristi\controller\JasicaBluetoothManager.kt", "w", encoding="utf-8") as f:
    f.write(manager_code)

print("Created JasicaBluetoothManager.kt")

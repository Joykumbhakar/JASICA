package com.jasica.ai.controller.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

class AppBluetoothManager(private val context: Context) {

    private val TAG = "JasicaBluetoothManager"
    private val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        val systemBtManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
        systemBtManager?.adapter ?: BluetoothAdapter.getDefaultAdapter()
    }

    private var socket: BluetoothSocket? = null
    private var outputStream: OutputStream? = null
    private var inputStream: InputStream? = null

    private val scope = CoroutineScope(Dispatchers.IO)

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private val _connectedDeviceName = MutableStateFlow<String?>(null)
    val connectedDeviceName: StateFlow<String?> = _connectedDeviceName.asStateFlow()

    private val _isBluetoothEnabled = MutableStateFlow(bluetoothAdapter?.isEnabled == true)
    val isBluetoothEnabled: StateFlow<Boolean> = _isBluetoothEnabled.asStateFlow()

    private val _pairedDevices = MutableStateFlow<List<BluetoothDeviceItem>>(emptyList())
    val pairedDevices: StateFlow<List<BluetoothDeviceItem>> = _pairedDevices.asStateFlow()

    private val _discoveredDevices = MutableStateFlow<List<BluetoothDeviceItem>>(emptyList())
    val discoveredDevices: StateFlow<List<BluetoothDeviceItem>> = _discoveredDevices.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _incomingMessages = MutableSharedFlow<String>()
    val incomingMessages: SharedFlow<String> = _incomingMessages.asSharedFlow()

    private val _statusMessage = MutableStateFlow<String>("")
    val statusMessage: StateFlow<String> = _statusMessage.asStateFlow()

    private val discoveryReceiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    }
                    device?.let {
                        val name = it.name ?: "Unknown Device (${it.address})"
                        val item = BluetoothDeviceItem(name = name, address = it.address, isPaired = false)
                        val current = _discoveredDevices.value.toMutableList()
                        if (current.none { d -> d.address == item.address }) {
                            current.add(item)
                            _discoveredDevices.value = current
                        }
                    }
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    _isScanning.value = false
                }
                BluetoothAdapter.ACTION_STATE_CHANGED -> {
                    _isBluetoothEnabled.value = bluetoothAdapter?.isEnabled == true
                    if (_isBluetoothEnabled.value) {
                        refreshPairedDevices()
                    }
                }
            }
        }
    }

    init {
        val filter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
            addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        }
        context.registerReceiver(discoveryReceiver, filter)
        refreshPairedDevices()
    }

    fun updateBtState() {
        _isBluetoothEnabled.value = bluetoothAdapter?.isEnabled == true
        if (_isBluetoothEnabled.value) {
            refreshPairedDevices()
        }
    }

    @SuppressLint("MissingPermission")
    fun refreshPairedDevices() {
        if (bluetoothAdapter?.isEnabled != true) return
        val paired = bluetoothAdapter?.bondedDevices?.map { device ->
            BluetoothDeviceItem(
                name = device.name ?: "Unknown Device",
                address = device.address,
                isPaired = true
            )
        } ?: emptyList()
        _pairedDevices.value = paired
    }

    @SuppressLint("MissingPermission")
    fun startDiscovery() {
        if (bluetoothAdapter?.isEnabled != true) return
        if (bluetoothAdapter?.isDiscovering == true) {
            bluetoothAdapter?.cancelDiscovery()
        }
        _discoveredDevices.value = emptyList()
        _isScanning.value = true
        bluetoothAdapter?.startDiscovery()
    }

    @SuppressLint("MissingPermission")
    fun stopDiscovery() {
        if (bluetoothAdapter?.isDiscovering == true) {
            bluetoothAdapter?.cancelDiscovery()
        }
        _isScanning.value = false
    }

    @SuppressLint("MissingPermission")
    fun connectToDevice(address: String) {
        scope.launch {
            stopDiscovery()
            disconnect()

            _connectionState.value = ConnectionState.CONNECTING
            _statusMessage.value = "Connecting to device..."

            val device = try {
                bluetoothAdapter?.getRemoteDevice(address)
            } catch (e: Exception) {
                null
            }

            if (device == null) {
                _connectionState.value = ConnectionState.ERROR
                _statusMessage.value = "Device not found"
                return@launch
            }

            val deviceName = device.name ?: device.address

            try {
                // Attempt standard SPP connection
                socket = device.createRfcommSocketToServiceRecord(SPP_UUID)
                socket?.connect()
            } catch (e: Exception) {
                Log.w(TAG, "Standard SPP failed, trying reflection fallback: ${e.message}")
                try {
                    val m = device.javaClass.getMethod("createRfcommSocket", Int::class.javaPrimitiveType)
                    socket = m.invoke(device, 1) as BluetoothSocket
                    socket?.connect()
                } catch (fallbackEx: Exception) {
                    Log.e(TAG, "Connection failed: ${fallbackEx.message}")
                    withContext(Dispatchers.Main) {
                        _connectionState.value = ConnectionState.ERROR
                        _statusMessage.value = "Failed to connect: ${fallbackEx.localizedMessage}"
                    }
                    disconnect()
                    return@launch
                }
            }

            try {
                outputStream = socket?.outputStream
                inputStream = socket?.inputStream

                withContext(Dispatchers.Main) {
                    _connectedDeviceName.value = deviceName
                    _connectionState.value = ConnectionState.CONNECTED
                    _statusMessage.value = "Connected to $deviceName"
                }

                startListeningForData()

            } catch (e: IOException) {
                Log.e(TAG, "Streams init failed", e)
                withContext(Dispatchers.Main) {
                    _connectionState.value = ConnectionState.ERROR
                    _statusMessage.value = "Error initializing streams"
                }
                disconnect()
            }
        }
    }

    private fun startListeningForData() {
        scope.launch {
            val buffer = ByteArray(1024)
            val stringBuilder = StringBuilder()

            while (_connectionState.value == ConnectionState.CONNECTED && inputStream != null) {
                try {
                    val bytesRead = inputStream?.read(buffer) ?: -1
                    if (bytesRead > 0) {
                        val chunk = String(buffer, 0, bytesRead)
                        stringBuilder.append(chunk)

                        var newlineIdx: Int
                        while (stringBuilder.indexOf("\n").also { newlineIdx = it } != -1) {
                            val line = stringBuilder.substring(0, newlineIdx).trim()
                            stringBuilder.delete(0, newlineIdx + 1)
                            if (line.isNotEmpty()) {
                                _incomingMessages.emit(line)
                            }
                        }
                    } else if (bytesRead == -1) {
                        break
                    }
                } catch (e: IOException) {
                    Log.e(TAG, "Connection lost during read", e)
                    break
                }
            }

            withContext(Dispatchers.Main) {
                if (_connectionState.value == ConnectionState.CONNECTED) {
                    _connectionState.value = ConnectionState.DISCONNECTED
                    _connectedDeviceName.value = null
                    _statusMessage.value = "Device disconnected"
                }
            }
        }
    }

    fun sendCommand(cmd: String): Boolean {
        if (_connectionState.value != ConnectionState.CONNECTED || outputStream == null) {
            _statusMessage.value = "Cannot send: Not connected"
            return false
        }
        return try {
            val data = (cmd + "\n").toByteArray(Charsets.UTF_8)
            outputStream?.write(data)
            outputStream?.flush()
            true
        } catch (e: IOException) {
            Log.e(TAG, "Send command failed", e)
            _statusMessage.value = "Failed to send command"
            false
        }
    }

    fun sendChar(char: Char): Boolean {
        return sendCommand(char.toString())
    }

    fun disconnect() {
        try {
            outputStream?.close()
            inputStream?.close()
            socket?.close()
        } catch (e: Exception) {
            Log.e(TAG, "Error closing socket", e)
        } finally {
            socket = null
            outputStream = null
            inputStream = null
            _connectionState.value = ConnectionState.DISCONNECTED
            _connectedDeviceName.value = null
        }
    }

    fun cleanup() {
        disconnect()
        try {
            context.unregisterReceiver(discoveryReceiver)
        } catch (e: Exception) {
            // Ignored if not registered
        }
    }
}

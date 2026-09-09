package com.jasica.ai.controller.bluetooth

data class BluetoothDeviceItem(
    val name: String,
    val address: String,
    val isPaired: Boolean = false
)

enum class ConnectionState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    ERROR
}

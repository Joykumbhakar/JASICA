package com.bristi.controller

data class DeviceConfig(
    val id: String,
    val defaultName: String,
    val defaultOnCmd: String,
    val defaultOffCmd: String,
    val defaultPinOn: String,
    val defaultPinOff: String
)

val DEFAULT_DEVICES = listOf(
    DeviceConfig("dev1", "LED 1", "turn on led 1", "turn off led 1", "a", "A"),
    DeviceConfig("dev2", "LED 2", "turn on led 2", "turn off led 2", "b", "B"),
    DeviceConfig("dev3", "LED 3", "turn on led 3", "turn off led 3", "c", "C"),
    DeviceConfig("dev4", "LED 4", "turn on led 4", "turn off led 4", "d", "D"),
    DeviceConfig("dev5", "LED 5", "turn on led 5", "turn off led 5", "e", "E"),
    DeviceConfig("dev6", "LED 6", "turn on led 6", "turn off led 6", "f", "F")
)

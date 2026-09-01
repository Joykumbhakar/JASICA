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
    DeviceConfig("dev1", "PC Hub", "turn on pc", "turn off pc", "a", "A"),
    DeviceConfig("dev2", "RGB", "turn on rgb", "turn off rgb", "b", "B"),
    DeviceConfig("dev3", "Room", "turn on room light", "turn off room light", "c", "C"),
    DeviceConfig("dev4", "Plug", "turn on plug", "turn off plug", "d", "D"),
    DeviceConfig("dev5", "Fan", "turn on fan", "turn off fan", "e", "E"),
    DeviceConfig("dev6", "AC Unit", "turn on ac", "turn off ac", "f", "F")
)

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
    DeviceConfig("dev1", "1st LED", "turn on 1st led", "turn off 1st led", "a", "A"),
    DeviceConfig("dev2", "2nd LED", "turn on 2nd led", "turn off 2nd led", "b", "B"),
    DeviceConfig("dev3", "3rd LED", "turn on 3rd led", "turn off 3rd led", "c", "C"),
    DeviceConfig("dev4", "4th LED", "turn on 4th led", "turn off 4th led", "d", "D"),
    DeviceConfig("dev5", "5th LED", "turn on 5th led", "turn off 5th led", "e", "E"),
    DeviceConfig("dev6", "6th LED", "turn on 6th led", "turn off 6th led", "f", "F")
)

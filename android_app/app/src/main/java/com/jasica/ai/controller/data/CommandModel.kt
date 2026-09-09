package com.jasica.ai.controller.data

data class PinCommand(
    val pinNumber: Int,
    val onChar: Char,
    val offChar: Char,
    var label: String,
    var onPhrase: String,
    var offPhrase: String,
    var isStateOn: Boolean = false
)

object DefaultCommands {
    fun getDefaultPinList(): List<PinCommand> {
        val list = mutableListOf<PinCommand>()
        val onChars = listOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l')
        val offChars = listOf('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L')
        val ordinals = listOf("1st", "2nd", "3rd", "4th", "5th", "6th", "7th", "8th", "9th", "10th", "11th", "12th")

        for (i in 0 until 12) {
            val pin = i + 2 // Pin 2 to Pin 13
            val ord = ordinals[i]
            list.add(
                PinCommand(
                    pinNumber = pin,
                    onChar = onChars[i],
                    offChar = offChars[i],
                    label = "$ord Device (Pin $pin)",
                    onPhrase = "turn on the $ord led",
                    offPhrase = "turn off the $ord led"
                )
            )
        }
        return list
    }
}

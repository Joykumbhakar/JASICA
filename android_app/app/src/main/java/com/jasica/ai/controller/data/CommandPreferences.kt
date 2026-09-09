package com.jasica.ai.controller.data

import android.content.Context
import android.content.SharedPreferences

class CommandPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("jasica_commands_prefs", Context.MODE_PRIVATE)

    fun savePinList(pinList: List<PinCommand>) {
        val editor = prefs.edit()
        for (item in pinList) {
            editor.putString("label_${item.pinNumber}", item.label)
            editor.putString("on_phrase_${item.pinNumber}", item.onPhrase)
            editor.putString("off_phrase_${item.pinNumber}", item.offPhrase)
        }
        editor.apply()
    }

    fun loadPinList(): List<PinCommand> {
        val defaults = DefaultCommands.getDefaultPinList()
        return defaults.map { def ->
            val label = prefs.getString("label_${def.pinNumber}", def.label) ?: def.label
            val onPhrase = prefs.getString("on_phrase_${def.pinNumber}", def.onPhrase) ?: def.onPhrase
            val offPhrase = prefs.getString("off_phrase_${def.pinNumber}", def.offPhrase) ?: def.offPhrase
            def.copy(label = label, onPhrase = onPhrase, offPhrase = offPhrase)
        }
    }

    fun resetToDefaults(): List<PinCommand> {
        prefs.edit().clear().apply()
        return DefaultCommands.getDefaultPinList()
    }
}

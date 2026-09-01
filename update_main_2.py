import re

file_path = r'e:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# Replace localCommandTable and matchLocalCommand
pattern = r'    private data class LocalCommand\([\s\S]*?allKeywords && anyOfMatch\n        \}\n    \}'
replacement = '''    private data class LocalCommand(
        val keywords : List<String>,
        val anyOf    : List<String> = emptyList(),
        val command  : String,
        val confirmationText: String
    )

    private fun matchLocalCommand(spokenText: String): LocalCommand? {
        val commands = mutableListOf<LocalCommand>()
        commands.add(LocalCommand(listOf("turn","on","all"), command = "on", confirmationText = "Activating all systems."))
        commands.add(LocalCommand(listOf("turn","off","all"), command = "off", confirmationText = "Shutting everything down."))
        commands.add(LocalCommand(listOf("mood"), command = "mood", confirmationText = "Mood lighting on."))
        commands.add(LocalCommand(listOf("play", "song"), anyOf = listOf("fav", "favorite", "favourite"), command = "SYS_YT_FAV", confirmationText = "Opening YouTube for your favorite song."))
        commands.add(LocalCommand(listOf("open", "instagram"), command = "SYS_OPEN_IG", confirmationText = "Opening Instagram."))
        commands.add(LocalCommand(listOf("open", "facebook"), command = "SYS_OPEN_FB", confirmationText = "Opening Facebook."))
        commands.add(LocalCommand(listOf("open", "linkedin"), command = "SYS_OPEN_LI", confirmationText = "Opening LinkedIn."))
        commands.add(LocalCommand(listOf("open", "whatsapp"), command = "SYS_OPEN_WA", confirmationText = "Opening WhatsApp."))
        commands.add(LocalCommand(listOf("open", "telegram"), command = "SYS_OPEN_TG", confirmationText = "Opening Telegram."))
        commands.add(LocalCommand(listOf("open", "camera"), command = "SYS_OPEN_CAMERA", confirmationText = "Opening the camera."))
        commands.add(LocalCommand(listOf("take", "photo"), command = "SYS_OPEN_CAMERA", confirmationText = "Opening the camera."))
        commands.add(LocalCommand(listOf("start", "recording"), command = "SYS_RECORD_VIDEO", confirmationText = "Opening camera in video mode."))
        commands.add(LocalCommand(listOf("record", "video"), command = "SYS_RECORD_VIDEO", confirmationText = "Opening camera in video mode."))

        // Add dynamic devices
        DEFAULT_DEVICES.forEach { dev ->
            val name = sharedPrefs.getString("DEV__NAME", dev.defaultName) ?: dev.defaultName
            val onCmd = sharedPrefs.getString("DEV__ON_CMD", dev.defaultOnCmd) ?: dev.defaultOnCmd
            val offCmd = sharedPrefs.getString("DEV__OFF_CMD", dev.defaultOffCmd) ?: dev.defaultOffCmd
            val pinOn = sharedPrefs.getString("DEV__PIN_ON", dev.defaultPinOn) ?: dev.defaultPinOn
            val pinOff = sharedPrefs.getString("DEV__PIN_OFF", dev.defaultPinOff) ?: dev.defaultPinOff

            commands.add(LocalCommand(
                keywords = onCmd.lowercase(java.util.Locale.getDefault()).split("\\\\s+".toRegex()).filter { it.isNotBlank() },
                command = pinOn,
                confirmationText = " on."
            ))
            commands.add(LocalCommand(
                keywords = offCmd.lowercase(java.util.Locale.getDefault()).split("\\\\s+".toRegex()).filter { it.isNotBlank() },
                command = pinOff,
                confirmationText = " off."
            ))
        }

        val words = spokenText
            .lowercase(java.util.Locale.getDefault())
            .replace(Regex("[^a-z0-9 ]"), " ")
            .split("\\\\s+".toRegex())
            .filter { it.isNotBlank() }
            .toSet()

        return commands.firstOrNull { cmd ->
            val allKeywords = cmd.keywords.all { it in words }
            val anyOfMatch  = cmd.anyOf.isEmpty() || cmd.anyOf.any { it in words }
            allKeywords && anyOfMatch
        }
    }'''

content = re.sub(pattern, replacement, content)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

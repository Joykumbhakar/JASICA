import re

file_path = r'e:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# 1. Remove sarvamTts declaration
content = re.sub(r'private lateinit var sarvamTts: SarvamTtsEngine\n', '', content)

# 2. Remove sarvamTts initialization
content = re.sub(r'sarvamTts = SarvamTtsEngine\(this, BuildConfig\.SARVAM_API_KEY\)\n', '', content)

# 3. Remove sarvamTts shutdown
content = re.sub(r'sarvamTts\.shutdown\(\)\n', '', content)

# 4. Simplify speakMultilingual
old_speak = """    private fun speakMultilingual(text: String, utteranceId: String) {
        if (text.isBlank()) return

        val useSarvam = sarvamTts.isAvailable && isNetworkAvailable()

        if (useSarvam) {
            sarvamTts.speak(
                text = text,
                utteranceId = utteranceId,
                onDone = { id ->
                    runOnUiThread { onFinalUtteranceDone(id) }
                },
                onError = { id, e ->
                    Log.e("JasicaApp", "Sarvam TTS failed, falling back to built-in TTS", e)
                    runOnUiThread {
                        speakBuiltIn(text, id)
                    }
                }
            )
        } else {
            speakBuiltIn(text, utteranceId)
        }
    }"""

new_speak = """    private fun speakMultilingual(text: String, utteranceId: String) {
        if (text.isBlank()) return
        speakBuiltIn(text, utteranceId)
    }"""

content = content.replace(old_speak, new_speak)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)
print("Removed SarvamTtsEngine references from MainActivity")

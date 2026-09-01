import re

file_path = r'e:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

pattern = r'''    private fun speakMultilingual\(text: String, utteranceId: String\) \{[\s\S]*?return chunked.ifEmpty \{ listOf\(TextSegment\(text, false\)\) \}\n    \}'''
replacement = '''    private fun speakMultilingual(text: String, utteranceId: String) {
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
    }

    private fun onFinalUtteranceDone(utteranceId: String) {
        if (utteranceId == "JASICA_WAKE") {
            startListening()
        } else if (appState.value == AppState.SPEAKING) {
            appState.value = AppState.IDLE
            triggerWakeWordLoopIfEnabled()
        }
    }

    private fun speakBuiltIn(text: String, utteranceId: String) {
        // We use bn-IN as the single voice since it handles both Bengali script and English well.
        tts.language = java.util.Locale("bn", "IN")
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }'''

content = re.sub(pattern, replacement, content)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)
print("Updated speakMultilingual to use one voice")

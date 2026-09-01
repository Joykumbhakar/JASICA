import re

file_path = r'e:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

pattern = r'''    override fun onInit\(status: Int\) \{[\s\S]*?        \}
    \}'''

replacement = '''    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = java.util.Locale("bn", "IN")
            tts.setPitch(1.1f)
            tts.setSpeechRate(1.0f)

            tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {}
                override fun onDone(utteranceId: String?)  {
                    runOnUiThread {
                        onFinalUtteranceDone(utteranceId ?: "")
                    }
                }
                override fun onError(utteranceId: String?) {
                    runOnUiThread {
                        if (appState.value == AppState.SPEAKING) {
                            appState.value = AppState.IDLE
                            triggerWakeWordLoopIfEnabled()
                        }
                    }
                }
            })
        }
    }'''

content = re.sub(pattern, replacement, content)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)
print("Updated onInit")

import re
path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

target = """    private var discoveryReceiver: BroadcastReceiver? = null"""

replacement = """    private var discoveryReceiver: BroadcastReceiver? = null
    
    private val quickAccessReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                "com.bristi.controller.SEND_QUICK_COMMAND" -> {
                    val idx = intent.getIntExtra("device_index", -1)
                    if (idx != -1) {
                        val command = when(idx) {
                            1 -> "A"
                            2 -> "B"
                            3 -> "C"
                            4 -> "D"
                            else -> return
                        }
                        sendRawCommand(command)
                        Toast.makeText(context, "Command sent", Toast.LENGTH_SHORT).show()
                    }
                }
                "com.bristi.controller.START_MIC" -> {
                    if (appState.value != AppState.LISTENING) {
                        startListening()
                    }
                }
            }
        }
    }"""

if target in text:
    text = text.replace(target, replacement)
    with open(path, "w", encoding="utf-8") as f:
        f.write(text)
    print("Updated MainActivity quickAccessReceiver")
else:
    print("Could not find target")

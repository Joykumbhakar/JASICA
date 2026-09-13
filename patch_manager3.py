path = r"E:\Controller\app\src\main\java\com\bristi\controller\JasicaBluetoothManager.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

target = """    fun sendRawCommand(command: String) {
        val payload = "$command\\n".toByteArray()
        if (isClassicConnected && classicOutStream != null) {"""

replacement = """    fun sendRawCommand(command: String) {
        Thread {
            val payload = "$command\\n".toByteArray()
            if (isClassicConnected && classicOutStream != null) {"""

target2 = """            } catch (e: SecurityException) {
            }
        }
    }
}"""

replacement2 = """            } catch (e: SecurityException) {
            }
        }
        }.start()
    }
}"""

text = text.replace(target, replacement)
text = text.replace(target2, replacement2)

with open(path, "w", encoding="utf-8") as f:
    f.write(text)

print("Thread added to JasicaBluetoothManager")

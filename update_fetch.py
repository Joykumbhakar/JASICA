import re
file_path = r'e:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

new_fetch_code = """
    private fun fetchApiKeyFromPortfolio() {
        val client = okhttp3.OkHttpClient()
        val request = okhttp3.Request.Builder()
            .url(PORTFOLIO_API_URL)
            .build()
            
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    
                    if (!responseBody.isNullOrBlank()) {
                        // 1. Try to extract API Key
                        var key = responseBody.trim()
                        val match = "\"(AIza[^\"]+)\"".toRegex().find(key)
                        if (match != null) {
                            key = match.groupValues[1]
                        }
                        
                        if (key.startsWith("AIza")) {
                            sharedPrefs.edit().putString("API_KEY", key).apply()
                            runOnUiThread {
                                userApiKey.value = key
                                android.util.Log.d("JasicaApp", "API Key successfully updated from portfolio!")
                            }
                        }
                        
                        // 2. Try to extract Dynamic Icon Config
                        val iconMatch = "\"active_icon\"\\s*:\\s*\"([^\"]+)\"".toRegex().find(responseBody)
                        if (iconMatch != null) {
                            val activeIconName = iconMatch.groupValues[1]
                            runOnUiThread {
                                changeAppIcon(activeIconName)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("JasicaApp", "Failed to fetch API key from portfolio", e)
            }
        }
    }

    private fun changeAppIcon(iconName: String) {
        val pm = packageManager
        val defaultComponent = android.content.ComponentName(this, "com.bristi.controller.MainActivity")
        val darkComponent = android.content.ComponentName(this, "com.bristi.controller.AliasDark")
        val neonComponent = android.content.ComponentName(this, "com.bristi.controller.AliasNeon")
        
        // Define desired states based on the string from the server
        val enableDefault = iconName.lowercase() == "default"
        val enableDark = iconName.lowercase() == "dark"
        val enableNeon = iconName.lowercase() == "neon"
        
        // Only apply if it's a known icon to prevent disabling everything
        if (!enableDefault && !enableDark && !enableNeon) return
        
        // Helper to enable/disable
        fun setComponentState(component: android.content.ComponentName, enable: Boolean) {
            val state = if (enable) android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_ENABLED else android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            pm.setComponentEnabledSetting(component, state, android.content.pm.PackageManager.DONT_KILL_APP)
        }
        
        setComponentState(darkComponent, enableDark)
        setComponentState(neonComponent, enableNeon)
        setComponentState(defaultComponent, enableDefault)
    }
"""

# Replace old fetchApiKeyFromPortfolio
old_start = content.find("private fun fetchApiKeyFromPortfolio()")
if old_start != -1:
    old_end = content.find("private fun setupSpeechRecognizer()", old_start)
    if old_end != -1:
        content = content[:old_start] + new_fetch_code + "\n    " + content[old_end:]
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(content)
        print("Updated fetch code")
else:
    print("Could not find function")


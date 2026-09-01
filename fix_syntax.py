import re
file_path = r'e:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

content = content.replace('private const val PORTFOLIO_API_URL', 'private val PORTFOLIO_API_URL')

new_fetch_func = '''
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
                        var key = responseBody.trim()
                        val match = "\\\"(AIza[^\\\"]+)\\\"".toRegex().find(key)
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
                        
                        val iconMatch = "\\\"active_icon\\\"\\\\s*:\\\\s*\\\"([^\\\"]+)\\\"".toRegex().find(responseBody)
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
'''

start_idx = content.find("private fun fetchApiKeyFromPortfolio()")
end_idx = content.find("private fun changeAppIcon(iconName: String)")

if start_idx != -1 and end_idx != -1:
    content = content[:start_idx] + new_fetch_func.strip() + "\n\n    " + content[end_idx:]

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)
print("Fixed syntax errors")

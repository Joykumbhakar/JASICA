import re
file_path = r'e:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# 1. Add PORTFOLIO_API_URL
if 'PORTFOLIO_API_URL' not in content:
    content = content.replace(
        'private val DEFAULT_API_KEY',
        '// Replace this URL with your actual portfolio admin panel API endpoint\n    private const val PORTFOLIO_API_URL = "https://your-portfolio.com/api/get-gemini-key"\n    private val DEFAULT_API_KEY'
    )

# 2. Add fetchApiKeyFromPortfolio function
fetch_func = """
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
                        // Attempt to extract the key if it's wrapped in JSON (e.g. {"key": "AIza..."})
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
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("JasicaApp", "Failed to fetch API key from portfolio", e)
            }
        }
    }
"""

if 'fun fetchApiKeyFromPortfolio' not in content:
    content = content.replace('private fun setupSpeechRecognizer() {', fetch_func + '\n    private fun setupSpeechRecognizer() {')

# 3. Call it in onCreate
if 'fetchApiKeyFromPortfolio()' not in content:
    content = content.replace('setupSpeechRecognizer()', 'fetchApiKeyFromPortfolio()\n        setupSpeechRecognizer()')

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)
print("Injected fetchApiKeyFromPortfolio")

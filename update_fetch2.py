import re
file_path = r'e:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

new_fetch_func = """
    // The new URL for fetching your app configuration
    private val PORTFOLIO_CONFIG_URL = "https://joykumbhakar.vercel.app/api/app-config"

    private fun fetchAppConfigFromPortfolio() {
        val client = okhttp3.OkHttpClient()
        val request = okhttp3.Request.Builder()
            .url(PORTFOLIO_CONFIG_URL)
            .build()
            
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    if (!responseBody.isNullOrBlank()) {
                        try {
                            // Cleanly parse the JSON response
                            val json = org.json.JSONObject(responseBody)
                            
                            // 1. Extract API Key if present
                            val apiKey = json.optString("api_key", "")
                            if (apiKey.startsWith("AIza")) {
                                sharedPrefs.edit().putString("API_KEY", apiKey).apply()
                                runOnUiThread {
                                    userApiKey.value = apiKey
                                }
                            }
                            
                            // 2. Extract active icon
                            val activeIcon = json.optString("active_icon", "default")
                            
                            // Instantly switch the app icon
                            runOnUiThread {
                                changeAppIcon(activeIcon)
                                android.util.Log.d("JasicaApp", "App icon updated to $activeIcon from portfolio!")
                            }
                        } catch (e: Exception) {
                            android.util.Log.e("JasicaApp", "Failed to parse JSON config", e)
                        }
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("JasicaApp", "Failed to fetch app config", e)
            }
        }
    }
"""

start_idx = content.find("private fun fetchApiKeyFromPortfolio()")
end_idx = content.find("private fun changeAppIcon(iconName: String)")

if start_idx != -1 and end_idx != -1:
    content = content[:start_idx] + new_fetch_func.strip() + "\n\n    " + content[end_idx:]
    
    # We also need to change the call in onCreate from fetchApiKeyFromPortfolio to fetchAppConfigFromPortfolio
    content = content.replace("fetchApiKeyFromPortfolio()", "fetchAppConfigFromPortfolio()")
    # and remove PORTFOLIO_API_URL
    content = content.replace('private val PORTFOLIO_API_URL = "https://joykumbhakar.vercel.app/api/get-gemini-key"\n', '')

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)
print("Updated to fetchAppConfigFromPortfolio")

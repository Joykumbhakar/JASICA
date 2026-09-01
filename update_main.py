import re
file_path = r'e:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

new_logic = """
    private fun changeAppIcon(iconName: String) {
        val pm = packageManager
        val defaultComponent = android.content.ComponentName(this, "com.bristi.controller.MainActivity")
        val favDiComponent = android.content.ComponentName(this, "com.bristi.controller.AliasFavDi")
        val sonaDiComponent = android.content.ComponentName(this, "com.bristi.controller.AliasSonaDi")
        val tithiComponent = android.content.ComponentName(this, "com.bristi.controller.AliasTithi")
        
        // Define desired states based on the string from the server
        val enableDefault = iconName.lowercase() == "default" || iconName.lowercase() == "jasica"
        val enableFavDi = iconName.lowercase() == "fav_di"
        val enableSonaDi = iconName.lowercase() == "sona_di"
        val enableTithi = iconName.lowercase() == "tithi"
        
        // Only apply if it's a known icon to prevent disabling everything
        if (!enableDefault && !enableFavDi && !enableSonaDi && !enableTithi) return
        
        // Helper to enable/disable
        fun setComponentState(component: android.content.ComponentName, enable: Boolean) {
            val state = if (enable) android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_ENABLED else android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            pm.setComponentEnabledSetting(component, state, android.content.pm.PackageManager.DONT_KILL_APP)
        }
        
        setComponentState(favDiComponent, enableFavDi)
        setComponentState(sonaDiComponent, enableSonaDi)
        setComponentState(tithiComponent, enableTithi)
        setComponentState(defaultComponent, enableDefault)
    }
"""

start_idx = content.find("private fun changeAppIcon(iconName: String) {")
end_idx = content.find("private fun setupSpeechRecognizer()", start_idx)

if start_idx != -1 and end_idx != -1:
    content = content[:start_idx] + new_logic.strip() + "\n\n    " + content[end_idx:]
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)
    print("Updated MainActivity logic")
else:
    print("Could not find changeAppIcon in MainActivity")

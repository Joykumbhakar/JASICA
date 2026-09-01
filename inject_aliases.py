import re
file_path = r'e:\Controller\app\src\main\AndroidManifest.xml'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

aliases = """
        <!-- Dynamic App Icons (Disabled by default) -->
        <activity-alias
            android:name=".AliasDark"
            android:enabled="false"
            android:exported="true"
            android:icon="@drawable/logo_dark"
            android:targetActivity=".MainActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity-alias>

        <activity-alias
            android:name=".AliasNeon"
            android:enabled="false"
            android:exported="true"
            android:icon="@drawable/logo_neon"
            android:targetActivity=".MainActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity-alias>
"""

if 'AliasDark' not in content:
    content = content.replace('</activity>', '</activity>\n' + aliases)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)
print("Added aliases")

import re
file_path = r'e:\Controller\app\src\main\AndroidManifest.xml'
with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# 1. Update Application Icon to Jasica
content = re.sub(r'android:icon="@mipmap/[^"]+"', 'android:icon="@drawable/jasica"', content)
content = re.sub(r'android:roundIcon="@mipmap/[^"]+"', 'android:roundIcon="@drawable/jasica"', content)

# 2. Re-write the aliases
old_aliases_regex = r'<!-- Dynamic App Icons .*?</activity-alias>'
# We will just replace from the first Alias to the last
start = content.find('<activity-alias')
end = content.rfind('</activity-alias>') + len('</activity-alias>')

new_aliases = """
        <!-- Dynamic App Icons (Disabled by default) -->
        <activity-alias
            android:name=".AliasFavDi"
            android:enabled="false"
            android:exported="true"
            android:icon="@drawable/favourite_photo_of_di"
            android:roundIcon="@drawable/favourite_photo_of_di"
            android:targetActivity=".MainActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity-alias>

        <activity-alias
            android:name=".AliasSonaDi"
            android:enabled="false"
            android:exported="true"
            android:icon="@drawable/sona_di"
            android:roundIcon="@drawable/sona_di"
            android:targetActivity=".MainActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity-alias>
        
        <activity-alias
            android:name=".AliasTithi"
            android:enabled="false"
            android:exported="true"
            android:icon="@drawable/tithi"
            android:roundIcon="@drawable/tithi"
            android:targetActivity=".MainActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity-alias>
"""

if start != -1:
    content = content[:start] + new_aliases.strip() + content[end:]

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)
print("Updated Manifest")

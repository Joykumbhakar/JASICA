import re
path = r"E:\Controller\app\src\main\AndroidManifest.xml"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

permissions_insert = """    <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE_SPECIAL_USE" />

    <!-- Bluetooth: Legacy (Android 11 and below) -->"""

if "SYSTEM_ALERT_WINDOW" not in text:
    text = text.replace("    <!-- Bluetooth: Legacy (Android 11 and below) -->", permissions_insert)

service_insert = """        <receiver android:name=".WaterAlarmReceiver" android:exported="false" />

        <service
            android:name=".FloatingControlService"
            android:exported="false"
            android:foregroundServiceType="specialUse">
            <property
                android:name="android.app.PROPERTY_SPECIAL_USE_FGS_SUBTYPE"
                android:value="Floating window overlay for quick hardware control" />
        </service>"""

if "FloatingControlService" not in text:
    text = text.replace("        <receiver android:name=\".WaterAlarmReceiver\" android:exported=\"false\" />", service_insert)

with open(path, "w", encoding="utf-8") as f:
    f.write(text)
print("Updated AndroidManifest.xml")

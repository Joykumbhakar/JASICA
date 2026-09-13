import os

path = r"E:\Controller\app\src\main\AndroidManifest.xml"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

target = """        <service
            android:name=".ShakeService"
            android:exported="false"
            android:foregroundServiceType="specialUse">
            <property
                android:name="android.app.PROPERTY_SPECIAL_USE_FGS_SUBTYPE"
                android:value="Accelerometer listener for shake gesture to toggle IoT device" />
        </service>"""

replacement = target + """

        <service
            android:name=".TiltService"
            android:exported="false"
            android:foregroundServiceType="specialUse">
            <property
                android:name="android.app.PROPERTY_SPECIAL_USE_FGS_SUBTYPE"
                android:value="Accelerometer listener for tilt gesture to toggle IoT devices" />
        </service>"""

if target in text:
    text = text.replace(target, replacement)
    with open(path, "w", encoding="utf-8") as f:
        f.write(text)
    print("Added TiltService to manifest")
else:
    print("Could not find ShakeService in manifest")

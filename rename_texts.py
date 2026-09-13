import re

file_path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

# 1. Quick commands
content = content.replace('"Turn on PC"', '"Turn on LED 1"')
content = content.replace('"Mood Lighting"', '"Turn on LED 2"')
content = content.replace('"Turn on Fan"', '"Turn on LED 5"')
content = content.replace('"Turn off RGB"', '"Turn off LED 2"')

# 2. Arduino Code Comments
content = content.replace('// a/A - PC / Computer', '// a/A - LED 1')
content = content.replace('// b/B - RGB Lights', '// b/B - LED 2')
content = content.replace('// c/C - Room Light', '// c/C - LED 3')
content = content.replace('// d/D - Smart Plug', '// d/D - LED 4')
content = content.replace('// e/E - Ceiling Fan', '// e/E - LED 5')
content = content.replace('// f/F - Air Conditioner', '// f/F - LED 6')

# 3. Onboarding screen
content = content.replace('lights, PC, AC, and more natively.', 'LEDs and devices natively.')

# 4. Action cards
content = content.replace('onSendRawCommand("a") // PC', 'onSendRawCommand("a") // LED 1')
content = content.replace('onSendRawCommand("b") // RGB', 'onSendRawCommand("b") // LED 2')
content = content.replace('onSendRawCommand("c") // Room light', 'onSendRawCommand("c") // LED 3')
content = content.replace('onSendRawCommand("d") // Plug', 'onSendRawCommand("d") // LED 4')
content = content.replace('onSendRawCommand("e") // Fan', 'onSendRawCommand("e") // LED 5')
content = content.replace('onSendRawCommand("f") // AC', 'onSendRawCommand("f") // LED 6')

# 5. Synonym mapping (if any)
content = content.replace('"computer"    to "pc"', '"computer"    to "led 1"')
content = content.replace('"laptop"      to "pc"', '"laptop"      to "led 1"')
content = content.replace('"desktop"     to "pc"', '"desktop"     to "led 1"')
content = content.replace('"hub"         to "pc"', '"hub"         to "led 1"')

content = content.replace('"nightlight"  to "rgb"', '"nightlight"  to "led 2"')
content = content.replace('"colorlight"  to "rgb"', '"colorlight"  to "led 2"')

content = content.replace('"cooler"      to "ac"', '"cooler"      to "led 6"')
content = content.replace('"aircon"      to "ac"', '"aircon"      to "led 6"')
content = content.replace('"conditioner" to "ac"', '"conditioner" to "led 6"')

content = content.replace('"ceiling"     to "fan"', '"ceiling"     to "led 5"')
content = content.replace('"pakha"       to "fan"', '"pakha"       to "led 5"')

# 6. Any random inline occurrences:
# We have a prompt builder:
# It says "Available devices to control:" and lists the devices from sharedPrefs. So that is already dynamic!
# We don't need to change the prompt string if it generates dynamically from DeviceConfig!

with open(file_path, "w", encoding="utf-8") as f:
    f.write(content)
print("Done.")

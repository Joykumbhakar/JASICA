import re

file_path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

# 1. Remove tint in DeviceControlCard
old_device_robot = r'androidx\.compose\.foundation\.Image\(\s*painter = androidx\.compose\.ui\.res\.painterResource\(id = R\.drawable\.robot\),\s*contentDescription = null,\s*modifier = Modifier\.size\(24\.dp\),\s*colorFilter = androidx\.compose\.ui\.graphics\.ColorFilter\.tint\(if \(isChecked\) activeAccent else subTextColor\)\s*\)'
new_device_robot = r'androidx.compose.foundation.Image(\n                    painter = androidx.compose.ui.res.painterResource(id = R.drawable.robot),\n                    contentDescription = null,\n                    modifier = Modifier.size(24.dp)\n                )'
content = re.sub(old_device_robot, new_device_robot, content)

# 2. Use robot in Settings where SmartToy is used
old_smarttoy = r'Icon\(Icons\.Rounded\.SmartToy,\s*contentDescription = null,\s*modifier = Modifier\.size\(24\.dp\),\s*tint = Color\.White\)'
new_smarttoy = r'androidx.compose.foundation.Image(painter = androidx.compose.ui.res.painterResource(id = R.drawable.robot), contentDescription = null, modifier = Modifier.size(24.dp))'
content = re.sub(old_smarttoy, new_smarttoy, content)

# Wait, is SmartToy used anywhere else?
# Let's see if there are other places.

with open(file_path, "w", encoding="utf-8") as f:
    f.write(content)

print("Fixed robot icons")

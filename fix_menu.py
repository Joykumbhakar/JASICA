import re

file_path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

# 1. Update Bluetooth icon
old_bt = r"""Icon\(
\s*imageVector = Icons\.Outlined\.Bluetooth,
\s*contentDescription = if \(isBtConnected\) "Bluetooth Connected" else "Bluetooth Disconnected",
\s*tint = if \(isBtConnected\) JasicaWhite else JasicaWhite\.copy\(alpha = 0\.4f\)
\s*\)"""

new_bt = """Icon(
                            painter = painterResource(id = R.drawable.bluetooth_icon),
                            contentDescription = if (isBtConnected) "Bluetooth Connected" else "Bluetooth Disconnected",
                            tint = if (isBtConnected) JasicaWhite else JasicaWhite.copy(alpha = 0.4f)
                        )"""
content = re.sub(old_bt, new_bt, content)

# 2. Update More Options menu icon
# In Android, android.R.drawable.ic_menu_more might look weird on different OS versions. 
# We'll use Icons.Rounded.MoreVert
old_menu_icon = r"""Icon\(
\s*painter = painterResource\(id = android\.R\.drawable\.ic_menu_more\),
\s*contentDescription = "More Options",
\s*tint = JasicaWhite
\s*\)"""

new_menu_icon = """Icon(
                                imageVector = Icons.Rounded.MoreVert,
                                contentDescription = "More Options",
                                tint = JasicaWhite
                            )"""

# Wait, if they had ArrowDropDownCircle before, maybe it was not ic_menu_more?
# Let's check if the file currently has ic_menu_more
if "android.R.drawable.ic_menu_more" in content:
    content = re.sub(old_menu_icon, new_menu_icon, content)
else:
    # Maybe it has Icons.Rounded.ArrowDropDownCircle?
    content = re.sub(r'imageVector = Icons\.(Rounded|Outlined|Default)\.ArrowDropDownCircle,', r'imageVector = Icons.Rounded.MoreVert,', content)

# 3. Update main menu background to solid white
old_menu_bg = r"""Box\(
\s*modifier = Modifier
\s*\.width\(220\.dp\)
\s*\.clip\(RoundedCornerShape\(16\.dp\)\)
\s*\.then\(
\s*if \(hazeState != null\) Modifier\.hazeEffect\(
\s*state = hazeState,
\s*style = dev\.chrisbanes\.haze\.HazeStyle\(
\s*blurRadius = 24\.dp,
\s*tint = dev\.chrisbanes\.haze\.HazeTint\(Color\.White\.copy\(alpha=0\.25f\)\)
\s*\)
\s*\) else Modifier
\s*\)
\s*\.background\(Color\.White\.copy\(alpha = if \(hazeState != null\) 0\.65f else 0\.95f\)\)
\s*\.border\(0\.5\.dp, Color\.White\.copy\(alpha=0\.6f\), RoundedCornerShape\(16\.dp\)\)
\s*\) \{"""

new_menu_bg = """Box(
                modifier = Modifier
                    .width(220.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
            ) {"""

content = re.sub(old_menu_bg, new_menu_bg, content)

with open(file_path, "w", encoding="utf-8") as f:
    f.write(content)
print("Updated main menu and icons")

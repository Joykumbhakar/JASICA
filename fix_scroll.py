import re

file_path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
with open(file_path, "r", encoding="utf-8") as f:
    content = f.read()

# Replace LazyColumn in SettingsScreen with Column(verticalScroll)
# Locate SettingsScreen LazyColumn
lazy_col_pattern = r'LazyColumn\(\s*modifier = Modifier\s*\.weight\(1f\)\s*\.fillMaxWidth\(\),\s*\)\s*\{'
replacement = r'''val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
            ) {'''
content = re.sub(lazy_col_pattern, replacement, content)

start_idx = content.find('fun SettingsScreen(')
end_idx = content.find('fun DeviceSelectionDialog(', start_idx)

settings_content = content[start_idx:end_idx]

# Replace `item {` with `Box {`
settings_content = re.sub(r'(\s+)item\s*\{', r'\1Box {', settings_content)

content = content[:start_idx] + settings_content + content[end_idx:]

with open(file_path, "w", encoding="utf-8") as f:
    f.write(content)
print("Done converting LazyColumn to Column in SettingsScreen.")

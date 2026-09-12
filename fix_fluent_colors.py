import os
import glob
import re

drawable_dir = r"E:\Controller\app\src\main\res\drawable"
xml_files = glob.glob(os.path.join(drawable_dir, "fluentui_system_icons_*.xml"))

count = 0
for file_path in xml_files:
    with open(file_path, "r", encoding="utf-8") as f:
        content = f.read()
    
    # Replace url(...) with a solid color so it compiles
    new_content = re.sub(r'fillColor="url\([^)]+\)"', 'fillColor="#FFFFFF"', content)
    new_content = re.sub(r'strokeColor="url\([^)]+\)"', 'strokeColor="#FFFFFF"', new_content)
    
    if new_content != content:
        with open(file_path, "w", encoding="utf-8") as f:
            f.write(new_content)
        count += 1

print(f"Fixed {count} fluent XML files.")

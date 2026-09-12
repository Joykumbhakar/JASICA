import re

with open(r"E:\Controller\app\build.gradle.kts", "r", encoding="utf-8") as f:
    content = f.read()

# Add coreLibraryDesugaringEnabled = true
if "isCoreLibraryDesugaringEnabled" not in content:
    content = re.sub(r'(compileOptions\s*\{)', r'\1\n        isCoreLibraryDesugaringEnabled = true', content)

# Add dependency
if "coreLibraryDesugaring" not in content:
    content = re.sub(r'(dependencies\s*\{)', r'\1\n    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")', content)

with open(r"E:\Controller\app\build.gradle.kts", "w", encoding="utf-8") as f:
    f.write(content)

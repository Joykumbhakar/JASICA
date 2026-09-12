import re
import sys

def main():
    file_path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
    with open(file_path, "r", encoding="utf-8") as f:
        content = f.read()

    # 1. Fix the extra parentheses after AppleSwitch
    # The regex matches: AppleSwitch( ... ) )
    # We replace it with: AppleSwitch( ... )
    content = re.sub(
        r'(AppleSwitch\(.*?\n\s*\}\s*\n\s*\))\s*\n\s*\)',
        r'\1',
        content,
        flags=re.DOTALL
    )

    # Note: the water reminder switch looks like this:
    # AppleSwitch(
    #    checked = waterReminderInput,
    #    onCheckedChange = { isChecked ->
    #      ...
    #    }
    #  )
    # )
    # So the above regex `AppleSwitch\(.*?\n\s*\}\s*\n\s*\)` will match `AppleSwitch` up to its closing parenthesis.
    
    # 2. Add import for shadow
    if "import androidx.compose.ui.draw.shadow" not in content:
        content = content.replace(
            "import androidx.compose.ui.Modifier",
            "import androidx.compose.ui.Modifier\nimport androidx.compose.ui.draw.shadow"
        )
        
    with open(file_path, "w", encoding="utf-8") as f:
        f.write(content)

if __name__ == "__main__":
    main()

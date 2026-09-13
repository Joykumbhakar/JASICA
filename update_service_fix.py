import re
path = r"E:\Controller\app\src\main\java\com\bristi\controller\FloatingControlService.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

text = text.replace("import androidx.lifecycle.ViewTreeLifecycleOwner", "import androidx.lifecycle.setViewTreeLifecycleOwner")
text = text.replace("import androidx.lifecycle.ViewTreeViewModelStoreOwner", "import androidx.lifecycle.setViewTreeViewModelStoreOwner")

target = """        ViewTreeLifecycleOwner.set(composeView, lifecycleOwner)
        ViewTreeViewModelStoreOwner.set(composeView, object : ViewModelStoreOwner {
            override val viewModelStore = ViewModelStore()
        })"""

replacement = """        composeView.setViewTreeLifecycleOwner(lifecycleOwner)
        composeView.setViewTreeViewModelStoreOwner(object : ViewModelStoreOwner {
            override val viewModelStore = ViewModelStore()
        })"""

if target in text:
    text = text.replace(target, replacement)
    with open(path, "w", encoding="utf-8") as f:
        f.write(text)
    print("Fixed FloatingControlService setup")
else:
    print("Could not find target")

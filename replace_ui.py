import re
import sys

def main():
    file_path = r"E:\Controller\app\src\main\java\com\bristi\controller\MainActivity.kt"
    with open(file_path, "r", encoding="utf-8") as f:
        content = f.read()

    components_code = """
@Composable
fun AppleSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val trackColor by animateColorAsState(if (checked) Color(0xFF34C759) else Color(0xFFE9E9EA), label = "trackColor")
    val thumbOffset by animateFloatAsState(if (checked) 22f else 2f, label = "thumbOffset")

    Box(
        modifier = modifier
            .width(50.dp)
            .height(30.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(trackColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onCheckedChange(!checked) }
    ) {
        Box(
            modifier = Modifier
                .padding(top = 2.dp, bottom = 2.dp)
                .offset(x = thumbOffset.dp)
                .size(26.dp)
                .shadow(elevation = 3.dp, shape = RoundedCornerShape(13.dp), clip = false)
                .background(Color.White, RoundedCornerShape(13.dp))
        )
    }
}
"""
    if "fun AppleSwitch" not in content:
        content = content.replace("@Composable\nfun LucideSliders", components_code.strip() + "\n\n@Composable\nfun LucideSliders")
    
    # Simple regex to replace Switch with AppleSwitch and remove colors
    content = re.sub(
        r'Switch\(\s*checked\s*=\s*([^,]+),\s*onCheckedChange\s*=\s*(\{.*?\})[^)]+colors\s*=\s*SwitchDefaults\.colors[^)]*\)\s*\)',
        r'AppleSwitch(\n                                      checked = \1,\n                                      onCheckedChange = \2\n                                  )',
        content,
        flags=re.DOTALL
    )
    
    content = re.sub(
        r'androidx\.compose\.material3\.Slider\(\s*value\s*=\s*(.*?),\s*onValueChange\s*=\s*(\{.*?\}),\s*valueRange\s*=\s*(.*?),\s*steps\s*=\s*(.*?),\s*colors\s*=\s*androidx\.compose\.material3\.SliderDefaults\.colors\([^)]*\)\s*\)',
        r'''androidx.compose.material3.Slider(
                                      value = \1,
                                      onValueChange = \2,
                                      valueRange = \3,
                                      steps = \4,
                                      colors = androidx.compose.material3.SliderDefaults.colors(
                                          thumbColor = Color.White,
                                          activeTrackColor = Color(0xFF007AFF),
                                          inactiveTrackColor = Color(0xFFE5E5EA)
                                      )
                                  )''',
        content,
        flags=re.DOTALL
    )
    
    with open(file_path, "w", encoding="utf-8") as f:
        f.write(content)

if __name__ == "__main__":
    main()

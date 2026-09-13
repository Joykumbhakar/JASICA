import re

path = r"E:\Controller\app\src\main\java\com\bristi\controller\FloatingControlService.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

target_anim = """        // Pulse animation for the main orb
        val infiniteTransition = rememberInfiniteTransition(label = "orbPulse")
        val orbScale by infiniteTransition.animateFloat(
            initialValue = 1f, targetValue = 1.08f,
            animationSpec = infiniteRepeatable(
                animation = tween(900, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ), label = "orbScale"
        )

        // Mic orb animations"""

replacement_anim = """        // Mic orb animations
        val infiniteTransition = rememberInfiniteTransition(label = "orbPulse")"""

text = text.replace(target_anim, replacement_anim)

target_scale = """                // Main Jasica orb button
                Box(
                    modifier = Modifier
                        .scale(if (expanded) 1f else orbScale)
                        .size(56.dp)"""

replacement_scale = """                // Main Jasica orb button
                Box(
                    modifier = Modifier
                        .size(56.dp)"""

text = text.replace(target_scale, replacement_scale)

with open(path, "w", encoding="utf-8") as f:
    f.write(text)
print("Removed orb scale animation")

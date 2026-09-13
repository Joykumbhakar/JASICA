import os

path = r"E:\Controller\app\src\main\java\com\bristi\controller\DoubleTapService.kt"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

target = """        if (magnitude < THRESHOLD_G) {
            wasBelowThreshold = true
            return
        }

        if (!wasBelowThreshold) return
        wasBelowThreshold = false

        val now = System.currentTimeMillis()

        if (now - lastTapTime > TAP_WINDOW_MS) {
            tapCount = 1
            lastTapTime = now
            
            handler.removeCallbacksAndMessages(null)
            handler.postDelayed({ finalizeTaps() }, TAP_WINDOW_MS + 150)
        } else {
            tapCount++
            lastTapTime = now
            
            handler.removeCallbacksAndMessages(null)
            handler.postDelayed({ finalizeTaps() }, TAP_WINDOW_MS + 150)
        }"""

replacement = """        if (magnitude < THRESHOLD_G) {
            wasBelowThreshold = true
            return
        }

        if (!wasBelowThreshold) return
        wasBelowThreshold = false

        val now = System.currentTimeMillis()

        // Debounce: ignore spikes that happen within 60ms of the last tap
        if (now - lastTapTime < 60) return

        if (now - lastTapTime > TAP_WINDOW_MS) {
            tapCount = 1
            lastTapTime = now
            
            handler.removeCallbacksAndMessages(null)
            handler.postDelayed({ finalizeTaps() }, TAP_WINDOW_MS + 100)
        } else {
            tapCount++
            lastTapTime = now
            
            handler.removeCallbacksAndMessages(null)
            handler.postDelayed({ finalizeTaps() }, TAP_WINDOW_MS + 100)
        }"""

if target in text:
    text = text.replace(target, replacement)
    with open(path, "w", encoding="utf-8") as f:
        f.write(text)
    print("DoubleTap logic improved")
else:
    print("Target not found in DoubleTapService")

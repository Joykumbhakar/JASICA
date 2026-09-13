import os

path = r"E:\Controller\app\release-notes\page.tsx"
with open(path, "r", encoding="utf-8") as f:
    text = f.read()

# Replace version and date strings
text = text.replace("Version 1.2.13.09.2026-beta", "Version 1.2.14.09.2026-beta")
text = text.replace("versionCode 12", "versionCode 13")
text = text.replace("Sept 13, 2026", "Sept 14, 2026")
text = text.replace("Commit:</strong> <code className=\"bg-white border border-zinc-200 px-1 py-0.5 rounded\">35b2f2f", "Commit:</strong> <code className=\"bg-white border border-zinc-200 px-1 py-0.5 rounded\">fdf5556")

# Prepare new section to insert right after the "What's New" title
new_section = """
            <div className="space-y-8 mb-12">
                <div>
                  <h3 className="text-xl font-bold text-zinc-800 mb-3 border-l-4 border-blue-500 pl-3">o" v1.2.14.09.2026 Major Features</h3>
                  
                  <div className="mt-6">
                    <h4 className="text-lg font-semibold text-zinc-800 mb-2">dY" Background Bluetooth Persistence</h4>
                    <ul className="list-disc pl-5 space-y-2 text-zinc-600 leading-relaxed">
                      <li><strong>Global Bluetooth Manager:</strong> The Bluetooth connection is no longer tied to the UI. The app now maintains a persistent background connection with your hardware even when you close or swipe away the app.</li>
                    </ul>
                  </div>

                  <div className="mt-6">
                    <h4 className="text-lg font-semibold text-zinc-800 mb-2">dY"s Smart Gesture Controls (Works While Locked!)</h4>
                    <ul className="list-disc pl-5 space-y-2 text-zinc-600 leading-relaxed">
                      <li><strong>Double & Triple Tap to Toggle:</strong> Tap the back of your phone twice to turn a device ON, and three times to turn it OFF. Built with a 60ms hardware debounce to prevent false triggers from vibration.</li>
                      <li><strong>Shake to Toggle:</strong> Shake your phone to turn a device on or off. Includes an Apple-style Jelly slider to adjust sensitivity, and requires 3 distinct shake peaks to prevent accidental triggers.</li>
                      <li><strong>Directional Tilt Control:</strong> Tilt your phone Left, Right, Forward, or Backward and hold for 300ms to instantly toggle up to 4 specific devices.</li>
                      <li><strong>Partial Wake Locks:</strong> All gesture sensors remain fully active even when your screen is completely turned off and locked in your pocket.</li>
                    </ul>
                  </div>

                  <div className="mt-6">
                    <h4 className="text-lg font-semibold text-zinc-800 mb-2">dYZ" Apple HIG UI Enhancements</h4>
                    <ul className="list-disc pl-5 space-y-2 text-zinc-600 leading-relaxed">
                      <li><strong>Jelly Slider:</strong> The sensitivity slider has been redesigned into a pure white glassy pill that physically scales up (bounces) when you press it, sliding over a 4dp thin Apple-style track.</li>
                      <li><strong>Mutual Exclusion:</strong> Safety checks added so Shake and Tilt gestures cannot conflict.</li>
                    </ul>
                  </div>
                </div>
            </div>
            
            <h2 className="text-2xl font-semibold text-zinc-900 mb-6 flex items-center gap-2 border-b border-zinc-100 pb-3 mt-12">dY"o Previous Updates (v1.2.13)</h2>
"""

# Insert the new section
insert_target = """<h2 className="text-2xl font-semibold text-zinc-900 mb-6 flex items-center gap-2 border-b border-zinc-100 pb-3">o" What's New</h2>"""
text = text.replace(insert_target, insert_target + new_section)

with open(path, "w", encoding="utf-8") as f:
    f.write(text)
print("Release notes page updated with v1.2.14 features!")

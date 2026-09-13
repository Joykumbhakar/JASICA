import React from 'react';
import Link from 'next/link';
import { ChevronLeft } from 'lucide-react';

export default function ReleaseNotes() {
  return (
    <div className="min-h-screen bg-[#f5f5f7] py-12 px-4 sm:px-6">
      <div className="max-w-3xl mx-auto">
        <Link href="/#version-history" className="inline-flex items-center text-blue-600 hover:text-blue-700 font-medium mb-6 transition-colors">
          <ChevronLeft className="w-5 h-5 mr-1" />
          Back to Versions
        </Link>
        
        <div className="bg-white rounded-[2rem] p-6 sm:p-12 shadow-sm border border-zinc-200">
          <h1 className="text-3xl sm:text-4xl font-bold text-zinc-900 tracking-tight mb-4">🤖 JASICA Flash-2.5 — Release Notes</h1>
          
          <div className="flex flex-wrap items-center gap-3 mb-10 text-sm">
            <span className="bg-zinc-100 text-zinc-800 px-3 py-1 rounded-full font-medium border border-zinc-200">Version 1.2.13.09.2026-beta</span>
            <span className="bg-blue-50 text-blue-700 px-3 py-1 rounded-full font-medium border border-blue-200/60">versionCode 12</span>
            <span className="text-zinc-500">Sept 13, 2026 &bull; Channel: Beta</span>
          </div>

          <section className="mb-10">
            <h2 className="text-2xl font-semibold text-zinc-900 mb-6 flex items-center gap-2 border-b border-zinc-100 pb-3">✨ What's New</h2>
            
            <div className="space-y-8">
                <div>
                  <h3 className="text-lg font-semibold text-zinc-800 mb-3">🎨 UI Overhaul — Settings Screen</h3>
                  <ul className="list-disc pl-5 space-y-2 text-zinc-600 leading-relaxed">
                    <li><strong>24dp radius</strong> applied to all settings cards for a consistent, modern squircle look.</li>
                    <li><strong>Settings header</strong> now uses a custom <code className="bg-zinc-100 px-1.5 py-0.5 rounded text-zinc-800 text-xs">Settings.png</code> icon instead of the default system icon.</li>
                    <li><strong>Hardware Config input boxes</strong> completely restyled: solid white background with visible gray outline (unfocused) and iOS-blue border (focused) — matching Apple HIG guidelines.</li>
                    <li><strong>Admin password input box</strong> for Water Reminder has been upgraded with matching soft input style.</li>
                    <li>All settings cards now use <code className="bg-zinc-100 px-1.5 py-0.5 rounded text-zinc-800 text-xs">verticalScroll</code> instead of <code className="bg-zinc-100 px-1.5 py-0.5 rounded text-zinc-800 text-xs">LazyColumn</code> — eliminating the massive <strong>frame drop / lag</strong> that occurred when scrolling through settings.</li>
                  </ul>
                </div>

                <div>
                  <h3 className="text-lg font-semibold text-zinc-800 mb-3">🔔 Custom Notification Icon</h3>
                  <ul className="list-disc pl-5 space-y-2 text-zinc-600 leading-relaxed">
                    <li>Jasica notifications now display the <strong>custom JASICA AI Waves SVG</strong> (<code className="bg-zinc-100 px-1.5 py-0.5 rounded text-zinc-800 text-xs">ic_ai_waves</code>) icon in the status bar instead of the default Android launcher icon.</li>
                  </ul>
                </div>

                <div>
                  <h3 className="text-lg font-semibold text-zinc-800 mb-3">🏠 Main Menu — Custom Icons</h3>
                  <p className="text-zinc-600 mb-3">All four main menu items now use your custom macOS-style PNG icons:</p>
                  <div className="overflow-x-auto rounded-xl border border-zinc-200">
                      <table className="min-w-full text-sm text-left text-zinc-600">
                          <thead className="text-xs text-zinc-500 bg-zinc-50 uppercase font-semibold">
                              <tr>
                                  <th className="px-4 py-3 border-b border-zinc-200">Menu Item</th>
                                  <th className="px-4 py-3 border-b border-zinc-200">Icon</th>
                              </tr>
                          </thead>
                          <tbody>
                              <tr className="border-b border-zinc-100"><td className="px-4 py-3">Manual Controls</td><td className="px-4 py-3"><code className="bg-zinc-100 px-1.5 py-0.5 rounded text-zinc-800 text-xs">Home.png</code></td></tr>
                              <tr className="border-b border-zinc-100"><td className="px-4 py-3">Chat History</td><td className="px-4 py-3"><code className="bg-zinc-100 px-1.5 py-0.5 rounded text-zinc-800 text-xs">Messages.png</code></td></tr>
                              <tr className="border-b border-zinc-100"><td className="px-4 py-3">Arduino Code</td><td className="px-4 py-3"><code className="bg-zinc-100 px-1.5 py-0.5 rounded text-zinc-800 text-xs">Arduino_IDE.png</code></td></tr>
                              <tr><td className="px-4 py-3">Settings</td><td className="px-4 py-3"><code className="bg-zinc-100 px-1.5 py-0.5 rounded text-zinc-800 text-xs">Settings.png</code></td></tr>
                          </tbody>
                      </table>
                  </div>
                </div>

                <div>
                  <h3 className="text-lg font-semibold text-zinc-800 mb-3">🔵 Bluetooth & Navigation Icons</h3>
                  <ul className="list-disc pl-5 space-y-2 text-zinc-600 leading-relaxed">
                    <li>Top bar <strong>Bluetooth icon</strong> replaced with <code className="bg-zinc-100 px-1.5 py-0.5 rounded text-zinc-800 text-xs">Bluetooth.png</code> (original colors, no tint).</li>
                    <li><strong>Three-dot menu button</strong> (<code className="bg-zinc-100 px-1.5 py-0.5 rounded text-zinc-800 text-xs">⋮</code>) replaces the old dropdown arrow icon.</li>
                  </ul>
                </div>

                <div>
                  <h3 className="text-lg font-semibold text-zinc-800 mb-3">🌟 Main Menu Background</h3>
                  <ul className="list-disc pl-5 space-y-2 text-zinc-600 leading-relaxed">
                    <li>Dropdown menu background is now <strong>solid white</strong> — removing the semi-transparent haze blur and replacing it with a crisp, clean card.</li>
                  </ul>
                </div>

                <div>
                  <h3 className="text-lg font-semibold text-zinc-800 mb-3">⚙️ Settings Rows — Custom Icons</h3>
                  <ul className="list-disc pl-5 space-y-2 text-zinc-600 leading-relaxed">
                    <li><strong>"Use Portfolio API Key"</strong> row now uses <code className="bg-zinc-100 px-1.5 py-0.5 rounded text-zinc-800 text-xs">maclaps.png</code> (no tint, transparent background).</li>
                    <li><strong>"AI Model Engine"</strong> row uses the custom JASICA AI Waves SVG icon (<code className="bg-zinc-100 px-1.5 py-0.5 rounded text-zinc-800 text-xs">ic_ai_waves</code>).</li>
                  </ul>
                </div>

                <div>
                  <h3 className="text-lg font-semibold text-zinc-800 mb-3">🖼️ Tour / Onboarding Screen</h3>
                  <div className="overflow-x-auto rounded-xl border border-zinc-200">
                      <table className="min-w-full text-sm text-left text-zinc-600">
                          <thead className="text-xs text-zinc-500 bg-zinc-50 uppercase font-semibold">
                              <tr>
                                  <th className="px-4 py-3 border-b border-zinc-200">Slide</th>
                                  <th className="px-4 py-3 border-b border-zinc-200">Image</th>
                              </tr>
                          </thead>
                          <tbody>
                              <tr className="border-b border-zinc-100"><td className="px-4 py-3">Voice Commands</td><td className="px-4 py-3"><code className="bg-zinc-100 px-1.5 py-0.5 rounded text-zinc-800 text-xs">hey_jasica.png</code></td></tr>
                              <tr className="border-b border-zinc-100"><td className="px-4 py-3">Manual Override</td><td className="px-4 py-3"><code className="bg-zinc-100 px-1.5 py-0.5 rounded text-zinc-800 text-xs">manual_control.png</code></td></tr>
                              <tr><td className="px-4 py-3">Stay Connected</td><td className="px-4 py-3"><code className="bg-zinc-100 px-1.5 py-0.5 rounded text-zinc-800 text-xs">stay_connected.png</code></td></tr>
                          </tbody>
                      </table>
                  </div>
                </div>

                <div>
                  <h3 className="text-lg font-semibold text-zinc-800 mb-3">💧 Water Reminder</h3>
                  <ul className="list-disc pl-5 space-y-2 text-zinc-600 leading-relaxed">
                    <li><strong>Smart Water Reminder toggle</strong> is now <strong>disabled by default</strong>.</li>
                    <li><strong>Water alarm animation</strong> upgraded to 3-layer overlapping fluid waves with independent speeds, phases, and gradients (<code className="text-xs">#0EA5E9</code>, <code className="text-xs">#38BDF8</code>, <code className="text-xs">#7DD3FC</code>).</li>
                    <li><strong>Admin password input</strong> restyled to match the rest of the UI.</li>
                  </ul>
                </div>

                <div>
                  <h3 className="text-lg font-semibold text-zinc-800 mb-3">🎵 Relationship & Identity</h3>
                  <ul className="list-disc pl-5 space-y-2 text-zinc-600 leading-relaxed">
                    <li><strong>Bristi's favorite song</strong> updated to <em>"Dekhechi Rupsagore moner manus"</em> (YouTube: <code className="bg-zinc-100 px-1.5 py-0.5 rounded text-zinc-800 text-xs">ReBHEyAd2zk</code>).</li>
                    <li><strong>Saikat Pal</strong> added as a known contact (Bristi's friend). Saying <em>"Play Saikat's favorite song"</em> launches <em>"Sorry Dipanita"</em> (YouTube: <code className="bg-zinc-100 px-1.5 py-0.5 rounded text-zinc-800 text-xs">lhV2bCBo-8k</code>).</li>
                  </ul>
                </div>
            </div>
          </section>

          <section className="mb-10">
            <h2 className="text-2xl font-semibold text-zinc-900 mb-6 flex items-center gap-2 border-b border-zinc-100 pb-3">🐛 Bug Fixes</h2>
            <div className="overflow-x-auto rounded-xl border border-zinc-200">
                <table className="min-w-full text-sm text-left text-zinc-600">
                    <thead className="text-xs text-zinc-500 bg-zinc-50 uppercase font-semibold">
                        <tr>
                            <th className="px-4 py-3 border-b border-zinc-200 w-12 text-center">#</th>
                            <th className="px-4 py-3 border-b border-zinc-200">Fix</th>
                        </tr>
                    </thead>
                    <tbody className="divide-y divide-zinc-100">
                        <tr><td className="px-4 py-3 text-center">1</td><td className="px-4 py-3"><strong>Settings scroll lag</strong> — Replaced <code className="bg-zinc-100 px-1 py-0.5 rounded text-xs">LazyColumn</code> with <code className="bg-zinc-100 px-1 py-0.5 rounded text-xs">verticalScroll</code> + <code className="bg-zinc-100 px-1 py-0.5 rounded text-xs">Column</code> to eliminate frame drops</td></tr>
                        <tr><td className="px-4 py-3 text-center">2</td><td className="px-4 py-3"><strong>"Reset Devices to Default"</strong> function now correctly resets all device pins and names</td></tr>
                        <tr><td className="px-4 py-3 text-center">3</td><td className="px-4 py-3"><strong>Smart Water Reminder</strong> alarm scheduling fixed to use <code className="bg-zinc-100 px-1 py-0.5 rounded text-xs">AlarmManager.setAlarmClock()</code> — bypasses Doze mode and restrictions on Android 14+</td></tr>
                        <tr><td className="px-4 py-3 text-center">4</td><td className="px-4 py-3"><strong>Water alarm screen</strong> corrupted emoji string (<code className="bg-zinc-100 px-1 py-0.5 rounded text-xs">dY'</code>) fixed to proper 💧 drop icon</td></tr>
                        <tr><td className="px-4 py-3 text-center">5</td><td className="px-4 py-3"><strong>Hardware config inputs</strong> now correctly reflect user edits after saving</td></tr>
                        <tr><td className="px-4 py-3 text-center">6</td><td className="px-4 py-3"><strong>Invalid drawable filenames</strong> cleaned from build — these caused <code className="bg-zinc-100 px-1 py-0.5 rounded text-xs">packageDebugResources</code> to fail</td></tr>
                        <tr><td className="px-4 py-3 text-center">7</td><td className="px-4 py-3"><strong>"Use Portfolio API Key" default</strong> changed to <code className="bg-zinc-100 px-1 py-0.5 rounded text-xs">OFF</code> to avoid unexpected cloud key usage</td></tr>
                    </tbody>
                </table>
            </div>
          </section>

          <section className="mb-10">
            <h2 className="text-2xl font-semibold text-zinc-900 mb-6 flex items-center gap-2 border-b border-zinc-100 pb-3">🔧 Technical Changes</h2>
            <ul className="list-disc pl-5 space-y-2 text-zinc-600 leading-relaxed">
              <li><strong>compileSdk / targetSdk:</strong> 36</li>
              <li><strong>minSdk:</strong> 23 (Android 6.0+)</li>
              <li>New drawables added: <code className="bg-zinc-100 px-1.5 py-0.5 rounded text-zinc-800 text-xs">bluetooth_icon</code>, <code className="bg-zinc-100 px-1.5 py-0.5 rounded text-zinc-800 text-xs">ic_home_custom</code>, <code className="bg-zinc-100 px-1.5 py-0.5 rounded text-zinc-800 text-xs">ic_messages_custom</code>, <code className="bg-zinc-100 px-1.5 py-0.5 rounded text-zinc-800 text-xs">ic_settings_custom</code>, <code className="bg-zinc-100 px-1.5 py-0.5 rounded text-zinc-800 text-xs">ic_maclaps</code>, <code className="bg-zinc-100 px-1.5 py-0.5 rounded text-zinc-800 text-xs">ic_ai_waves</code>, <code className="bg-zinc-100 px-1.5 py-0.5 rounded text-zinc-800 text-xs">hey_jasica</code>, <code className="bg-zinc-100 px-1.5 py-0.5 rounded text-zinc-800 text-xs">manual_control</code>, <code className="bg-zinc-100 px-1.5 py-0.5 rounded text-zinc-800 text-xs">stay_connected</code>, <code className="bg-zinc-100 px-1.5 py-0.5 rounded text-zinc-800 text-xs">settings_header</code></li>
              <li>All Bengali text injected via Python scripts (UTF-8 safe) to avoid PowerShell encoding corruption</li>
              <li>YouTube intents now use <code className="bg-zinc-100 px-1.5 py-0.5 rounded text-zinc-800 text-xs">setPackage("com.google.android.youtube")</code> with <code className="bg-zinc-100 px-1.5 py-0.5 rounded text-zinc-800 text-xs">FLAG_ACTIVITY_NEW_TASK</code> for reliable deep-linking</li>
            </ul>
          </section>

          <section className="bg-zinc-50 border border-zinc-200 rounded-xl p-6 text-sm text-zinc-600">
            <h3 className="font-semibold text-zinc-800 mb-2">📦 Download</h3>
            <p><strong>Branch:</strong> <code className="bg-white border border-zinc-200 px-1 py-0.5 rounded">main</code> &bull; <strong>Commit:</strong> <code className="bg-white border border-zinc-200 px-1 py-0.5 rounded">35b2f2f</code></p>
            <p className="mt-1">Build: <code>assembleDebug</code> — APK located at <code>app/build/outputs/apk/debug/app-debug.apk</code></p>
          </section>
          
          <div className="mt-10 text-center text-sm text-zinc-400 italic">
            Built with ❤️ by Joy Kumbhakar for Bristi — JASICA AI, Durgapur, West Bengal.
          </div>
        </div>
      </div>
    </div>
  );
}

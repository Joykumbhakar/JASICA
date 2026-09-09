"use client";

import React from "react";
import Navbar from "@/components/Navbar";
import { Smartphone, Download, Bluetooth, Radio, Shield, Zap, QrCode } from "lucide-react";

export default function AndroidPage() {
  return (
    <div className="min-h-screen bg-[#06070d] text-white flex flex-col selection:bg-cyan-500 selection:text-black">
      <Navbar />

      <main className="relative z-10 flex-1 max-w-7xl w-full mx-auto px-4 py-8">
        
        {/* Banner */}
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 pb-6 border-b border-white/10">
          <div>
            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-cyan-950/60 border border-cyan-500/30 text-cyan-400 text-xs font-mono mb-2">
              <Smartphone className="w-3.5 h-3.5" strokeWidth={1.5} />
              <span className="underline underline-offset-4 decoration-cyan-400/50">MOBILE CLIENT & APK DOWNLOAD</span>
            </div>
            <h1 className="text-3xl sm:text-4xl font-black font-orbitron text-white underline underline-offset-8 decoration-cyan-400/40">
              JASICA Android App
            </h1>
            <p className="text-sm text-white/60 font-mono mt-2 underline underline-offset-4 decoration-white/20">
              Direct HC-05 / HC-06 Bluetooth Voice & Hardware Remote Control
            </p>
          </div>

          <div className="flex items-center gap-3">
            <button
              onClick={() => alert("APK generation is linked to android_app/ build output. You can run 'gradlew assembleRelease' in android_app directory.")}
              className="px-5 py-2.5 rounded-xl bg-gradient-to-r from-cyan-500 to-blue-600 text-black text-xs font-bold flex items-center gap-2 shadow-glow hover:opacity-95 transition underline underline-offset-4 decoration-black/40"
            >
              <Download className="w-4 h-4" strokeWidth={1.5} />
              <span>Download Android APK</span>
            </button>
          </div>
        </div>

        {/* Feature Highlights Grid */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mt-8">
          <div className="p-6 rounded-2xl bg-[#0e101f]/80 border border-cyan-500/20 backdrop-blur-xl">
            <div className="p-3 rounded-xl bg-cyan-950/80 border border-cyan-500/40 text-cyan-400 w-fit mb-4">
              <Bluetooth className="w-6 h-6" strokeWidth={1.5} />
            </div>
            <h3 className="text-base font-bold font-orbitron text-white mb-2 underline underline-offset-4 decoration-cyan-400/40">Instant HC-05 Bluetooth Pairing</h3>
            <p className="text-xs text-white/60 font-mono leading-relaxed">
              Auto-discovers and connects with your Arduino UNO Bluetooth module with zero latency communication.
            </p>
          </div>

          <div className="p-6 rounded-2xl bg-[#0e101f]/80 border border-purple-500/20 backdrop-blur-xl">
            <div className="p-3 rounded-xl bg-purple-950/80 border border-purple-500/40 text-purple-400 w-fit mb-4">
              <Radio className="w-6 h-6" strokeWidth={1.5} />
            </div>
            <h3 className="text-base font-bold font-orbitron text-white mb-2 underline underline-offset-4 decoration-purple-400/40">Continuous Voice Recognition</h3>
            <p className="text-xs text-white/60 font-mono leading-relaxed">
              Hands-free voice recognition on Android. Trigger commands like &ldquo;Turn on PC&rdquo; or &ldquo;Mood Mode&rdquo; anytime.
            </p>
          </div>

          <div className="p-6 rounded-2xl bg-[#0e101f]/80 border border-emerald-500/20 backdrop-blur-xl">
            <div className="p-3 rounded-xl bg-emerald-950/80 border border-emerald-500/40 text-emerald-400 w-fit mb-4">
              <Zap className="w-6 h-6" strokeWidth={1.5} />
            </div>
            <h3 className="text-base font-bold font-orbitron text-white mb-2 underline underline-offset-4 decoration-emerald-400/40">Haptic Feedback & Soundwaves</h3>
            <p className="text-xs text-white/60 font-mono leading-relaxed">
              Rich haptic vibrations and animated cyber UI on every button tap and hardware toggle.
            </p>
          </div>
        </div>

        {/* Pairing Instructions */}
        <div className="mt-8 p-6 rounded-2xl bg-[#0a0c16] border border-white/10 flex flex-col md:flex-row items-center justify-between gap-6">
          <div className="space-y-2">
            <h3 className="text-lg font-bold font-orbitron text-white underline underline-offset-4 decoration-white/30">How to Pair with HC-05 on Android</h3>
            <ol className="list-decimal list-inside space-y-1.5 text-xs font-mono text-white/70">
              <li>Open Android Settings &gt; Bluetooth and pair with <span className="text-cyan-400 font-bold underline underline-offset-2 decoration-cyan-400/50">&quot;HC-05&quot;</span> (PIN: 1234 or 0000).</li>
              <li>Launch the JASICA Android App and select your paired device.</li>
              <li>Tap the microphone icon or tap any of the relay cards to switch hardware instantly.</li>
            </ol>
          </div>

          <div className="flex flex-col items-center p-4 rounded-xl bg-black/60 border border-cyan-500/30">
            <QrCode className="w-24 h-24 text-cyan-400" strokeWidth={1.5} />
            <span className="text-[10px] font-mono text-white/50 mt-2 underline underline-offset-2 decoration-white/20">Scan from Android Device</span>
          </div>
        </div>

      </main>
    </div>
  );
}

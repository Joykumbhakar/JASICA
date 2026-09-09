"use client";

import React from "react";
import { Mic, Activity, Wifi, ShieldAlert, Cpu } from "lucide-react";

interface HudVisualizerProps {
  isListening: boolean;
  voiceFeedback: string;
  onMicClick: () => void;
  serialConnected: boolean;
  activeCommand?: string;
}

export default function HudVisualizer({
  isListening,
  voiceFeedback,
  onMicClick,
  serialConnected,
  activeCommand,
}: HudVisualizerProps) {
  return (
    <div className="relative w-full flex flex-col items-center justify-center py-6 px-4">
      {/* Background Ambient Glow Blobs */}
      <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[340px] h-[340px] bg-gradient-to-r from-orange-500/20 via-purple-600/25 to-cyan-500/20 rounded-full blur-3xl pointer-events-none animate-pulse" />

      {/* Main Hologram Container */}
      <div className="relative w-64 h-64 sm:w-80 sm:h-80 flex items-center justify-center">
        {/* Outer Rotating HUD Ring 1 */}
        <div className="absolute inset-0 rounded-full border-[1.5px] border-dashed border-cyan-400/30 animate-spin-slow pointer-events-none" />
        
        {/* Inner Counter-Rotating HUD Ring 2 */}
        <div className="absolute inset-4 rounded-full border border-purple-500/40 border-t-transparent border-b-transparent animate-spin-reverse pointer-events-none" />
        
        {/* Fine HUD Markings */}
        <div className="absolute inset-8 rounded-full border border-cyan-500/20 pointer-events-none flex items-center justify-center">
          <div className="absolute top-0 w-2 h-0.5 bg-cyan-400" />
          <div className="absolute bottom-0 w-2 h-0.5 bg-cyan-400" />
          <div className="absolute left-0 h-2 w-0.5 bg-purple-400" />
          <div className="absolute right-0 h-2 w-0.5 bg-purple-400" />
        </div>

        {/* Dynamic Sound Wave Bars (Behind Orb) */}
        <div className="absolute inset-x-0 h-28 flex items-center justify-center gap-1.5 opacity-70 pointer-events-none">
          {[40, 65, 85, 95, 70, 50, 80, 100, 75, 45, 90, 60, 30].map((h, i) => (
            <div
              key={i}
              className={`w-1 rounded-full bg-gradient-to-t from-cyan-500 to-purple-500 transition-all duration-150 ${
                isListening ? "animate-pulse" : "opacity-40"
              }`}
              style={{
                height: isListening ? `${Math.max(15, (h * 0.8) + 15)}%` : `${h * 0.35}%`,
              }}
            />
          ))}
        </div>

        {/* Central Glowing Hologram Orb */}
        <div className="relative z-10 group cursor-pointer" onClick={onMicClick}>
          <div className={`relative w-36 h-36 sm:w-44 sm:h-44 rounded-full p-2 transition-transform duration-300 group-hover:scale-105 ${
            isListening ? "scale-105" : ""
          }`}>
            <img
              src="/jasica.png"
              alt="JASICA Core"
              className="w-full h-full object-contain rounded-full drop-shadow-[0_0_35px_rgba(0,242,254,0.65)]"
            />
            {isListening && (
              <div className="absolute inset-0 rounded-full border-2 border-cyan-400 animate-ping opacity-75" />
            )}
          </div>
        </div>

        {/* Floating Telemetry Badges */}
        <div className="absolute top-2 left-2 flex items-center gap-1.5 bg-black/60 backdrop-blur-md px-2.5 py-1 rounded-full border border-cyan-500/30 text-[10px] font-mono text-cyan-300">
          <Activity className="w-3 h-3 text-cyan-400 animate-pulse" strokeWidth={1.5} />
          <span className="underline underline-offset-2 decoration-cyan-400/40">LINK: {serialConnected ? "SERIAL 9600" : "CLOUD API"}</span>
        </div>

        <div className="absolute bottom-2 right-2 flex items-center gap-1.5 bg-black/60 backdrop-blur-md px-2.5 py-1 rounded-full border border-purple-500/30 text-[10px] font-mono text-purple-300">
          <Cpu className="w-3 h-3 text-purple-400" strokeWidth={1.5} />
          <span className="underline underline-offset-2 decoration-purple-400/40">CORE: ARDUINO UNO</span>
        </div>
      </div>

      {/* Voice Assistant Feedback Box */}
      <div className="mt-4 text-center max-w-lg">
        <div className="inline-flex items-center gap-2 px-4 py-1.5 rounded-full bg-cyan-950/40 border border-cyan-500/30 text-cyan-300 text-xs font-mono tracking-wide shadow-glow">
          <span className="w-2 h-2 rounded-full bg-cyan-400 animate-ping" />
          <span className="font-semibold uppercase underline underline-offset-4 decoration-cyan-400/50">
            {isListening ? "LISTENING FOR VOICE COMMAND..." : (voiceFeedback || "SYSTEM READY // SAY 'JASICA'")}
          </span>
        </div>
        {activeCommand && (
          <p className="text-[11px] font-mono text-emerald-400 mt-1.5 underline underline-offset-2 decoration-emerald-500/40">
            [EXECUTED]: {activeCommand}
          </p>
        )}
      </div>
    </div>
  );
}

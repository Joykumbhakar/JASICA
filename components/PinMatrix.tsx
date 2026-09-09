"use client";

import React, { useState } from "react";
import { 
  Monitor, 
  Sparkles, 
  Lightbulb, 
  Plug, 
  Power, 
  Sliders, 
  Moon, 
  Sun, 
  Zap, 
  CheckCircle2, 
  RotateCcw,
  Layers
} from "lucide-react";

interface PinMatrixProps {
  deviceStates: {
    pc: boolean;
    rgb: boolean;
    roomLight: boolean;
    plug: boolean;
  };
  onSendCommand: (cmd: string, desc: string) => void;
  serialConnected: boolean;
}

export default function PinMatrix({
  deviceStates,
  onSendCommand,
  serialConnected,
}: PinMatrixProps) {
  const [showExtendedPins, setShowExtendedPins] = useState(false);
  const [extendedPins, setExtendedPins] = useState<Record<number, boolean>>({
    2: false, 3: false, 4: false, 5: false, 6: false, 7: false,
    8: false, 9: false, 10: false, 11: false, 12: false, 13: false
  });

  const relayCards = [
    {
      id: "pc",
      name: "PC Power",
      pin: "Pin D4",
      cmdOn: "a",
      cmdOff: "A",
      icon: Monitor,
      active: deviceStates.pc,
      color: "from-cyan-500 to-blue-600",
      borderActive: "border-cyan-400 shadow-glow",
    },
    {
      id: "rgb",
      name: "RGB Ambience",
      pin: "Pin D5",
      cmdOn: "b",
      cmdOff: "B",
      icon: Sparkles,
      active: deviceStates.rgb,
      color: "from-purple-500 to-pink-600",
      borderActive: "border-purple-400 shadow-glow-purple",
    },
    {
      id: "roomLight",
      name: "Room Light",
      pin: "Pin D6",
      cmdOn: "c",
      cmdOff: "C",
      icon: Lightbulb,
      active: deviceStates.roomLight,
      color: "from-amber-400 to-orange-500",
      borderActive: "border-amber-400 shadow-[0_0_25px_rgba(251,191,36,0.4)]",
    },
    {
      id: "plug",
      name: "Power Plug",
      pin: "Pin D7",
      cmdOn: "d",
      cmdOff: "D",
      icon: Plug,
      active: deviceStates.plug,
      color: "from-emerald-400 to-teal-600",
      borderActive: "border-emerald-400 shadow-glow-green",
    },
  ];

  const handleToggle = (card: typeof relayCards[0]) => {
    if (card.active) {
      onSendCommand(card.cmdOff, `Turned OFF ${card.name}`);
    } else {
      onSendCommand(card.cmdOn, `Turned ON ${card.name}`);
    }
  };

  const handleTogglePin = (pin: number) => {
    const next = !extendedPins[pin];
    setExtendedPins(prev => ({ ...prev, [pin]: next }));
    onSendCommand(`PIN${pin}:${next ? "1" : "0"}`, `Toggled Pin D${pin} ${next ? "HIGH" : "LOW"}`);
  };

  return (
    <div className="w-full max-w-4xl mx-auto px-4 mt-2">
      
      {/* 4 Core Active-LOW Relays */}
      <div className="grid grid-cols-2 sm:grid-cols-4 gap-3 sm:gap-4">
        {relayCards.map((card) => {
          const Icon = card.icon;
          return (
            <button
              key={card.id}
              onClick={() => handleToggle(card)}
              className={`relative overflow-hidden group p-4 rounded-2xl border transition-all duration-300 flex flex-col items-center text-center backdrop-blur-xl ${
                card.active
                  ? `bg-white/[0.09] ${card.borderActive} scale-[1.02]`
                  : "bg-[#121424]/70 border-cyan-500/20 hover:border-cyan-400/50 hover:bg-white/[0.05]"
              }`}
            >
              {/* Active Glow Gradient Background */}
              {card.active && (
                <div className={`absolute -top-12 -right-12 w-28 h-28 bg-gradient-to-br ${card.color} rounded-full blur-2xl opacity-40 pointer-events-none`} />
              )}

              {/* Status Dot */}
              <div className="absolute top-3 right-3 flex items-center gap-1.5">
                <span className={`w-2 h-2 rounded-full ${card.active ? "bg-emerald-400 shadow-glow-green" : "bg-white/20"}`} />
              </div>

              {/* Icon Container with Thin Stroke */}
              <div className={`w-12 h-12 rounded-xl flex items-center justify-center mb-3 transition-transform duration-300 group-hover:scale-110 ${
                card.active 
                  ? `bg-gradient-to-br ${card.color} text-white shadow-md` 
                  : "bg-cyan-950/60 border border-cyan-500/30 text-cyan-400"
              }`}>
                <Icon className="w-6 h-6" strokeWidth={1.5} />
              </div>

              <span className="font-semibold text-sm text-white tracking-wide underline underline-offset-4 decoration-current/30 group-hover:decoration-current">
                {card.name}
              </span>
              
              <div className="flex items-center gap-1.5 mt-1">
                <span className="text-[10px] font-mono text-white/50 underline underline-offset-2 decoration-white/20">{card.pin}</span>
                <span className="text-[10px] font-mono text-white/40">•</span>
                <span className={`text-[10px] font-bold font-mono underline underline-offset-2 ${card.active ? "text-emerald-400 decoration-emerald-500/40" : "text-white/40 decoration-white/20"}`}>
                  {card.active ? "ACTIVE" : "STANDBY"}
                </span>
              </div>

              {/* Signal Trigger Badge */}
              <div className="mt-3 px-2 py-0.5 rounded bg-black/40 border border-white/10 text-[9px] font-mono text-cyan-300/80">
                SIG: {card.active ? card.cmdOff : card.cmdOn}
              </div>
            </button>
          );
        })}
      </div>

      {/* Preset Action Macro Bar */}
      <div className="mt-4 p-3 rounded-2xl bg-[#0c0e1a]/80 border border-white/10 backdrop-blur-xl flex flex-wrap items-center justify-between gap-2">
        <div className="flex items-center gap-2 flex-wrap">
          <span className="text-xs font-mono text-white/50 pl-2 hidden sm:inline underline underline-offset-4 decoration-white/20">MACROS:</span>
          
          <button
            onClick={() => onSendCommand("on", "Enabled All Relays")}
            className="flex items-center gap-1.5 px-3 py-1.5 rounded-xl bg-emerald-500/10 border border-emerald-500/30 text-emerald-300 hover:bg-emerald-500/20 text-xs font-semibold transition underline underline-offset-4 decoration-emerald-400/40"
          >
            <Sun className="w-3.5 h-3.5" strokeWidth={1.5} />
            <span>ALL ON</span>
          </button>

          <button
            onClick={() => onSendCommand("off", "Disabled All Relays")}
            className="flex items-center gap-1.5 px-3 py-1.5 rounded-xl bg-rose-500/10 border border-rose-500/30 text-rose-300 hover:bg-rose-500/20 text-xs font-semibold transition underline underline-offset-4 decoration-rose-400/40"
          >
            <Power className="w-3.5 h-3.5" strokeWidth={1.5} />
            <span>ALL OFF</span>
          </button>

          <button
            onClick={() => onSendCommand("mood", "Activated Stark Tower Mood Mode")}
            className="flex items-center gap-1.5 px-3 py-1.5 rounded-xl bg-purple-500/10 border border-purple-500/30 text-purple-300 hover:bg-purple-500/20 text-xs font-semibold transition underline underline-offset-4 decoration-purple-400/40"
          >
            <Moon className="w-3.5 h-3.5" strokeWidth={1.5} />
            <span>MOOD MODE</span>
          </button>

          <button
            onClick={() => onSendCommand("status", "Queried Hardware Status")}
            className="flex items-center gap-1.5 px-3 py-1.5 rounded-xl bg-cyan-500/10 border border-cyan-500/30 text-cyan-300 hover:bg-cyan-500/20 text-xs font-semibold transition underline underline-offset-4 decoration-cyan-400/40"
          >
            <RotateCcw className="w-3.5 h-3.5" strokeWidth={1.5} />
            <span>STATUS</span>
          </button>
        </div>

        {/* Extended 12-Pin Matrix View Toggle */}
        <button
          onClick={() => setShowExtendedPins(!showExtendedPins)}
          className={`flex items-center gap-1.5 px-3 py-1.5 rounded-xl text-xs font-medium border transition ${
            showExtendedPins 
              ? "bg-cyan-500/20 border-cyan-400 text-cyan-300 shadow-glow" 
              : "bg-white/5 border-white/10 text-white/70 hover:text-white"
          }`}
        >
          <Layers className="w-3.5 h-3.5" strokeWidth={1.5} />
          <span className="underline underline-offset-4 decoration-current/40">{showExtendedPins ? "HIDE 12-PIN MATRIX" : "12-PIN MATRIX (D2-D13)"}</span>
        </button>
      </div>

      {/* Extended 12-Pin Digital Matrix */}
      {showExtendedPins && (
        <div className="mt-3 p-4 rounded-2xl bg-[#090a14]/95 border border-cyan-500/30 backdrop-blur-2xl animate-in fade-in slide-in-from-top-2 duration-300">
          <div className="flex items-center justify-between mb-3">
            <div className="flex items-center gap-2">
              <Zap className="w-4 h-4 text-cyan-400" strokeWidth={1.5} />
              <span className="font-orbitron font-bold text-xs text-white uppercase tracking-wider underline underline-offset-4 decoration-cyan-400/40">
                Full Arduino UNO Digital Bus (D2 - D13)
              </span>
            </div>
            <span className="text-[10px] font-mono text-cyan-400/70 underline underline-offset-2 decoration-cyan-400/30">
              Active-LOW & TTL Output Logic
            </span>
          </div>

          <div className="grid grid-cols-3 sm:grid-cols-6 gap-2">
            {[2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13].map((pin) => {
              const isHigh = extendedPins[pin] || (
                (pin === 4 && deviceStates.pc) ||
                (pin === 5 && deviceStates.rgb) ||
                (pin === 6 && deviceStates.roomLight) ||
                (pin === 7 && deviceStates.plug)
              );
              const label = pin === 2 ? "BT RX" : pin === 3 ? "BT TX" : pin === 4 ? "PC" : pin === 5 ? "RGB" : pin === 6 ? "LIGHT" : pin === 7 ? "PLUG" : pin === 13 ? "LED" : `AUX ${pin}`;

              return (
                <button
                  key={pin}
                  onClick={() => handleTogglePin(pin)}
                  className={`p-2 rounded-xl border text-left flex flex-col justify-between transition ${
                    isHigh
                      ? "bg-cyan-500/20 border-cyan-400 text-cyan-200 shadow-glow"
                      : "bg-white/[0.03] border-white/10 text-white/60 hover:border-cyan-500/40"
                  }`}
                >
                  <div className="flex items-center justify-between">
                    <span className="text-xs font-mono font-bold underline underline-offset-2 decoration-current/30">D{pin}</span>
                    <span className={`w-1.5 h-1.5 rounded-full ${isHigh ? "bg-cyan-400 shadow-glow" : "bg-white/20"}`} />
                  </div>
                  <span className="text-[10px] font-mono mt-1 text-white/50 truncate">{label}</span>
                  <span className={`text-[9px] font-mono font-bold mt-1 underline underline-offset-2 ${isHigh ? "text-emerald-400 decoration-emerald-500/40" : "text-white/30 decoration-white/20"}`}>
                    {isHigh ? "HIGH (ON)" : "LOW (OFF)"}
                  </span>
                </button>
              );
            })}
          </div>
        </div>
      )}

    </div>
  );
}

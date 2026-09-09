"use client";

import React, { useState, useEffect, useRef } from "react";
import { 
  Terminal, 
  X, 
  Trash2, 
  Send, 
  Check, 
  Download, 
  Usb, 
  Bluetooth, 
  Radio,
  Sliders,
  Play
} from "lucide-react";

interface SerialTerminalProps {
  isOpen: boolean;
  onClose: () => void;
  logs: Array<{ id: string; text: string; type: "tx" | "rx" | "system" | "error"; time: string }>;
  onClearLogs: () => void;
  onSendRaw: (cmd: string) => void;
  serialConnected: boolean;
  onToggleSerial: () => void;
  baudRate: number;
  onChangeBaudRate: (rate: number) => void;
}

export default function SerialTerminal({
  isOpen,
  onClose,
  logs,
  onClearLogs,
  onSendRaw,
  serialConnected,
  onToggleSerial,
  baudRate,
  onChangeBaudRate,
}: SerialTerminalProps) {
  const [inputVal, setInputVal] = useState("");
  const logsEndRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (isOpen) {
      logsEndRef.current?.scrollIntoView({ behavior: "smooth" });
    }
  }, [logs, isOpen]);

  if (!isOpen) return null;

  const handleSend = (e: React.FormEvent) => {
    e.preventDefault();
    if (!inputVal.trim()) return;
    onSendRaw(inputVal.trim());
    setInputVal("");
  };

  const presetChips = [
    { label: "a (PC ON)", cmd: "a" },
    { label: "A (PC OFF)", cmd: "A" },
    { label: "b (RGB ON)", cmd: "b" },
    { label: "B (RGB OFF)", cmd: "B" },
    { label: "c (Light ON)", cmd: "c" },
    { label: "C (Light OFF)", cmd: "C" },
    { label: "d (Plug ON)", cmd: "d" },
    { label: "D (Plug OFF)", cmd: "D" },
    { label: "on", cmd: "on" },
    { label: "off", cmd: "off" },
    { label: "mood", cmd: "mood" },
    { label: "status", cmd: "status" },
  ];

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/70 backdrop-blur-md animate-in fade-in duration-200">
      <div className="w-full max-w-2xl bg-[#090b14] border border-cyan-500/40 rounded-2xl shadow-2xl overflow-hidden flex flex-col h-[520px] max-h-[90vh]">
        
        {/* Terminal Header */}
        <div className="flex items-center justify-between px-4 py-3 bg-white/[0.04] border-b border-white/10">
          <div className="flex items-center gap-2.5">
            <div className="p-1.5 rounded-lg bg-cyan-950 border border-cyan-500/30 text-cyan-400">
              <Terminal className="w-4 h-4" strokeWidth={1.5} />
            </div>
            <div>
              <h3 className="text-xs font-orbitron font-bold text-white tracking-wider underline underline-offset-4 decoration-cyan-400/50">
                JASICA HARDWARE SERIAL MONITOR
              </h3>
              <p className="text-[10px] font-mono text-white/50">
                Direct Web Serial & Bluetooth Low-Latency Console
              </p>
            </div>
          </div>

          <div className="flex items-center gap-2">
            {/* Baud Rate Selector */}
            <select
              value={baudRate}
              onChange={(e) => onChangeBaudRate(Number(e.target.value))}
              className="bg-black/60 border border-white/15 rounded-lg text-[11px] font-mono text-cyan-300 px-2 py-1 outline-none focus:border-cyan-400"
            >
              {[9600, 19200, 38400, 57600, 115200].map((rate) => (
                <option key={rate} value={rate}>
                  {rate} baud
                </option>
              ))}
            </select>

            {/* Clear Logs */}
            <button
              onClick={onClearLogs}
              className="p-1.5 rounded-lg bg-white/5 border border-white/10 text-white/60 hover:text-rose-400 hover:border-rose-400/40 transition"
              title="Clear Terminal Output"
            >
              <Trash2 className="w-4 h-4" strokeWidth={1.5} />
            </button>

            {/* Close */}
            <button
              onClick={onClose}
              className="p-1.5 rounded-lg bg-white/5 border border-white/10 text-white/60 hover:text-white hover:bg-white/10 transition"
            >
              <X className="w-4 h-4" strokeWidth={1.5} />
            </button>
          </div>
        </div>

        {/* Quick Test Chips */}
        <div className="flex items-center gap-1.5 px-4 py-2 bg-black/40 border-b border-white/5 overflow-x-auto no-scrollbar">
          <span className="text-[10px] font-mono text-white/40 uppercase whitespace-nowrap mr-1 underline underline-offset-2 decoration-white/20">QUICK SEND:</span>
          {presetChips.map((chip) => (
            <button
              key={chip.cmd}
              onClick={() => onSendRaw(chip.cmd)}
              className="px-2.5 py-0.5 rounded-lg bg-cyan-950/50 border border-cyan-500/30 text-cyan-300 hover:bg-cyan-500/20 text-[10px] font-mono whitespace-nowrap transition underline underline-offset-2 decoration-cyan-400/40"
            >
              {chip.label}
            </button>
          ))}
        </div>

        {/* Terminal Log Output Window */}
        <div className="flex-1 p-4 overflow-y-auto font-mono text-xs space-y-1.5 bg-[#05060b]">
          {logs.length === 0 ? (
            <div className="h-full flex flex-col items-center justify-center text-white/30 text-center">
              <Radio className="w-8 h-8 mb-2 opacity-50 text-cyan-400 animate-pulse" strokeWidth={1.5} />
              <p className="underline underline-offset-4 decoration-white/20">NO ACTIVE DATA STREAM</p>
              <p className="text-[10px] text-white/20 mt-1">Connect USB Serial or send commands via the quick bar below</p>
            </div>
          ) : (
            logs.map((log) => (
              <div key={log.id} className="flex items-start gap-2 leading-relaxed">
                <span className="text-[10px] text-white/30 select-none">[{log.time}]</span>
                <span className={`text-[10px] font-bold px-1 rounded select-none ${
                  log.type === "tx" 
                    ? "bg-cyan-950 text-cyan-400 border border-cyan-500/30" 
                    : log.type === "rx"
                    ? "bg-emerald-950 text-emerald-400 border border-emerald-500/30"
                    : log.type === "error"
                    ? "bg-rose-950 text-rose-400 border border-rose-500/30"
                    : "bg-purple-950 text-purple-400 border border-purple-500/30"
                }`}>
                  {log.type.toUpperCase()}
                </span>
                <span className={`${
                  log.type === "tx" ? "text-cyan-200" :
                  log.type === "rx" ? "text-emerald-300 font-semibold" :
                  log.type === "error" ? "text-rose-300" : "text-purple-200"
                }`}>
                  {log.text}
                </span>
              </div>
            ))
          )}
          <div ref={logsEndRef} />
        </div>

        {/* Command Input Bar */}
        <form onSubmit={handleSend} className="p-3 bg-[#0c0e1a] border-t border-white/10 flex items-center gap-2">
          <span className="text-cyan-400 font-mono text-xs pl-2 font-bold select-none">&gt;&gt;</span>
          <input
            type="text"
            value={inputVal}
            onChange={(e) => setInputVal(e.target.value)}
            placeholder="Type serial command (e.g., 'a', 'mood', 'status') and press Enter..."
            className="flex-1 bg-black/50 border border-white/15 rounded-xl px-3 py-2 text-xs font-mono text-white placeholder-white/30 outline-none focus:border-cyan-400 transition"
          />
          <button
            type="submit"
            className="px-4 py-2 rounded-xl bg-gradient-to-r from-cyan-500 to-blue-600 text-black font-bold text-xs flex items-center gap-1.5 shadow-glow hover:opacity-90 transition underline underline-offset-4 decoration-black/40"
          >
            <Send className="w-3.5 h-3.5" strokeWidth={1.5} />
            <span>SEND</span>
          </button>
        </form>

      </div>
    </div>
  );
}

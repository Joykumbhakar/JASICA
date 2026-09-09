"use client";

import React, { useState, useEffect, useRef } from "react";
import Navbar from "@/components/Navbar";
import HudVisualizer from "@/components/HudVisualizer";
import PinMatrix from "@/components/PinMatrix";
import SerialTerminal from "@/components/SerialTerminal";
import { Mic, Terminal, Radio, Zap, Shield, Sparkles, Volume2 } from "lucide-react";

export default function HomePage() {
  const [isListening, setIsListening] = useState(false);
  const [voiceFeedback, setVoiceFeedback] = useState("SYSTEM READY // SAY 'JASICA' OR CLICK MIC");
  const [activeCommand, setActiveCommand] = useState("");
  const [voiceEnabled, setVoiceEnabled] = useState(true);
  
  // Hardware States
  const [deviceStates, setDeviceStates] = useState({
    pc: false,
    rgb: false,
    roomLight: false,
    plug: false,
  });

  // Web Serial & Terminal
  const [serialConnected, setSerialConnected] = useState(false);
  const [terminalOpen, setTerminalOpen] = useState(false);
  const [baudRate, setBaudRate] = useState(9600);
  const [logs, setLogs] = useState<Array<{ id: string; text: string; type: "tx" | "rx" | "system" | "error"; time: string }>>([
    {
      id: "init",
      text: "JASICA Cloud Bridge Initialized. Ready for Serial or Webhook input.",
      type: "system",
      time: new Date().toLocaleTimeString(),
    },
  ]);

  const portRef = useRef<any>(null);
  const readerRef = useRef<any>(null);
  const writerRef = useRef<any>(null);
  const recognitionRef = useRef<any>(null);

  // Add Log Helper
  const addLog = (text: string, type: "tx" | "rx" | "system" | "error" = "system") => {
    setLogs((prev) => [
      ...prev.slice(-60),
      {
        id: Math.random().toString(36).substring(2, 9),
        text,
        type,
        time: new Date().toLocaleTimeString(),
      },
    ]);
  };

  // Speech Synthesizer
  const speakText = (text: string) => {
    if (!voiceEnabled || typeof window === "undefined" || !("speechSynthesis" in window)) return;
    try {
      window.speechSynthesis.cancel();
      const utterance = new SpeechSynthesisUtterance(text);
      utterance.pitch = 1.05;
      utterance.rate = 1.0;
      const voices = window.speechSynthesis.getVoices();
      const preferred = voices.find(v => v.name.includes("Google") || v.name.includes("Natural") || v.name.includes("Samantha") || v.lang.startsWith("en"));
      if (preferred) utterance.voice = preferred;
      window.speechSynthesis.speak(utterance);
    } catch (e) {
      console.warn("Speech synthesis error:", e);
    }
  };

  // Send Command to Serial Port & Cloud API
  const sendCommand = async (cmd: string, description?: string) => {
    const time = new Date().toLocaleTimeString();
    addLog(`CMD >> "${cmd}" (${description || "User Trigger"})`, "tx");
    setActiveCommand(description || cmd);

    // 1. Send via Web Serial if active
    if (serialConnected && writerRef.current) {
      try {
        const encoder = new TextEncoder();
        await writerRef.current.write(encoder.encode(cmd + "\n"));
        addLog(`Sent "${cmd}" to USB Serial @ ${baudRate} baud`, "tx");
      } catch (err: any) {
        addLog(`Serial Write Error: ${err.message}`, "error");
      }
    }

    // 2. Sync with Next.js Backend REST API
    try {
      const res = await fetch("/api/command", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ command: cmd, source: "WEB_CONSOLE" }),
      });
      const data = await res.json();
      if (data.deviceStates) {
        setDeviceStates(data.deviceStates);
      }
    } catch (e) {
      console.warn("API Sync notice:", e);
    }

    // 3. Local State Update for immediate UI responsiveness
    if (cmd === "a") setDeviceStates((prev) => ({ ...prev, pc: true }));
    else if (cmd === "A") setDeviceStates((prev) => ({ ...prev, pc: false }));
    else if (cmd === "b") setDeviceStates((prev) => ({ ...prev, rgb: true }));
    else if (cmd === "B") setDeviceStates((prev) => ({ ...prev, rgb: false }));
    else if (cmd === "c") setDeviceStates((prev) => ({ ...prev, roomLight: true }));
    else if (cmd === "C") setDeviceStates((prev) => ({ ...prev, roomLight: false }));
    else if (cmd === "d") setDeviceStates((prev) => ({ ...prev, plug: true }));
    else if (cmd === "D") setDeviceStates((prev) => ({ ...prev, plug: false }));
    else if (cmd === "on" || cmd === "all on") setDeviceStates({ pc: true, rgb: true, roomLight: true, plug: true });
    else if (cmd === "off" || cmd === "all off") setDeviceStates({ pc: false, rgb: false, roomLight: false, plug: false });
    else if (cmd === "mood") setDeviceStates((prev) => ({ ...prev, roomLight: false, rgb: true }));
  };

  // Voice Command Interpreter
  const interpretVoice = (transcript: string) => {
    const raw = transcript.toLowerCase();
    setVoiceFeedback(`Heard: "${transcript}"`);

    if (raw.includes("pc on") || raw.includes("turn on pc") || raw.includes("computer on")) {
      sendCommand("a", "PC Powered ON");
      speakText("Powering on your workstation PC.");
    } else if (raw.includes("pc off") || raw.includes("turn off pc") || raw.includes("computer off")) {
      sendCommand("A", "PC Powered OFF");
      speakText("Shutting down workstation PC.");
    } else if (raw.includes("rgb on") || raw.includes("ambient on") || raw.includes("lights on rgb")) {
      sendCommand("b", "RGB Ambience ON");
      speakText("Ambient RGB illumination active.");
    } else if (raw.includes("rgb off") || raw.includes("turn off rgb")) {
      sendCommand("B", "RGB Ambience OFF");
      speakText("RGB lighting disabled.");
    } else if (raw.includes("room light on") || raw.includes("main light on") || raw.includes("turn on light")) {
      sendCommand("c", "Room Light ON");
      speakText("Main room lighting turned on.");
    } else if (raw.includes("room light off") || raw.includes("main light off") || raw.includes("turn off light")) {
      sendCommand("C", "Room Light OFF");
      speakText("Room lights turned off.");
    } else if (raw.includes("plug on") || raw.includes("socket on") || raw.includes("turn on plug")) {
      sendCommand("d", "Power Plug ON");
      speakText("Power outlet socket activated.");
    } else if (raw.includes("plug off") || raw.includes("turn off plug")) {
      sendCommand("D", "Power Plug OFF");
      speakText("Power outlet disabled.");
    } else if (raw.includes("all on") || raw.includes("turn everything on") || raw.includes("all devices on")) {
      sendCommand("on", "All Relays ON");
      speakText("All hardware relays engaged.");
    } else if (raw.includes("all off") || raw.includes("turn everything off") || raw.includes("sleep mode") || raw.includes("good night")) {
      sendCommand("off", "All Relays OFF");
      speakText("Deactivating all hardware channels. Good night.");
    } else if (raw.includes("mood") || raw.includes("stark") || raw.includes("party mode") || raw.includes("movie")) {
      sendCommand("mood", "Stark Tower Mood Mode");
      speakText("Mood protocol engaged. Ambient lighting active.");
    } else if (raw.includes("status") || raw.includes("report")) {
      sendCommand("status", "Status Query");
      speakText("Diagnostics nominal. Cloud link ready.");
    } else {
      setVoiceFeedback(`Command "${transcript}" unmapped. Try "turn on PC" or "all on"`);
      speakText("I didn't recognize that command. Please try again.");
    }
  };

  // Toggle Voice Recognition
  const toggleListening = () => {
    if (typeof window === "undefined") return;
    const SpeechRecognition = (window as any).SpeechRecognition || (window as any).webkitSpeechRecognition;

    if (!SpeechRecognition) {
      alert("Web Speech Recognition API is not supported in this browser. Please use Google Chrome or Microsoft Edge.");
      return;
    }

    if (isListening) {
      recognitionRef.current?.stop();
      setIsListening(false);
      return;
    }

    try {
      const recognition = new SpeechRecognition();
      recognition.continuous = false;
      recognition.interimResults = false;
      recognition.lang = "en-US";

      recognition.onstart = () => {
        setIsListening(true);
        setVoiceFeedback("Listening... Speak now");
      };

      recognition.onresult = (event: any) => {
        const transcript = event.results[0][0].transcript;
        interpretVoice(transcript);
        setIsListening(false);
      };

      recognition.onerror = (event: any) => {
        setIsListening(false);
        setVoiceFeedback(`Voice recognition notice: ${event.error || "No speech detected"}`);
      };

      recognition.onend = () => {
        setIsListening(false);
      };

      recognitionRef.current = recognition;
      recognition.start();
    } catch (err: any) {
      setIsListening(false);
      addLog(`Mic Error: ${err.message}`, "error");
    }
  };

  // Web Serial Port Connect
  const toggleSerialConnection = async () => {
    if (typeof window === "undefined") return;

    if (!("serial" in navigator)) {
      alert("Web Serial API is not supported on this browser. Use Google Chrome or Microsoft Edge on Desktop.");
      return;
    }

    if (serialConnected) {
      try {
        if (readerRef.current) await readerRef.current.cancel();
        if (writerRef.current) await writerRef.current.close();
        if (portRef.current) await portRef.current.close();
        setSerialConnected(false);
        addLog("USB Serial Port Disconnected.", "system");
      } catch (err: any) {
        addLog(`Serial Disconnect Notice: ${err.message}`, "error");
      }
      return;
    }

    try {
      const port = await (navigator as any).serial.requestPort();
      await port.open({ baudRate });
      portRef.current = port;
      setSerialConnected(true);
      addLog(`Connected to USB Serial Port @ ${baudRate} baud.`, "system");

      const textEncoderStream = new TextEncoderStream();
      const writableStreamClosed = textEncoderStream.readable.pipeTo(port.writable);
      writerRef.current = textEncoderStream.writable.getWriter();

      const textDecoderStream = new TextDecoderStream();
      const readableStreamClosed = port.readable.pipeTo(textDecoderStream.writable);
      const reader = textDecoderStream.readable.getReader();
      readerRef.current = reader;

      readLoop(reader);
    } catch (err: any) {
      addLog(`Port connection cancelled or failed: ${err.message}`, "error");
      setSerialConnected(false);
    }
  };

  const readLoop = async (reader: any) => {
    try {
      while (true) {
        const { value, done } = await reader.read();
        if (done) break;
        if (value && value.trim()) {
          addLog(value.trim(), "rx");
        }
      }
    } catch (err: any) {
      addLog(`Read loop stopped: ${err.message}`, "system");
    }
  };

  return (
    <div className="relative min-h-screen flex flex-col bg-[#06070d] text-white overflow-x-hidden">
      
      {/* Background Graphic & Cyberpunk Grid */}
      <div 
        className="fixed inset-0 bg-cover bg-center opacity-30 pointer-events-none z-0"
        style={{ backgroundImage: "url('/orangeandpurplebg.png')" }}
      />
      <div className="fixed inset-0 bg-[linear-gradient(to_right,#00f2fe08_1px,transparent_1px),linear-gradient(to_bottom,#00f2fe08_1px,transparent_1px)] bg-[size:36px_36px] pointer-events-none z-0" />

      {/* Top Header Navbar */}
      <Navbar
        serialConnected={serialConnected}
        onToggleSerial={toggleSerialConnection}
        onOpenTerminal={() => setTerminalOpen(true)}
        voiceEnabled={voiceEnabled}
        onToggleVoice={() => setVoiceEnabled(!voiceEnabled)}
      />

      {/* Main Interactive Stage */}
      <main className="relative z-10 flex-1 max-w-7xl w-full mx-auto flex flex-col justify-between items-center px-4 py-4 sm:py-6">
        
        {/* Title Header */}
        <div className="text-center">
          <h1 className="font-orbitron font-black text-3xl sm:text-5xl tracking-wider bg-gradient-to-r from-white via-cyan-200 to-purple-400 bg-clip-text text-transparent underline underline-offset-8 decoration-cyan-400/40">
            JASICA AI CORE
          </h1>
          <p className="text-xs sm:text-sm font-medium text-white/60 mt-2 font-mono tracking-wide underline underline-offset-4 decoration-white/20">
            Next-Gen Voice Recognition & 4-Channel Hardware Controller
          </p>
        </div>

        {/* Center Holographic HUD Orb */}
        <HudVisualizer
          isListening={isListening}
          voiceFeedback={voiceFeedback}
          onMicClick={toggleListening}
          serialConnected={serialConnected}
          activeCommand={activeCommand}
        />

        {/* Hardware Relays & Pin Control Matrix */}
        <PinMatrix
          deviceStates={deviceStates}
          onSendCommand={sendCommand}
          serialConnected={serialConnected}
        />

        {/* Bottom Voice Trigger Area */}
        <div className="mt-6 mb-2 flex flex-col items-center">
          <div className="relative">
            {isListening && (
              <>
                <span className="absolute inset-0 rounded-full border-2 border-cyan-400 animate-ping opacity-60" />
                <span className="absolute -inset-2 rounded-full border border-purple-500 animate-pulse opacity-40" />
              </>
            )}
            <button
              onClick={toggleListening}
              className={`relative z-10 w-16 h-16 sm:w-20 sm:h-20 rounded-full flex items-center justify-center transition-all duration-300 shadow-glow ${
                isListening
                  ? "bg-gradient-to-r from-rose-500 to-amber-500 scale-110 shadow-[0_0_35px_rgba(244,63,94,0.7)]"
                  : "bg-gradient-to-r from-cyan-400 via-blue-500 to-purple-600 hover:scale-105 hover:shadow-glow-lg"
              }`}
            >
              <Mic className="w-8 h-8 text-black" strokeWidth={1.5} />
            </button>
          </div>
          <span className="text-xs font-mono text-white/60 mt-2 font-semibold tracking-wider underline underline-offset-4 decoration-white/30">
            {isListening ? "LISTENING... TAP TO STOP" : "TAP TO SPEAK // COMMAND JASICA"}
          </span>
        </div>

      </main>

      {/* Hardware Serial Terminal Modal */}
      <SerialTerminal
        isOpen={terminalOpen}
        onClose={() => setTerminalOpen(false)}
        logs={logs}
        onClearLogs={() => setLogs([])}
        onSendRaw={(cmd) => sendCommand(cmd, "Raw Terminal Input")}
        serialConnected={serialConnected}
        onToggleSerial={toggleSerialConnection}
        baudRate={baudRate}
        onChangeBaudRate={setBaudRate}
      />

    </div>
  );
}

"use client";

import React, { useState } from "react";
import Navbar from "@/components/Navbar";
import { Cpu, Copy, Check, Download, Info, Zap, Layers } from "lucide-react";

export default function FirmwarePage() {
  const [copied, setCopied] = useState(false);
  const [selectedSketch, setSelectedSketch] = useState<"4relay" | "12pin">("4relay");

  const sketch4Relay = `/*
 * ==========================================================
 * Project: J.A.R.V.I.S / JASICA Hardware Controller
 * Target Board: Arduino UNO / Nano
 * Description: 4-Channel Active-LOW Relay Controller
 * Supports: 
 *   1. Direct USB Serial (Serial Monitor / Next.js Web App)
 *   2. External Bluetooth Module (HC-05 / HC-06 via SoftwareSerial)
 * ==========================================================
 */

#include <SoftwareSerial.h>

const int BT_RX = 2;
const int BT_TX = 3;
SoftwareSerial SerialBT(BT_RX, BT_TX);

const int PC_PIN     = 4;   // Commands: a / A
const int RGB_PIN    = 5;   // Commands: b / B
const int ROOM_LIGHT = 6;   // Commands: c / C
const int PLUG_PIN   = 7;   // Commands: d / D

const int RELAY_ON  = LOW;
const int RELAY_OFF = HIGH;

String incomingUSB = "";
String incomingBT  = "";

void setup() {
  Serial.begin(9600);
  SerialBT.begin(9600);

  digitalWrite(PC_PIN, RELAY_OFF);
  digitalWrite(RGB_PIN, RELAY_OFF);
  digitalWrite(ROOM_LIGHT, RELAY_OFF);
  digitalWrite(PLUG_PIN, RELAY_OFF);

  pinMode(PC_PIN, OUTPUT);
  pinMode(RGB_PIN, OUTPUT);
  pinMode(ROOM_LIGHT, OUTPUT);
  pinMode(PLUG_PIN, OUTPUT);

  Serial.println(F("SYSTEM.OS: J.A.R.V.I.S Hardware Interface Booted (Arduino UNO)"));
  Serial.println(F("AWAITING_UPLINK (Listening on USB Serial & Bluetooth)..."));
}

void loop() {
  while (Serial.available() > 0) {
    char c = (char)Serial.read();
    if (c == '\\n' || c == '\\r') {
      if (incomingUSB.length() > 0) {
        incomingUSB.trim();
        executeCommand(incomingUSB, "USB");
        incomingUSB = "";
      }
    } else {
      incomingUSB += c;
    }
  }

  while (SerialBT.available() > 0) {
    char c = (char)SerialBT.read();
    if (c == '\\n' || c == '\\r') {
      if (incomingBT.length() > 0) {
        incomingBT.trim();
        executeCommand(incomingBT, "BLUETOOTH");
        incomingBT = "";
      }
    } else {
      incomingBT += c;
    }
  }
}

void executeCommand(String cmd, String source) {
  Serial.print(F(">> ["));
  Serial.print(source);
  Serial.print(F("] INCOMING_CMD: "));
  Serial.println(cmd);

  SerialBT.print(F("ACK: "));
  SerialBT.println(cmd);

  if (cmd == "a") {
    digitalWrite(PC_PIN, RELAY_ON);
    Serial.println(F(">> PC: ON"));
  } 
  else if (cmd == "A") {
    digitalWrite(PC_PIN, RELAY_OFF);
    Serial.println(F(">> PC: OFF"));
  } 
  else if (cmd == "b") {
    digitalWrite(RGB_PIN, RELAY_ON);
    Serial.println(F(">> RGB: ON"));
  } 
  else if (cmd == "B") {
    digitalWrite(RGB_PIN, RELAY_OFF);
    Serial.println(F(">> RGB: OFF"));
  } 
  else if (cmd == "c") {
    digitalWrite(ROOM_LIGHT, RELAY_ON);
    Serial.println(F(">> ROOM_LIGHT: ON"));
  } 
  else if (cmd == "C") {
    digitalWrite(ROOM_LIGHT, RELAY_OFF);
    Serial.println(F(">> ROOM_LIGHT: OFF"));
  } 
  else if (cmd == "d") {
    digitalWrite(PLUG_PIN, RELAY_ON);
    Serial.println(F(">> PLUG: ON"));
  } 
  else if (cmd == "D") {
    digitalWrite(PLUG_PIN, RELAY_OFF);
    Serial.println(F(">> PLUG: OFF"));
  } 
  else if (cmd == "on" || cmd == "ON" || cmd == "all on") {
    digitalWrite(PC_PIN, RELAY_ON);
    digitalWrite(RGB_PIN, RELAY_ON);
    digitalWrite(ROOM_LIGHT, RELAY_ON);
    digitalWrite(PLUG_PIN, RELAY_ON);
    Serial.println(F(">> ALL DEVICES: ON"));
  } 
  else if (cmd == "off" || cmd == "OFF" || cmd == "all off") {
    digitalWrite(PC_PIN, RELAY_OFF);
    digitalWrite(RGB_PIN, RELAY_OFF);
    digitalWrite(ROOM_LIGHT, RELAY_OFF);
    digitalWrite(PLUG_PIN, RELAY_OFF);
    Serial.println(F(">> ALL DEVICES: OFF"));
  } 
  else if (cmd == "mood" || cmd == "MOOD") {
    digitalWrite(ROOM_LIGHT, RELAY_OFF);
    digitalWrite(RGB_PIN, RELAY_ON);
    Serial.println(F(">> MOOD MODE ACTIVATED"));
  }
}
`;

  const codeToShow = selectedSketch === "4relay" ? sketch4Relay : `// 12-Pin Controller (Pins 2 to 13)
void setup() {
  Serial.begin(9600);
  for (int p = 2; p <= 13; p++) {
    pinMode(p, OUTPUT);
    digitalWrite(p, HIGH);
  }
}
void loop() {
  if (Serial.available()) {
    char c = Serial.read();
  }
}`;

  const copyCode = () => {
    navigator.clipboard.writeText(codeToShow);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  return (
    <div className="min-h-screen bg-[#06070d] text-white flex flex-col selection:bg-cyan-500 selection:text-black">
      <Navbar />

      <main className="relative z-10 flex-1 max-w-7xl w-full mx-auto px-4 py-8">
        
        {/* Banner */}
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 pb-6 border-b border-white/10">
          <div>
            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-cyan-950/60 border border-cyan-500/30 text-cyan-400 text-xs font-mono mb-2">
              <Cpu className="w-3.5 h-3.5" strokeWidth={1.5} />
              <span className="underline underline-offset-4 decoration-cyan-400/50">ARDUINO & ESP32 FIRMWARE HUB</span>
            </div>
            <h1 className="text-3xl sm:text-4xl font-black font-orbitron text-white underline underline-offset-8 decoration-cyan-400/40">
              Hardware & Wiring Schematics
            </h1>
            <p className="text-sm text-white/60 font-mono mt-2 underline underline-offset-4 decoration-white/20">
              Active-LOW relay driver, Bluetooth HC-05 serial bridge, and pinout configurations.
            </p>
          </div>

          <div className="flex items-center gap-2">
            <button
              onClick={() => setSelectedSketch("4relay")}
              className={`px-3.5 py-1.5 rounded-xl text-xs font-mono font-bold transition underline underline-offset-4 ${
                selectedSketch === "4relay" ? "bg-cyan-500 text-black shadow-glow decoration-black/40" : "bg-white/5 border border-white/10 text-white/70 decoration-white/30"
              }`}
            >
              4-Channel Relay Sketch
            </button>
            <button
              onClick={copyCode}
              className="px-4 py-1.5 rounded-xl bg-gradient-to-r from-cyan-500 to-blue-600 text-black text-xs font-bold flex items-center gap-2 shadow-glow hover:opacity-95 underline underline-offset-4 decoration-black/40"
            >
              {copied ? <Check className="w-4 h-4" strokeWidth={1.5} /> : <Copy className="w-4 h-4" strokeWidth={1.5} />}
              <span>{copied ? "Copied" : "Copy Code"}</span>
            </button>
          </div>
        </div>

        {/* Pinout Grid */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mt-6">
          <div className="p-4 rounded-xl bg-[#0e101f] border border-cyan-500/20">
            <span className="text-xs font-mono text-cyan-400 font-bold block underline underline-offset-2 decoration-cyan-400/30">PIN D4</span>
            <h4 className="text-sm font-bold text-white mt-1 underline underline-offset-4 decoration-white/30">PC Power Relay</h4>
            <p className="text-xs text-white/50 font-mono mt-1">Commands: &apos;a&apos; (ON), &apos;A&apos; (OFF)</p>
          </div>
          <div className="p-4 rounded-xl bg-[#0e101f] border border-purple-500/20">
            <span className="text-xs font-mono text-purple-400 font-bold block underline underline-offset-2 decoration-purple-400/30">PIN D5</span>
            <h4 className="text-sm font-bold text-white mt-1 underline underline-offset-4 decoration-white/30">RGB Ambience</h4>
            <p className="text-xs text-white/50 font-mono mt-1">Commands: &apos;b&apos; (ON), &apos;B&apos; (OFF)</p>
          </div>
          <div className="p-4 rounded-xl bg-[#0e101f] border border-amber-500/20">
            <span className="text-xs font-mono text-amber-400 font-bold block underline underline-offset-2 decoration-amber-400/30">PIN D6</span>
            <h4 className="text-sm font-bold text-white mt-1 underline underline-offset-4 decoration-white/30">Room Light</h4>
            <p className="text-xs text-white/50 font-mono mt-1">Commands: &apos;c&apos; (ON), &apos;C&apos; (OFF)</p>
          </div>
          <div className="p-4 rounded-xl bg-[#0e101f] border border-emerald-500/20">
            <span className="text-xs font-mono text-emerald-400 font-bold block underline underline-offset-2 decoration-emerald-400/30">PIN D7</span>
            <h4 className="text-sm font-bold text-white mt-1 underline underline-offset-4 decoration-white/30">Power Socket Plug</h4>
            <p className="text-xs text-white/50 font-mono mt-1">Commands: &apos;d&apos; (ON), &apos;D&apos; (OFF)</p>
          </div>
        </div>

        {/* Code Viewer */}
        <div className="mt-6 rounded-2xl bg-[#080911] border border-white/10 overflow-hidden">
          <div className="px-4 py-2.5 bg-white/5 border-b border-white/10 flex items-center justify-between">
            <span className="text-xs font-mono text-white/70 underline underline-offset-2 decoration-white/20">jasica_controller.ino</span>
            <span className="text-[10px] font-mono text-cyan-400 underline underline-offset-2 decoration-cyan-400/40">9600 Baud • Arduino C++</span>
          </div>
          <pre className="p-5 font-mono text-xs text-cyan-200 overflow-x-auto leading-relaxed max-h-[500px]">
            {codeToShow}
          </pre>
        </div>

      </main>
    </div>
  );
}

"use client";

import React, { useState } from "react";
import Link from "next/link";
import { usePathname } from "next/navigation";
import { 
  Radio, 
  Cpu, 
  Server, 
  Smartphone, 
  Terminal, 
  Volume2, 
  VolumeX, 
  Usb,
  Menu,
  X,
  Sparkles
} from "lucide-react";

interface NavbarProps {
  serialConnected?: boolean;
  onToggleSerial?: () => void;
  onOpenTerminal?: () => void;
  voiceEnabled?: boolean;
  onToggleVoice?: () => void;
}

export default function Navbar({
  serialConnected = false,
  onToggleSerial,
  onOpenTerminal,
  voiceEnabled = true,
  onToggleVoice,
}: NavbarProps) {
  const pathname = usePathname();
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  const navLinks = [
    { name: "Live Console", href: "/", icon: Radio },
    { name: "Custom Hosting", href: "/hosting", icon: Server },
    { name: "Firmware & Pinout", href: "/firmware", icon: Cpu },
    { name: "Android App", href: "/android", icon: Smartphone },
    { name: "3D Showcase", href: "/showcase", icon: Sparkles },
  ];

  return (
    <header className="sticky top-0 z-50 w-full backdrop-blur-xl bg-[#06070d]/85 border-b border-white/10 px-4 lg:px-8 py-3 transition-all duration-300">
      <div className="max-w-7xl mx-auto flex items-center justify-between">
        
        {/* Brand Logo */}
        <Link href="/" className="flex items-center gap-3 group">
          <div className="relative flex items-center justify-center">
            <div className="absolute -inset-1 bg-gradient-to-r from-cyan-500 to-purple-600 rounded-full blur opacity-60 group-hover:opacity-100 transition duration-500" />
            <img 
              src="/jasica.png" 
              alt="JASICA Logo" 
              className="relative w-9 h-9 rounded-full object-cover border border-cyan-400/80 shadow-glow"
            />
          </div>
          <div className="flex flex-col">
            <div className="flex items-center gap-2">
              <span className="font-orbitron font-black text-lg tracking-wider bg-gradient-to-r from-white via-cyan-200 to-cyan-400 bg-clip-text text-transparent underline underline-offset-4 decoration-cyan-400/60 decoration-1">
                JASICA AI
              </span>
              <span className="text-[10px] font-bold px-2 py-0.5 rounded bg-cyan-950/80 border border-cyan-500/40 text-cyan-400 font-mono uppercase tracking-widest shadow-sm">
                v2.0
              </span>
            </div>
            <span className="text-[10px] font-medium text-white/50 tracking-wider font-mono">
              NEXT-GEN HARDWARE HOST
            </span>
          </div>
        </Link>

        {/* Desktop Navigation */}
        <nav className="hidden md:flex items-center gap-2 bg-white/[0.04] p-1.5 rounded-full border border-white/10">
          {navLinks.map((link) => {
            const Icon = link.icon;
            const isActive = pathname === link.href;
            return (
              <Link
                key={link.name}
                href={link.href}
                className={`flex items-center gap-2 px-3.5 py-1.5 rounded-full text-xs font-semibold tracking-wide transition-all duration-200 ${
                  isActive
                    ? "bg-gradient-to-r from-cyan-500/20 to-purple-600/20 text-cyan-300 border border-cyan-500/50 shadow-glow underline underline-offset-4 decoration-cyan-300"
                    : "text-white/70 hover:text-white hover:bg-white/5 hover:underline underline-offset-4 decoration-white/40"
                }`}
              >
                <Icon className="w-3.5 h-3.5" strokeWidth={1.5} />
                <span className="underline underline-offset-4 decoration-current/50">{link.name}</span>
              </Link>
            );
          })}
        </nav>

        {/* Action Buttons */}
        <div className="hidden lg:flex items-center gap-3">
          {onToggleSerial && (
            <button
              onClick={onToggleSerial}
              className={`flex items-center gap-2 px-3.5 py-1.5 rounded-full text-xs font-semibold tracking-wide border transition-all duration-300 ${
                serialConnected
                  ? "bg-emerald-500/15 border-emerald-400 text-emerald-300 shadow-glow-green"
                  : "bg-white/[0.06] border-white/20 text-white/90 hover:border-cyan-400 hover:text-cyan-300 hover:shadow-glow"
              }`}
            >
              <div className={`w-2 h-2 rounded-full ${serialConnected ? "bg-emerald-400 animate-pulse" : "bg-rose-500"}`} />
              <Usb className="w-3.5 h-3.5" strokeWidth={1.5} />
              <span className="underline underline-offset-4 decoration-current/40">{serialConnected ? "PORT CONNECTED" : "CONNECT USB"}</span>
            </button>
          )}

          {onOpenTerminal && (
            <button
              onClick={onOpenTerminal}
              className="p-2 rounded-xl bg-white/[0.05] border border-white/15 text-white/80 hover:text-cyan-400 hover:border-cyan-400 hover:bg-cyan-950/30 transition-all shadow-sm"
              title="Open Hardware Serial Terminal"
            >
              <Terminal className="w-4 h-4" strokeWidth={1.5} />
            </button>
          )}

          {onToggleVoice && (
            <button
              onClick={onToggleVoice}
              className={`p-2 rounded-xl border transition-all ${
                voiceEnabled
                  ? "bg-purple-950/40 border-purple-500/50 text-purple-300 shadow-glow-purple"
                  : "bg-white/[0.05] border-white/15 text-white/40 hover:text-white"
              }`}
              title={voiceEnabled ? "Voice Synthesizer: ON" : "Voice Synthesizer: MUTED"}
            >
              {voiceEnabled ? (
                <Volume2 className="w-4 h-4" strokeWidth={1.5} />
              ) : (
                <VolumeX className="w-4 h-4" strokeWidth={1.5} />
              )}
            </button>
          )}
        </div>

        {/* Mobile Menu Toggle */}
        <div className="flex items-center gap-2 md:hidden">
          {onOpenTerminal && (
            <button
              onClick={onOpenTerminal}
              className="p-2 rounded-lg bg-white/5 border border-white/10 text-cyan-400"
            >
              <Terminal className="w-4 h-4" strokeWidth={1.5} />
            </button>
          )}
          <button
            onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
            className="p-2 rounded-lg bg-white/5 border border-white/10 text-white"
          >
            {mobileMenuOpen ? (
              <X className="w-5 h-5" strokeWidth={1.5} />
            ) : (
              <Menu className="w-5 h-5" strokeWidth={1.5} />
            )}
          </button>
        </div>

      </div>

      {/* Mobile Menu Dropdown */}
      {mobileMenuOpen && (
        <div className="md:hidden mt-3 pt-3 border-t border-white/10 flex flex-col gap-2 pb-2">
          {navLinks.map((link) => {
            const Icon = link.icon;
            const isActive = pathname === link.href;
            return (
              <Link
                key={link.name}
                href={link.href}
                onClick={() => setMobileMenuOpen(false)}
                className={`flex items-center gap-3 px-4 py-2.5 rounded-xl text-sm font-medium underline underline-offset-4 ${
                  isActive
                    ? "bg-cyan-950/60 border border-cyan-500/40 text-cyan-300 decoration-cyan-300"
                    : "text-white/70 hover:bg-white/5 decoration-white/30"
                }`}
              >
                <Icon className="w-4 h-4" strokeWidth={1.5} />
                <span>{link.name}</span>
              </Link>
            );
          })}
        </div>
      )}
    </header>
  );
}

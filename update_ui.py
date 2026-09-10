import re

with open('app/page.tsx', 'r', encoding='utf-8') as f:
    content = f.read()

# Find where the return statement starts
match = re.search(r'  return \(\n    <div className="min-h-screen', content)
if not match:
    print("Could not find return statement")
    exit(1)

return_idx = match.start()

# Keep everything before return statement
top_part = content[:return_idx]

# Ensure we have all necessary icons imported
imports = """import { 
  Download, LayoutTemplate, Layers, Info, DownloadCloud, ChevronRight,
  BrainCircuit, Languages, Mic, WifiOff, Cpu, Monitor, Lightbulb, Plug,
  Wind, Fan, Code, Droplet, SmartphoneCharging, SquarePlay, Music, Camera, 
  Paintbrush, Check, Globe, Search, Wifi, Bluetooth, Bell, Volume2, 
  ChevronDown, Sun, SunDim
} from "lucide-react";"""
top_part = re.sub(r'import \{[\s\S]*?\} from "lucide-react";', imports, top_part)

bottom_part = """  return (
    <div className="min-h-screen bg-[#f5f5f7] text-[#1d1d1f] font-sans antialiased overflow-x-hidden selection:bg-cyan-500 selection:text-black">
      
      <nav className="fixed top-0 left-0 w-full h-14 bg-white/70 backdrop-blur-lg border-b border-zinc-200 z-50 flex items-center justify-between px-4 md:px-8">
        <div className="font-semibold text-lg tracking-tight flex items-center gap-2 text-black">
            <svg viewBox="0 0 24 24" width="24" height="24" stroke="currentColor" strokeWidth="1.25" fill="none" strokeLinecap="round" strokeLinejoin="round" className="w-6 h-6">
                <circle cx="12" cy="12" r="10" />
                <clipPath id="nav-sphere-clip">
                    <circle cx="12" cy="12" r="9.5" />
                </clipPath>
                <g clipPath="url(#nav-sphere-clip)">
                    <path d="M -2 6 C 6 14, 14 -2, 26 6" />
                    <path d="M -2 12 C 6 20, 14 4, 26 12" />
                    <path d="M -2 18 C 6 26, 14 10, 26 18" />
                </g>
            </svg>
            Jasica AI
        </div>
        <div className="flex items-center gap-5 sm:gap-6 text-sm font-medium text-zinc-500">
            <div className="hidden sm:flex items-center gap-1 text-zinc-400 mr-2">
                <div className="ios-spinner scale-[0.6]">
                    <div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div>
                </div>
                <span className="text-xs">Syncing</span>
            </div>
            
            <a href="#" className="text-black cursor-pointer group">
                <span className="underline underline-offset-4 decoration-1 decoration-zinc-400">Overview</span>
            </a>
            <a href="#" className="cursor-pointer group">
                <span className="underline underline-offset-4 decoration-1 decoration-zinc-300">Tech Specs</span>
            </a>
        </div>
    </nav>

    <div className="absolute top-[22vh] sm:top-[20vh] md:top-[18vh] left-0 w-full px-4 text-center z-0 pointer-events-none flex flex-col items-center">
        <h1 className="mobile-title-scale text-[7rem] sm:text-[9rem] md:text-[12rem] lg:text-[16rem] font-bold tracking-tighter leading-[0.8] animate-fade-up pb-2 whitespace-nowrap">
            <span className="text-black">Jasica</span>
            <span className="bg-gradient-to-b from-zinc-600 to-black bg-clip-text text-transparent">AI.</span>
        </h1>
    </div>

    <div className="fixed bottom-10 md:bottom-14 left-0 w-full flex justify-center z-50 pointer-events-none animate-fade-up delay-200">
        <button className="pointer-events-auto bg-black text-white text-lg md:text-xl font-medium px-12 md:px-16 py-4 md:py-5 rounded-full cursor-pointer transition-colors duration-200 flex items-center gap-3">
            <Download strokeWidth={1} className="w-6 h-6" />
            Download Now
        </button>
    </div>

    <div id="canvas-container" ref={containerRef} />

    <section className="relative z-20 bg-[#f5f5f7] min-h-screen py-32 px-4 md:px-12 mt-[110vh]">
        <div className="max-w-4xl mx-auto">
            <div className="text-center mb-16">
                <h2 className="text-4xl md:text-5xl font-semibold tracking-tight text-black mb-4">System Specifications</h2>
                <p className="text-xl text-zinc-500 font-normal max-w-2xl mx-auto">Everything you need to know about the latest release, structured for clarity and quick access.</p>
            </div>

            <div className="border border-zinc-200 rounded-[2rem] overflow-hidden bg-white max-w-3xl mx-auto">
                <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center py-6 px-8 border-b border-zinc-200">
                    <div className="flex items-center gap-3 w-1/3 mb-1 sm:mb-0">
                        <div className="w-8 h-8 rounded-lg bg-[#007AFF] flex items-center justify-center text-white shrink-0">
                            <LayoutTemplate strokeWidth={1} className="w-5 h-5" />
                        </div>
                        <span className="text-zinc-500 font-medium text-lg">App Name</span>
                    </div>
                    <span className="text-zinc-900 font-semibold text-lg sm:text-right w-full sm:w-2/3 mt-2 sm:mt-0 pl-11 sm:pl-0">Jasica AI</span>
                </div>
                
                <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center py-6 px-8 border-b border-zinc-200">
                    <div className="flex items-center gap-3 w-1/3 mb-1 sm:mb-0">
                        <div className="w-8 h-8 rounded-lg bg-[#34C759] flex items-center justify-center text-white shrink-0">
                            <Layers strokeWidth={1} className="w-5 h-5" />
                        </div>
                        <span className="text-zinc-500 font-medium text-lg">Update Name</span>
                    </div>
                    <span className="text-zinc-900 font-semibold text-lg sm:text-right w-full sm:w-2/3 mt-2 sm:mt-0 pl-11 sm:pl-0">Neural Core Spring Release</span>
                </div>
                
                <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center py-6 px-8 border-b border-zinc-200">
                    <div className="flex items-center gap-3 w-1/3 mb-1 sm:mb-0">
                        <div className="w-8 h-8 rounded-lg bg-[#8E8E93] flex items-center justify-center text-white shrink-0">
                            <Info strokeWidth={1} className="w-5 h-5" />
                        </div>
                        <span className="text-zinc-500 font-medium text-lg">Version</span>
                    </div>
                    <span className="text-zinc-900 font-semibold text-lg sm:text-right w-full sm:w-2/3 mt-2 sm:mt-0 pl-11 sm:pl-0">v2.4.0 (Build 2409)</span>
                </div>
                
                <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center py-6 px-8">
                    <div className="flex items-center gap-3 w-1/3 mb-1 sm:mb-0">
                        <div className="w-8 h-8 rounded-lg bg-[#5856D6] flex items-center justify-center text-white shrink-0">
                            <DownloadCloud strokeWidth={1} className="w-5 h-5" />
                        </div>
                        <span className="text-zinc-500 font-medium text-lg">Direct Link</span>
                    </div>
                    <div className="sm:text-right w-full sm:w-2/3 flex justify-start sm:justify-end mt-2 sm:mt-0 pl-11 sm:pl-0">
                        <a href="#download" className="text-blue-600 font-semibold text-lg flex items-center gap-2 cursor-pointer group">
                            <span className="underline underline-offset-4 decoration-1">Download Package (.zip)</span>
                            <ChevronRight strokeWidth={1.5} className="w-4 h-4 text-blue-600" />
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <section className="relative z-20 bg-[#f5f5f7] py-16 md:py-24 px-4 md:px-12">
        <div className="max-w-6xl mx-auto">
            
            <div className="text-center mb-16 md:mb-20">
                <h2 className="text-4xl md:text-5xl font-semibold tracking-tight text-black mb-4">Intelligence, built in.</h2>
                <p className="text-xl text-zinc-500 font-normal max-w-2xl mx-auto">A seamless blend of emotional AI, hardware control, and everyday utility.</p>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-6 gap-6">
                
                <div className="col-span-1 md:col-span-6 bg-white border border-zinc-200 rounded-[2rem] p-8 md:p-12 flex flex-col md:flex-row gap-10 items-center overflow-hidden">
                    <div className="flex-1">
                        <div className="w-12 h-12 rounded-[14px] bg-[#5856D6] flex items-center justify-center text-white mb-6">
                            <svg viewBox="0 0 24 24" width="24" height="24" stroke="currentColor" strokeWidth="1.25" fill="none" strokeLinecap="round" strokeLinejoin="round" className="w-6 h-6">
                                <circle cx="12" cy="12" r="10" />
                                <clipPath id="card-sphere-clip">
                                    <circle cx="12" cy="12" r="9.5" />
                                </clipPath>
                                <g clipPath="url(#card-sphere-clip)">
                                    <path d="M -2 6 C 6 14, 14 -2, 26 6" />
                                    <path d="M -2 12 C 6 20, 14 4, 26 12" />
                                    <path d="M -2 18 C 6 26, 14 10, 26 18" />
                                </g>
                            </svg>
                        </div>
                        <h3 className="text-3xl md:text-4xl font-semibold text-black mb-4 tracking-tight">Meet Jasica.</h3>
                        <p className="text-lg text-zinc-500 mb-8 leading-relaxed font-normal max-w-3xl">
                            Powered by Google's Gemini 2.5 Flash API, Jasica isn't just an assistant—she's an emotionally aware companion with a custom persona. Fluent in English and Bengali, she remembers your context and is always ready with hands-free Wake Word activation.
                        </p>
                        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-y-4 gap-x-6">
                            <div className="flex items-center gap-3">
                                <BrainCircuit strokeWidth={1.5} className="w-5 h-5 text-zinc-400" />
                                <span className="text-zinc-700 font-medium text-sm">Conversation Memory</span>
                            </div>
                            <div className="flex items-center gap-3">
                                <Languages strokeWidth={1.5} className="w-5 h-5 text-zinc-400" />
                                <span className="text-zinc-700 font-medium text-sm">Bilingual TTS</span>
                            </div>
                            <div className="flex items-center gap-3">
                                <Mic strokeWidth={1.5} className="w-5 h-5 text-zinc-400" />
                                <span className="text-zinc-700 font-medium text-sm">Voice Calibration</span>
                            </div>
                            <div className="flex items-center gap-3">
                                <WifiOff strokeWidth={1.5} className="w-5 h-5 text-zinc-400" />
                                <span className="text-zinc-700 font-medium text-sm">Offline Command Parser</span>
                            </div>
                        </div>
                    </div>
                </div>

                <div className="col-span-1 md:col-span-4 bg-white border border-zinc-200 rounded-[2rem] p-8 md:p-10 flex flex-col justify-between">
                    <div>
                        <div className="w-12 h-12 rounded-[14px] bg-[#FF9500] flex items-center justify-center text-white mb-6">
                            <Cpu strokeWidth={1.5} className="w-6 h-6" />
                        </div>
                        <h3 className="text-2xl font-semibold text-black mb-3 tracking-tight">Smart Hardware Control</h3>
                        <p className="text-zinc-500 mb-8 leading-relaxed font-normal max-w-xl">
                            Command your Arduino or ESP32 systems seamlessly via Dual Bluetooth (Classic & BLE). Utilize local offline intents, mood presets, or the manual control panel for complete mastery over your environment.
                        </p>
                    </div>
                    <div className="grid grid-cols-2 sm:grid-cols-3 gap-3">
                        <div className="bg-[#f5f5f7] border border-zinc-200 rounded-full px-4 py-2.5 flex items-center justify-center gap-2">
                            <Monitor strokeWidth={1.5} className="w-4 h-4 text-zinc-500 shrink-0" />
                            <span className="text-sm font-medium text-zinc-700 truncate">PC Hub</span>
                        </div>
                        <div className="bg-[#f5f5f7] border border-zinc-200 rounded-full px-4 py-2.5 flex items-center justify-center gap-2">
                            <Lightbulb strokeWidth={1.5} className="w-4 h-4 text-zinc-500 shrink-0" />
                            <span className="text-sm font-medium text-zinc-700 truncate">RGB Light</span>
                        </div>
                        <div className="bg-[#f5f5f7] border border-zinc-200 rounded-full px-4 py-2.5 flex items-center justify-center gap-2">
                            <Plug strokeWidth={1.5} className="w-4 h-4 text-zinc-500 shrink-0" />
                            <span className="text-sm font-medium text-zinc-700 truncate">Smart Plug</span>
                        </div>
                        <div className="bg-[#f5f5f7] border border-zinc-200 rounded-full px-4 py-2.5 flex items-center justify-center gap-2">
                            <Wind strokeWidth={1.5} className="w-4 h-4 text-zinc-500 shrink-0" />
                            <span className="text-sm font-medium text-zinc-700 truncate">AC Unit</span>
                        </div>
                        <div className="bg-[#f5f5f7] border border-zinc-200 rounded-full px-4 py-2.5 flex items-center justify-center gap-2">
                            <Fan strokeWidth={1.5} className="w-4 h-4 text-zinc-500 shrink-0" />
                            <span className="text-sm font-medium text-zinc-700 truncate">Room Fan</span>
                        </div>
                        <div className="bg-[#f5f5f7] border border-zinc-200 rounded-full px-4 py-2.5 flex items-center justify-center gap-2">
                            <Code strokeWidth={1.5} className="w-4 h-4 text-zinc-500 shrink-0" />
                            <span className="text-sm font-medium text-zinc-700 truncate">Arduino Gen</span>
                        </div>
                    </div>
                </div>

                <div className="col-span-1 md:col-span-2 bg-white border border-zinc-200 rounded-[2rem] p-8 md:p-10 flex flex-col justify-between">
                    <div>
                        <div className="w-12 h-12 rounded-[14px] bg-[#FF3B30] flex items-center justify-center text-white mb-6">
                            <Droplet strokeWidth={1.5} className="w-6 h-6" />
                        </div>
                        <h3 className="text-2xl font-semibold text-black mb-3 tracking-tight">Stay Hydrated</h3>
                        <p className="text-zinc-500 leading-relaxed font-normal mb-8">
                            A dedicated background service ensures you drink water every 30 minutes.
                        </p>
                    </div>
                    <div className="bg-zinc-50 rounded-2xl p-5 border border-zinc-200 flex flex-col items-center justify-center text-center h-full min-h-[120px]">
                        <SmartphoneCharging strokeWidth={1.5} className="w-7 h-7 text-[#FF3B30] mb-2" />
                        <span className="text-sm font-medium text-zinc-900 block mb-1">Lock Screen Override</span>
                        <span className="text-xs text-zinc-500 font-normal">Wakes device & vibrates</span>
                    </div>
                </div>
                
                <div className="col-span-1 md:col-span-3 bg-white border border-zinc-200 rounded-[2rem] p-8 md:p-10 flex flex-col justify-between">
                    <div>
                        <div className="w-12 h-12 rounded-[14px] bg-[#007AFF] flex items-center justify-center text-white mb-6">
                            <SquarePlay strokeWidth={1.5} className="w-6 h-6" />
                        </div>
                        <h3 className="text-2xl font-semibold text-black mb-3 tracking-tight">System Integration</h3>
                        <p className="text-zinc-500 leading-relaxed font-normal mb-8">
                            Jasica bridges the gap between AI and your OS. Launch social media, open the camera to snap a photo, or start a YouTube playlist using nothing but your voice.
                        </p>
                    </div>
                    <div className="flex items-center gap-3 flex-wrap">
                        <div className="w-12 h-12 rounded-full bg-zinc-50 flex items-center justify-center border border-zinc-200">
                            <Instagram strokeWidth={1.5} className="w-5 h-5 text-zinc-700" />
                        </div>
                        <div className="w-12 h-12 rounded-full bg-zinc-50 flex items-center justify-center border border-zinc-200">
                            <Facebook strokeWidth={1.5} className="w-5 h-5 text-zinc-700" />
                        </div>
                        <div className="w-12 h-12 rounded-full bg-zinc-50 flex items-center justify-center border border-zinc-200">
                            <Linkedin strokeWidth={1.5} className="w-5 h-5 text-zinc-700" />
                        </div>
                        <div className="w-12 h-12 rounded-full bg-zinc-50 flex items-center justify-center border border-zinc-200">
                            <Music strokeWidth={1.5} className="w-5 h-5 text-zinc-700" />
                        </div>
                        <div className="w-12 h-12 rounded-full bg-zinc-50 flex items-center justify-center border border-zinc-200">
                            <Camera strokeWidth={1.5} className="w-5 h-5 text-zinc-700" />
                        </div>
                    </div>
                </div>

                <div className="col-span-1 md:col-span-3 bg-white border border-zinc-200 rounded-[2rem] p-8 md:p-10 flex flex-col justify-between">
                    <div>
                        <div className="w-12 h-12 rounded-[14px] bg-[#8E8E93] flex items-center justify-center text-white mb-6">
                            <Paintbrush strokeWidth={1.5} className="w-6 h-6" />
                        </div>
                        <h3 className="text-2xl font-semibold text-black mb-3 tracking-tight">Fluid Experience</h3>
                        <p className="text-zinc-500 leading-relaxed font-normal mb-8">
                            Enjoy dynamic app icons that adapt to preference, beautiful Lottie animations mirroring the AI's state, and cloud-synced configurations.
                        </p>
                    </div>
                    <div className="space-y-1">
                        <div className="flex items-center justify-between py-2.5 border-b border-zinc-100">
                             <span className="text-sm font-medium text-zinc-700">Animated Voice UI</span>
                             <Check strokeWidth={1.5} className="w-4 h-4 text-[#34C759]" />
                        </div>
                        <div className="flex items-center justify-between py-2.5 border-b border-zinc-100">
                             <span className="text-sm font-medium text-zinc-700">Dynamic App Icons</span>
                             <Check strokeWidth={1.5} className="w-4 h-4 text-[#34C759]" />
                        </div>
                        <div className="flex items-center justify-between py-2.5">
                             <span className="text-sm font-medium text-zinc-700">Cloud Configuration Sync</span>
                             <Check strokeWidth={1.5} className="w-4 h-4 text-[#34C759]" />
                        </div>
                    </div>
                </div>

            </div>
        </div>
    </section>

    <section className="max-w-[1200px] mx-auto px-4 py-24 z-10 relative">
        <div className="text-center mb-16 max-w-2xl mx-auto">
            <h2 className="text-3xl md:text-5xl font-bold tracking-tighter text-zinc-900 mb-4">Deeply integrated.</h2>
            <p className="text-lg md:text-xl text-zinc-500 font-medium">Manage Gemini APIs, hardware triggers, and system overrides from a remarkably familiar interface.</p>
        </div>

        <div className="border border-zinc-300 rounded-[12px] bg-white overflow-hidden flex w-full max-w-4xl mx-auto h-[600px] text-sm">
            
            <div className="w-[240px] bg-[#f5f5f7] border-r border-zinc-200 flex flex-col shrink-0 hidden md:flex">
                <div className="h-[52px] flex items-center px-4 gap-2 shrink-0">
                    <div className="w-3 h-3 rounded-full bg-[#ff5f56] border border-[#e0443e]"></div>
                    <div className="w-3 h-3 rounded-full bg-[#ffbd2e] border border-[#dea123]"></div>
                    <div className="w-3 h-3 rounded-full bg-[#27c93f] border border-[#1aab29]"></div>
                </div>

                <div className="flex-1 overflow-y-auto pb-4">
                    <div className="px-3 mb-3">
                        <div className="w-full bg-zinc-200/60 border border-zinc-300/60 rounded-md flex items-center px-2 py-1 gap-1.5">
                            <Search className="w-3 h-3 text-zinc-500" />
                            <span className="text-[12px] text-zinc-500 font-medium">Search</span>
                        </div>
                    </div>

                    <div className="px-2 mb-4">
                        <div className="flex items-center gap-2.5 p-1 rounded-md">
                            <div className="w-9 h-9 rounded-full bg-[#d8a878] flex items-center justify-center text-white text-[11px] font-semibold border border-zinc-200/50 shrink-0">JK</div>
                            <div className="flex flex-col">
                                <span className="text-[13px] font-semibold text-zinc-900 leading-tight">Joy Kumbhakar</span>
                                <span className="text-[11px] text-zinc-500 leading-tight mt-0.5">Administrator</span>
                            </div>
                        </div>
                    </div>

                    <div className="px-2 mb-3">
                        <div className="flex items-center gap-2.5 px-2 py-1.5 rounded-md bg-[#007AFF] text-white">
                            <div className="w-5 h-5 rounded-[5px] bg-white flex items-center justify-center shrink-0">
                                <svg viewBox="0 0 24 24" width="14" height="14" stroke="currentColor" strokeWidth="1.5" fill="none" className="text-[#007AFF]">
                                    <circle cx="12" cy="12" r="10" />
                                    <clipPath id="mac-sphere-clip"><circle cx="12" cy="12" r="9.5" /></clipPath>
                                    <g clipPath="url(#mac-sphere-clip)">
                                        <path d="M -2 6 C 6 14, 14 -2, 26 6" />
                                        <path d="M -2 12 C 6 20, 14 4, 26 12" />
                                        <path d="M -2 18 C 6 26, 14 10, 26 18" />
                                    </g>
                                </svg>
                            </div>
                            <span className="text-[13px] font-medium">Jasica AI</span>
                        </div>
                    </div>

                    <div className="space-y-0.5 px-2 mb-3">
                        <div className="flex items-center gap-2.5 px-2 py-1 rounded-md text-zinc-700">
                            <div className="w-5 h-5 rounded-[5px] bg-[#007AFF] flex items-center justify-center shrink-0 text-white">
                                <Wifi strokeWidth={2} className="w-3.5 h-3.5" />
                            </div>
                            <span className="text-[13px]">Wi-Fi</span>
                        </div>
                        <div className="flex items-center gap-2.5 px-2 py-1 rounded-md text-zinc-700">
                            <div className="w-5 h-5 rounded-[5px] bg-[#007AFF] flex items-center justify-center shrink-0 text-white">
                                <Bluetooth strokeWidth={2} className="w-3 h-3" />
                            </div>
                            <span className="text-[13px]">Bluetooth</span>
                        </div>
                        <div className="flex items-center gap-2.5 px-2 py-1 rounded-md text-zinc-700">
                            <div className="w-5 h-5 rounded-[5px] bg-[#007AFF] flex items-center justify-center shrink-0 text-white">
                                <Globe strokeWidth={2} className="w-3.5 h-3.5" />
                            </div>
                            <span className="text-[13px]">Network</span>
                        </div>
                    </div>
                    
                    <div className="space-y-0.5 px-2">
                        <div className="flex items-center gap-2.5 px-2 py-1 rounded-md text-zinc-700">
                            <div className="w-5 h-5 rounded-[5px] bg-[#FF3B30] flex items-center justify-center shrink-0 text-white">
                                <Bell strokeWidth={2} className="w-3.5 h-3.5 fill-white/20" />
                            </div>
                            <span className="text-[13px]">Notifications</span>
                        </div>
                        <div className="flex items-center gap-2.5 px-2 py-1 rounded-md text-zinc-700">
                            <div className="w-5 h-5 rounded-[5px] bg-[#FF2D55] flex items-center justify-center shrink-0 text-white">
                                <Volume2 strokeWidth={2} className="w-3.5 h-3.5 fill-white/20" />
                            </div>
                            <span className="text-[13px]">Sound</span>
                        </div>
                    </div>
                </div>
            </div>

            <div className="flex-1 bg-white flex flex-col relative overflow-hidden">
                
                <div className="h-[52px] flex items-center justify-center shrink-0 font-semibold text-[13px] text-zinc-800 border-b border-transparent md:border-zinc-200/50">
                    Jasica AI Settings
                </div>

                <div className="flex-1 overflow-y-auto px-6 md:px-10 pb-12 pt-2">
                    
                    <div className="text-[11px] font-semibold text-zinc-500 uppercase tracking-wider mb-1.5 ml-1">AI Core</div>
                    <div className="bg-white border border-zinc-200 rounded-lg mb-6 flex flex-col">
                        <div className="flex justify-between items-center px-4 py-3 border-b border-zinc-200">
                            <span className="text-[13px] text-zinc-800">Selected AI Model</span>
                            <span className="text-[13px] text-zinc-500 flex items-center gap-1">Gemini 2.5 Flash <ChevronDown strokeWidth={2} className="w-3.5 h-3.5" /></span>
                        </div>
                        <div className="flex flex-col px-4 py-3 border-b border-zinc-200">
                            <span className="text-[12px] text-zinc-500 text-center mb-3">Conversation Memory Context Limit</span>
                            <div className="flex items-center gap-3 w-full max-w-md mx-auto">
                                <span className="text-[11px] text-zinc-400 font-medium">1</span>
                                <div className="flex-1 h-1 bg-zinc-200 rounded-full relative">
                                    <div className="absolute left-0 top-0 h-full bg-[#007AFF] rounded-full w-[60%]"></div>
                                    <div className="absolute left-[60%] top-1/2 -translate-y-1/2 w-4 h-4 bg-white border border-zinc-300 rounded-full"></div>
                                </div>
                                <span className="text-[11px] text-zinc-400 font-medium">10</span>
                            </div>
                        </div>
                        <div className="flex justify-between items-center px-4 py-3">
                            <span className="text-[13px] text-zinc-800">Voice Calibration Language</span>
                            <span className="text-[13px] text-zinc-500 flex items-center gap-1">Bilingual (EN/BN)</span>
                        </div>
                    </div>

                    <div className="text-[11px] font-semibold text-zinc-500 uppercase tracking-wider mb-1.5 ml-1">Hardware & Shortcuts</div>
                    <div className="bg-white border border-zinc-200 rounded-lg flex flex-col mb-6">
                        <div className="flex justify-between items-center px-4 py-3 border-b border-zinc-200">
                            <span className="text-[13px] text-zinc-800">Wake Word Mode (Background Listen)</span>
                            <div className="w-10 h-6 bg-[#34c759] rounded-full relative">
                                <div className="w-5 h-5 bg-white rounded-full absolute right-[1.5px] top-[1.5px] border border-black/5"></div>
                            </div>
                        </div>
                        <div className="flex justify-between items-center px-4 py-3 border-b border-zinc-200">
                            <span className="text-[13px] text-zinc-800">Water Reminder Lock Screen Override</span>
                            <div className="w-10 h-6 bg-[#34c759] rounded-full relative">
                                <div className="w-5 h-5 bg-white rounded-full absolute right-[1.5px] top-[1.5px] border border-black/5"></div>
                            </div>
                        </div>
                        <div className="flex justify-between items-center px-4 py-3 border-b border-zinc-200">
                            <span className="text-[13px] text-zinc-800">Social Media & Camera Launcher</span>
                            <div className="w-10 h-6 bg-[#34c759] rounded-full relative">
                                <div className="w-5 h-5 bg-white rounded-full absolute right-[1.5px] top-[1.5px] border border-black/5"></div>
                            </div>
                        </div>
                        <div className="flex justify-between items-center px-4 py-3">
                            <span className="text-[13px] text-zinc-800">Local Offline Command Parser</span>
                            <div className="w-10 h-6 bg-[#34c759] rounded-full relative">
                                <div className="w-5 h-5 bg-white rounded-full absolute right-[1.5px] top-[1.5px] border border-black/5"></div>
                            </div>
                        </div>
                    </div>

                    <div className="text-[11px] font-semibold text-zinc-500 uppercase tracking-wider mb-1.5 ml-1">Device Network</div>
                    <div className="bg-white border border-zinc-200 rounded-lg flex flex-col">
                        <div className="flex justify-between items-center px-4 py-3 border-b border-zinc-200">
                            <span className="text-[13px] text-zinc-800">Bluetooth Connection Mode</span>
                            <span className="text-[13px] text-zinc-500 flex items-center gap-1">Dual (BLE & Classic) <ChevronDown strokeWidth={2} className="w-3.5 h-3.5" /></span>
                        </div>
                        <div className="flex flex-col px-4 py-3">
                            <span className="text-[12px] text-zinc-500 text-center mb-3">RGB Light Master Brightness Override</span>
                            <div className="flex items-center gap-3 w-full max-w-md mx-auto">
                                <Sun strokeWidth={1.5} className="w-4 h-4 text-zinc-400" />
                                <div className="flex-1 h-1 bg-zinc-200 rounded-full relative">
                                    <div className="absolute left-0 top-0 h-full bg-[#007AFF] rounded-full w-[85%]"></div>
                                    <div className="absolute left-[85%] top-1/2 -translate-y-1/2 w-4 h-4 bg-white border border-zinc-300 rounded-full"></div>
                                </div>
                                <SunDim strokeWidth={2.5} className="w-4 h-4 text-zinc-400" />
                            </div>
                        </div>
                    </div>

                </div>
            </div>
        </div>
    </section>

    <footer className="relative z-20 bg-[#f5f5f7] py-10 md:py-12 px-4 md:px-12 text-xs text-zinc-500 border-t border-zinc-300">
        <div className="max-w-4xl mx-auto">
            <div className="pb-5 border-b border-zinc-300 leading-relaxed font-normal">
                <p className="mb-2">1. The Neural Core Spring Release is optimized for devices running Windows 11.</p>
                <p>2. Actual download size and installation time may vary depending on your region and local network conditions.</p>
            </div>

            <div className="grid grid-cols-2 md:grid-cols-4 gap-8 py-8">
                <div>
                    <h3 className="text-zinc-900 font-semibold mb-3">Explore Jasica</h3>
                    <ul className="space-y-3 font-normal">
                        <li><a href="#" className="underline underline-offset-4 decoration-1 decoration-zinc-300 text-zinc-600 cursor-pointer">Windows</a></li>
                        <li><a href="#" className="underline underline-offset-4 decoration-1 decoration-zinc-300 text-zinc-600 cursor-pointer">Android</a></li>
                        <li><a href="#" className="underline underline-offset-4 decoration-1 decoration-zinc-300 text-zinc-600 cursor-pointer">Web</a></li>
                    </ul>
                </div>
                <div>
                    <h3 className="text-zinc-900 font-semibold mb-3">Resources</h3>
                    <ul className="space-y-3 font-normal">
                        <li><a href="#" className="underline underline-offset-4 decoration-1 decoration-zinc-300 text-zinc-600 cursor-pointer">Documentation</a></li>
                        <li><a href="#" className="underline underline-offset-4 decoration-1 decoration-zinc-300 text-zinc-600 cursor-pointer">API Reference</a></li>
                        <li><a href="#" className="underline underline-offset-4 decoration-1 decoration-zinc-300 text-zinc-600 cursor-pointer">Community Forum</a></li>
                    </ul>
                </div>
                <div>
                    <h3 className="text-zinc-900 font-semibold mb-3">Company</h3>
                    <ul className="space-y-3 font-normal">
                        <li><a href="#" className="underline underline-offset-4 decoration-1 decoration-zinc-300 text-zinc-600 cursor-pointer">About Jasica AI</a></li>
                        <li><a href="#" className="underline underline-offset-4 decoration-1 decoration-zinc-300 text-zinc-600 cursor-pointer">Careers</a></li>
                        <li><a href="#" className="underline underline-offset-4 decoration-1 decoration-zinc-300 text-zinc-600 cursor-pointer">Investors</a></li>
                    </ul>
                </div>
                <div>
                    <h3 className="text-zinc-900 font-semibold mb-3">Support</h3>
                    <ul className="space-y-3 font-normal">
                        <li><a href="#" className="underline underline-offset-4 decoration-1 decoration-zinc-300 text-zinc-600 cursor-pointer">Contact Us</a></li>
                        <li><a href="#" className="underline underline-offset-4 decoration-1 decoration-zinc-300 text-zinc-600 cursor-pointer">System Status</a></li>
                        <li><a href="#" className="underline underline-offset-4 decoration-1 decoration-zinc-300 text-zinc-600 cursor-pointer">Release Notes</a></li>
                    </ul>
                </div>
            </div>

            <div className="pt-6 border-t border-zinc-300 flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
                <div className="flex flex-col xl:flex-row items-start xl:items-center gap-2 xl:gap-8 font-normal">
                    <p>Copyright &copy; 2026 Jasica AI Inc. All rights reserved.</p>
                    <div className="flex flex-wrap items-center gap-x-3 gap-y-2">
                        <a href="#" className="underline underline-offset-4 decoration-1 decoration-zinc-300 text-zinc-600 cursor-pointer">Privacy Policy</a>
                        <span className="text-zinc-300 hidden sm:inline">|</span>
                        <a href="#" className="underline underline-offset-4 decoration-1 decoration-zinc-300 text-zinc-600 cursor-pointer">Terms of Use</a>
                        <span className="text-zinc-300 hidden sm:inline">|</span>
                        <a href="#" className="underline underline-offset-4 decoration-1 decoration-zinc-300 text-zinc-600 cursor-pointer">Legal</a>
                        <span className="text-zinc-300 hidden sm:inline">|</span>
                        <a href="#" className="underline underline-offset-4 decoration-1 decoration-zinc-300 text-zinc-600 cursor-pointer">Site Map</a>
                    </div>
                </div>
                <div className="flex items-center gap-2 font-medium text-zinc-600">
                    <Globe strokeWidth={1.5} className="w-4 h-4" />
                    <span>India</span>
                </div>
            </div>
        </div>
    </footer>

    </div>
  );
}
"""

with open('app/page.tsx', 'w', encoding='utf-8') as f:
    f.write(top_part + bottom_part)

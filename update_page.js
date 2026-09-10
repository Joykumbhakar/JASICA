const fs = require('fs');
let content = fs.readFileSync('app/page.tsx', 'utf-8');

// Replace imports
const newImports = `import { 
  Download, 
  LayoutTemplate, 
  Layers, 
  Info, 
  DownloadCloud, 
  ChevronRight,
  BrainCircuit,
  Languages,
  Mic,
  WifiOff,
  Cpu,
  Monitor,
  Lightbulb,
  Plug,
  Wind,
  Fan,
  Code,
  Droplet,
  SmartphoneCharging,
  SquarePlay,
  Instagram,
  Facebook,
  Linkedin,
  Music,
  Camera,
  Paintbrush,
  Check,
  Globe
} from "lucide-react";`;

content = content.replace(/import \{[\s\S]*?\} from "lucide-react";/, newImports);

// Replace everything from `return (` to the end of the file
const returnIndex = content.indexOf('  return (');
const endContent = `  return (
    <div className="min-h-screen bg-[#f5f5f7] text-[#1d1d1f] font-sans antialiased overflow-x-hidden selection:bg-cyan-500 selection:text-black">
      
      {/* Top Navigation */}
      <nav className="fixed top-0 left-0 w-full h-14 bg-white/70 backdrop-blur-lg border-b border-zinc-200 z-50 flex items-center justify-between px-4 md:px-8">
        <div className="font-semibold text-lg tracking-tight flex items-center gap-2 text-black">
            <svg viewBox="0 0 24 24" width="24" height="24" stroke="currentColor" strokeWidth="1.25" fill="none" strokeLinecap="round" strokeLinejoin="round" className="w-6 h-6">
                <circle cx="12" cy="12" r="10"></circle>
                <path d="M3.5 7.5 C 9 11.5, 15 4.5, 20.5 8.5"></path>
                <path d="M2 12 C 8 16, 16 9, 22 13"></path>
                <path d="M4 16.5 C 9 20, 15 14, 20 17.5"></path>
            </svg>
            Jasica AI
        </div>
        <div className="flex items-center gap-6 text-sm font-medium text-zinc-500">
            <a href="#" className="text-black cursor-pointer group">
                <span className="underline underline-offset-4 decoration-1 decoration-zinc-400">Overview</span>
            </a>
            <a href="#specs" className="cursor-pointer group">
                <span className="underline underline-offset-4 decoration-1 decoration-zinc-300">Tech Specs</span>
            </a>
        </div>
      </nav>

      {/* Hero Headline */}
      <div className="absolute top-[22vh] sm:top-[20vh] md:top-[18vh] left-0 w-full px-4 text-center z-0 pointer-events-none flex flex-col items-center">
          <h1 className="mobile-title-scale text-[7rem] sm:text-[9rem] md:text-[12rem] lg:text-[16rem] font-bold tracking-apple-tighter leading-[0.8] animate-fade-up pb-2 whitespace-nowrap">
              <span className="text-black">Jasica</span>{" "}
              <span className="bg-gradient-to-b from-zinc-600 to-black bg-clip-text text-transparent">AI.</span>
          </h1>
      </div>

      {/* Floating Download Button */}
      <div className="fixed bottom-10 md:bottom-14 left-0 w-full flex justify-center z-50 pointer-events-none animate-fade-up delay-200">
          <button 
              onClick={() => {
                  const specElement = document.getElementById("specs");
                  specElement?.scrollIntoView({ behavior: "smooth" });
              }}
              className="pointer-events-auto bg-black text-white text-lg md:text-xl font-medium px-12 md:px-16 py-4 md:py-5 rounded-full cursor-pointer transition-colors duration-200 flex items-center gap-3">
              <Download strokeWidth={1} className="w-6 h-6" />
              <span>Download Now</span>
          </button>
      </div>

      {/* 3D Canvas Container */}
      <div id="canvas-container" ref={containerRef} />

      {/* System Specifications Section */}
      <section id="specs" className="relative z-20 bg-[#f5f5f7] min-h-screen py-32 px-4 md:px-12 mt-[110vh]">
          <div className="max-w-4xl mx-auto">
              <div className="text-center mb-16">
                  <h2 className="text-4xl md:text-5xl font-semibold tracking-tight text-black mb-4">System Specifications</h2>
                  <p className="text-xl text-zinc-500 font-normal max-w-2xl mx-auto">Everything you need to know about the latest release, structured for clarity and quick access.</p>
              </div>

              {/* Apple-style Card UI for 4x2 Table */}
              <div className="border border-zinc-200 rounded-[2rem] overflow-hidden bg-white max-w-3xl mx-auto">
                  
                  {/* Row 1 */}
                  <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center py-6 px-8 border-b border-zinc-200">
                      <div className="flex items-center gap-3 w-1/3 mb-1 sm:mb-0">
                          <div className="w-8 h-8 rounded-lg bg-[#007AFF] flex items-center justify-center text-white shrink-0">
                              <LayoutTemplate strokeWidth={1} className="w-5 h-5" />
                          </div>
                          <span className="text-zinc-500 font-medium text-lg">App Name</span>
                      </div>
                      <span className="text-zinc-900 font-semibold text-lg sm:text-right w-full sm:w-2/3 mt-2 sm:mt-0 pl-11 sm:pl-0">Jasica AI</span>
                  </div>
                  
                  {/* Row 2 */}
                  <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center py-6 px-8 border-b border-zinc-200">
                      <div className="flex items-center gap-3 w-1/3 mb-1 sm:mb-0">
                          <div className="w-8 h-8 rounded-lg bg-[#34C759] flex items-center justify-center text-white shrink-0">
                              <Layers strokeWidth={1} className="w-5 h-5" />
                          </div>
                          <span className="text-zinc-500 font-medium text-lg">Update Name</span>
                      </div>
                      <span className="text-zinc-900 font-semibold text-lg sm:text-right w-full sm:w-2/3 mt-2 sm:mt-0 pl-11 sm:pl-0">Neural Core Spring Release</span>
                  </div>
                  
                  {/* Row 3 */}
                  <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center py-6 px-8 border-b border-zinc-200">
                      <div className="flex items-center gap-3 w-1/3 mb-1 sm:mb-0">
                          <div className="w-8 h-8 rounded-lg bg-[#8E8E93] flex items-center justify-center text-white shrink-0">
                              <Info strokeWidth={1} className="w-5 h-5" />
                          </div>
                          <span className="text-zinc-500 font-medium text-lg">Version</span>
                      </div>
                      <span className="text-zinc-900 font-semibold text-lg sm:text-right w-full sm:w-2/3 mt-2 sm:mt-0 pl-11 sm:pl-0">v2.4.0 (Build 2409)</span>
                  </div>
                  
                  {/* Row 4 */}
                  <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center py-6 px-8">
                      <div className="flex items-center gap-3 w-1/3 mb-1 sm:mb-0">
                          <div className="w-8 h-8 rounded-lg bg-[#5856D6] flex items-center justify-center text-white shrink-0">
                              <DownloadCloud strokeWidth={1} className="w-5 h-5" />
                          </div>
                          <span className="text-zinc-500 font-medium text-lg">Direct Link</span>
                      </div>
                      <div className="sm:text-right w-full sm:w-2/3 flex justify-start sm:justify-end mt-2 sm:mt-0 pl-11 sm:pl-0">
                          <a href="#specs" className="text-blue-600 font-semibold text-lg flex items-center gap-2 cursor-pointer group">
                              <span className="underline underline-offset-4 decoration-1">Download Package (.zip)</span>
                              <ChevronRight strokeWidth={1.5} className="w-4 h-4 text-blue-600" />
                          </a>
                      </div>
                  </div>
                  
              </div>
          </div>
      </section>

      {/* Bento Grid */}
      <section className="relative z-20 bg-[#f5f5f7] py-16 md:py-24 px-4 md:px-12">
          <div className="max-w-6xl mx-auto">
              
              <div className="text-center mb-16 md:mb-20">
                  <h2 className="text-4xl md:text-5xl font-semibold tracking-tight text-black mb-4">Intelligence, built in.</h2>
                  <p className="text-xl text-zinc-500 font-normal max-w-2xl mx-auto">A seamless blend of emotional AI, hardware control, and everyday utility.</p>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-6 gap-6">
                  
                  {/* 1. AI Voice Assistant */}
                  <div className="col-span-1 md:col-span-6 bg-white border border-zinc-200 rounded-[2rem] p-8 md:p-12 flex flex-col md:flex-row gap-10 items-center overflow-hidden">
                      <div className="flex-1">
                          <div className="w-12 h-12 rounded-[14px] bg-[#5856D6] flex items-center justify-center text-white mb-6">
                              <svg viewBox="0 0 24 24" width="24" height="24" stroke="currentColor" strokeWidth="1.25" fill="none" strokeLinecap="round" strokeLinejoin="round" className="w-6 h-6">
                                  <circle cx="12" cy="12" r="10"></circle>
                                  <path d="M3.5 7.5 C 9 11.5, 15 4.5, 20.5 8.5"></path>
                                  <path d="M2 12 C 8 16, 16 9, 22 13"></path>
                                  <path d="M4 16.5 C 9 20, 15 14, 20 17.5"></path>
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

                  {/* 2. Smart Home & Hardware */}
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

                  {/* 3. Health & Wellness */}
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
                  
                  {/* 4. System Shortcuts */}
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

                  {/* 5. UI, UX & Customization */}
                  <div className="col-span-1 md:col-span-3 bg-white border border-zinc-200 rounded-[2rem] p-8 md:p-10 flex flex-col justify-between">
                      <div>
                          <div className="w-12 h-12 rounded-[14px] bg-[#8E8E93] flex items-center justify-center text-white mb-6">
                              <Paintbrush strokeWidth={1.5} className="w-6 h-6" />
                          </div>
                          <h3 className="text-2xl font-semibold text-black mb-3 tracking-tight">Fluid Experience</h3>
                          <p className="text-zinc-500 leading-relaxed font-normal mb-8">
                              Enjoy dynamic app icons that adapt to preference, beautiful Lottie animations mirroring the AI's state, and cloud-synced configurations directly from osmac.vercel.app.
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

      {/* Footer */}
      <footer className="relative z-20 bg-[#f5f5f7] py-10 md:py-12 px-4 md:px-12 text-xs text-zinc-500 border-t border-zinc-300">
          <div className="max-w-4xl mx-auto">
              <div className="pb-5 border-b border-zinc-300 leading-relaxed font-normal">
                  <p className="mb-2">1. The Neural Core Spring Release is optimized for devices running macOS 14.0 or later, and Windows 11.</p>
                  <p>2. Actual download size and installation time may vary depending on your region and local network conditions.</p>
              </div>

              <div className="grid grid-cols-2 md:grid-cols-4 gap-8 py-8">
                  <div>
                      <h3 className="text-zinc-900 font-semibold mb-3">Explore Jasica</h3>
                      <ul className="space-y-3 font-normal">
                          <li><a href="#" className="underline underline-offset-4 decoration-1 decoration-zinc-300 text-zinc-600 cursor-pointer">Mac</a></li>
                          <li><a href="#" className="underline underline-offset-4 decoration-1 decoration-zinc-300 text-zinc-600 cursor-pointer">iPad</a></li>
                          <li><a href="#" className="underline underline-offset-4 decoration-1 decoration-zinc-300 text-zinc-600 cursor-pointer">iPhone</a></li>
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
`;
fs.writeFileSync('app/page.tsx', content.substring(0, returnIndex) + endContent);

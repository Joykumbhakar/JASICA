"use client";

import React, { useState } from "react";
import Navbar from "@/components/Navbar";
import Link from "next/link";
import { 
  Server, 
  Globe, 
  Terminal, 
  ShieldCheck, 
  Copy, 
  Check, 
  ExternalLink, 
  Cpu, 
  Key, 
  Zap, 
  Sliders, 
  Radio, 
  RefreshCw,
  FolderGit2,
  Lock,
  ArrowRight,
  Code2
} from "lucide-react";

export default function HostingPage() {
  const [copiedId, setCopiedId] = useState<string | null>(null);
  const [customDomain, setCustomDomain] = useState("jasica.yourdomain.com");
  const [activeTab, setActiveTab] = useState<"docker" | "vercel" | "tunnel" | "pm2" | "api">("docker");
  const [testCmd, setTestCmd] = useState("a");
  const [apiResponse, setApiResponse] = useState<string | null>(null);
  const [isSending, setIsSending] = useState(false);

  const copyToClipboard = (text: string, id: string) => {
    navigator.clipboard.writeText(text);
    setCopiedId(id);
    setTimeout(() => setCopiedId(null), 2000);
  };

  const testApi = async () => {
    setIsSending(true);
    try {
      const res = await fetch("/api/command", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ command: testCmd, source: "HOSTING_DASHBOARD_TESTER" }),
      });
      const data = await res.json();
      setApiResponse(JSON.stringify(data, null, 2));
    } catch (err: any) {
      setApiResponse(JSON.stringify({ error: err.message }, null, 2));
    } finally {
      setIsSending(false);
    }
  };

  return (
    <div className="min-h-screen bg-[#06070d] text-white flex flex-col selection:bg-cyan-500 selection:text-black">
      
      {/* Background Glow */}
      <div className="fixed inset-0 bg-[radial-gradient(ellipse_at_top,_var(--tw-gradient-stops))] from-cyan-950/30 via-[#06070d] to-[#06070d] pointer-events-none" />
      <div className="fixed inset-0 bg-[linear-gradient(to_right,#00f2fe05_1px,transparent_1px),linear-gradient(to_bottom,#00f2fe05_1px,transparent_1px)] bg-[size:32px_32px] pointer-events-none" />

      <Navbar />

      <main className="relative z-10 flex-1 max-w-7xl w-full mx-auto px-4 py-8">
        
        {/* Header Banner */}
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 pb-8 border-b border-white/10">
          <div>
            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-cyan-950/60 border border-cyan-500/30 text-cyan-400 text-xs font-mono mb-2">
              <Server className="w-3.5 h-3.5" strokeWidth={1.5} />
              <span className="underline underline-offset-4 decoration-cyan-400/50">CUSTOM HOSTING & DEPLOYMENT CENTER</span>
            </div>
            <h1 className="text-3xl sm:text-4xl font-black font-orbitron tracking-wide text-white underline underline-offset-8 decoration-cyan-400/40">
              Host & Deploy JASICA AI
            </h1>
            <p className="text-sm text-white/60 font-mono mt-2 underline underline-offset-4 decoration-white/20">
              Production self-hosting, custom domain DNS, Cloudflare tunnels, and IoT REST API webhooks.
            </p>
          </div>

          <div className="flex items-center gap-3">
            <Link
              href="/"
              className="px-4 py-2 rounded-xl bg-white/5 border border-white/15 text-xs font-semibold hover:bg-white/10 transition underline underline-offset-4 decoration-white/30"
            >
              Back to Live Console
            </Link>
            <button
              onClick={() => copyToClipboard(typeof window !== 'undefined' ? window.location.origin : '', "origin")}
              className="px-4 py-2 rounded-xl bg-gradient-to-r from-cyan-500 to-blue-600 text-black text-xs font-bold flex items-center gap-2 shadow-glow hover:opacity-95 transition underline underline-offset-4 decoration-black/40"
            >
              {copiedId === "origin" ? <Check className="w-3.5 h-3.5" strokeWidth={1.5} /> : <Copy className="w-3.5 h-3.5" strokeWidth={1.5} />}
              <span>Copy Endpoint URL</span>
            </button>
          </div>
        </div>

        {/* Custom Domain & Server Status Overview */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 mt-8">
          
          {/* Custom Domain Config Card */}
          <div className="lg:col-span-2 p-6 rounded-2xl bg-[#0e101f]/80 border border-cyan-500/20 backdrop-blur-xl">
            <div className="flex items-center gap-3 mb-4">
              <div className="p-2.5 rounded-xl bg-cyan-950 border border-cyan-500/30 text-cyan-400">
                <Globe className="w-5 h-5" strokeWidth={1.5} />
              </div>
              <div>
                <h3 className="text-base font-bold font-orbitron text-white underline underline-offset-4 decoration-cyan-400/50">
                  Custom Domain & DNS Records
                </h3>
                <p className="text-xs text-white/50 font-mono">
                  Point your personal domain or subdomain directly to this Next.js instance
                </p>
              </div>
            </div>

            <div className="flex flex-col sm:flex-row items-center gap-3 mb-6">
              <input
                type="text"
                value={customDomain}
                onChange={(e) => setCustomDomain(e.target.value)}
                placeholder="e.g., jasica.yourdomain.com"
                className="w-full sm:flex-1 bg-black/60 border border-white/15 rounded-xl px-4 py-2.5 text-sm font-mono text-cyan-300 outline-none focus:border-cyan-400 transition"
              />
              <button
                onClick={() => copyToClipboard(`CNAME ${customDomain} -> host.jasica.ai`, "dns")}
                className="w-full sm:w-auto px-4 py-2.5 rounded-xl bg-cyan-500/20 border border-cyan-400/40 text-cyan-300 text-xs font-bold flex items-center justify-center gap-2 hover:bg-cyan-500/30 transition underline underline-offset-4 decoration-cyan-400/40"
              >
                {copiedId === "dns" ? <Check className="w-4 h-4" strokeWidth={1.5} /> : <Copy className="w-4 h-4" strokeWidth={1.5} />}
                <span>Generate DNS Records</span>
              </button>
            </div>

            {/* DNS Records Table */}
            <div className="overflow-x-auto rounded-xl border border-white/10 bg-black/40">
              <table className="w-full text-left text-xs font-mono">
                <thead className="bg-white/5 text-white/60 border-b border-white/10">
                  <tr>
                    <th className="p-3 underline underline-offset-2 decoration-white/20">Type</th>
                    <th className="p-3 underline underline-offset-2 decoration-white/20">Host / Name</th>
                    <th className="p-3 underline underline-offset-2 decoration-white/20">Value / Target</th>
                    <th className="p-3 underline underline-offset-2 decoration-white/20">TTL</th>
                    <th className="p-3 underline underline-offset-2 decoration-white/20">Proxy</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-white/5 text-white/80">
                  <tr>
                    <td className="p-3 text-cyan-400 font-bold underline underline-offset-2 decoration-cyan-400/40">CNAME</td>
                    <td className="p-3">{customDomain.split(".")[0] || "jasica"}</td>
                    <td className="p-3 text-emerald-400 underline underline-offset-2 decoration-emerald-500/40">cname.vercel-dns.com / @</td>
                    <td className="p-3">Auto</td>
                    <td className="p-3 text-cyan-300">Proxied (Cloudflare)</td>
                  </tr>
                  <tr>
                    <td className="p-3 text-purple-400 font-bold underline underline-offset-2 decoration-purple-400/40">A Record</td>
                    <td className="p-3">@</td>
                    <td className="p-3 text-emerald-400 underline underline-offset-2 decoration-emerald-500/40">76.76.21.21 (or Your VPS IP)</td>
                    <td className="p-3">3600</td>
                    <td className="p-3 text-purple-300">DNS Only</td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>

          {/* Quick Health & Server Metrics Card */}
          <div className="p-6 rounded-2xl bg-[#0e101f]/80 border border-purple-500/20 backdrop-blur-xl flex flex-col justify-between">
            <div>
              <div className="flex items-center gap-3 mb-4">
                <div className="p-2.5 rounded-xl bg-purple-950 border border-purple-500/30 text-purple-400">
                  <ShieldCheck className="w-5 h-5" strokeWidth={1.5} />
                </div>
                <div>
                  <h3 className="text-base font-bold font-orbitron text-white underline underline-offset-4 decoration-purple-400/50">
                    Cloud Bridge Health
                  </h3>
                  <p className="text-xs text-white/50 font-mono">Real-time Node.js status</p>
                </div>
              </div>

              <div className="space-y-3 font-mono text-xs">
                <div className="flex justify-between p-2 rounded-lg bg-black/40 border border-white/5">
                  <span className="text-white/50">Next.js Mode:</span>
                  <span className="text-cyan-400 font-semibold underline underline-offset-2 decoration-cyan-400/40">Standalone Production</span>
                </div>
                <div className="flex justify-between p-2 rounded-lg bg-black/40 border border-white/5">
                  <span className="text-white/50">API Endpoints:</span>
                  <span className="text-emerald-400 font-semibold underline underline-offset-2 decoration-emerald-500/40">Active (/api/command)</span>
                </div>
                <div className="flex justify-between p-2 rounded-lg bg-black/40 border border-white/5">
                  <span className="text-white/50">Hardware Target:</span>
                  <span className="text-purple-400 font-semibold underline underline-offset-2 decoration-purple-400/40">Arduino UNO (D2-D13)</span>
                </div>
                <div className="flex justify-between p-2 rounded-lg bg-black/40 border border-white/5">
                  <span className="text-white/50">SSL / TLS:</span>
                  <span className="text-emerald-400 font-semibold underline underline-offset-2 decoration-emerald-500/40">HTTPS Ready</span>
                </div>
              </div>
            </div>

            <a
              href="/api/health"
              target="_blank"
              className="mt-4 w-full py-2 rounded-xl bg-white/5 border border-white/15 hover:bg-white/10 text-xs font-mono text-center flex items-center justify-center gap-1.5 text-white/80 transition underline underline-offset-4 decoration-white/40"
            >
              <span>View /api/health JSON</span>
              <ExternalLink className="w-3 h-3" strokeWidth={1.5} />
            </a>
          </div>

        </div>

        {/* Deployment Guides Navigation */}
        <div className="mt-10">
          <div className="flex items-center gap-2 border-b border-white/10 pb-3 overflow-x-auto no-scrollbar">
            {[
              { id: "docker", label: "Docker & Compose", icon: FolderGit2 },
              { id: "vercel", label: "Vercel / Netlify", icon: Globe },
              { id: "tunnel", label: "Cloudflare Tunnel (IoT)", icon: Zap },
              { id: "pm2", label: "PM2 / Node.js VPS", icon: Server },
              { id: "api", label: "REST API & Siri/Home Assistant", icon: Code2 },
            ].map((tab) => {
              const Icon = tab.icon;
              const isActive = activeTab === tab.id;
              return (
                <button
                  key={tab.id}
                  onClick={() => setActiveTab(tab.id as any)}
                  className={`flex items-center gap-2 px-4 py-2 rounded-xl text-xs font-semibold whitespace-nowrap transition underline underline-offset-4 ${
                    isActive
                      ? "bg-cyan-500/20 border border-cyan-400 text-cyan-300 shadow-glow decoration-cyan-300"
                      : "bg-white/5 border border-transparent text-white/60 hover:text-white hover:bg-white/10 decoration-white/30"
                  }`}
                >
                  <Icon className="w-4 h-4" strokeWidth={1.5} />
                  <span>{tab.label}</span>
                </button>
              );
            })}
          </div>

          {/* Tab 1: Docker Self-Hosting */}
          {activeTab === "docker" && (
            <div className="mt-6 p-6 rounded-2xl bg-[#0c0e1a]/90 border border-cyan-500/20 backdrop-blur-xl">
              <div className="flex items-center justify-between mb-4">
                <div>
                  <h3 className="text-lg font-bold font-orbitron text-white underline underline-offset-4 decoration-cyan-400/40">Docker Self-Hosting Container</h3>
                  <p className="text-xs font-mono text-white/50">Run JASICA on any Linux server, Raspberry Pi, or local NAS</p>
                </div>
                <button
                  onClick={() => copyToClipboard(`docker compose up -d`, "docker-cmd")}
                  className="px-3 py-1.5 rounded-lg bg-cyan-500/20 border border-cyan-400/40 text-cyan-300 text-xs font-mono flex items-center gap-1.5 hover:bg-cyan-500/30 transition underline underline-offset-2 decoration-cyan-400/40"
                >
                  {copiedId === "docker-cmd" ? <Check className="w-3.5 h-3.5" strokeWidth={1.5} /> : <Copy className="w-3.5 h-3.5" strokeWidth={1.5} />}
                  <span>Copy Command</span>
                </button>
              </div>

              <div className="space-y-4 font-mono text-xs">
                <div>
                  <span className="text-white/60 mb-1 block underline underline-offset-2 decoration-white/20">1. Build and start the container:</span>
                  <pre className="p-4 rounded-xl bg-black/70 border border-white/10 text-cyan-300 overflow-x-auto">
                    {`# Build Docker image
docker build -t jasica-ai .

# Run container on port 3000
docker run -d --name jasica -p 3000:3000 --restart always jasica-ai`}
                  </pre>
                </div>

                <div>
                  <span className="text-white/60 mb-1 block underline underline-offset-2 decoration-white/20">2. Or use Docker Compose:</span>
                  <pre className="p-4 rounded-xl bg-black/70 border border-white/10 text-purple-300 overflow-x-auto">
                    {`version: '3.8'
services:
  jasica:
    build: .
    container_name: jasica-app
    ports:
      - "3000:3000"
    restart: unless-stopped
    environment:
      - NODE_ENV=production
      - PORT=3000`}
                  </pre>
                </div>
              </div>
            </div>
          )}

          {/* Tab 2: Vercel / Cloud */}
          {activeTab === "vercel" && (
            <div className="mt-6 p-6 rounded-2xl bg-[#0c0e1a]/90 border border-cyan-500/20 backdrop-blur-xl">
              <div className="flex items-center justify-between mb-4">
                <div>
                  <h3 className="text-lg font-bold font-orbitron text-white underline underline-offset-4 decoration-cyan-400/40">Deploy to Vercel or Netlify</h3>
                  <p className="text-xs font-mono text-white/50">Zero-configuration cloud deployment with global edge latency</p>
                </div>
                <button
                  onClick={() => copyToClipboard(`npx vercel --prod`, "vercel-cmd")}
                  className="px-3 py-1.5 rounded-lg bg-cyan-500/20 border border-cyan-400/40 text-cyan-300 text-xs font-mono flex items-center gap-1.5 hover:bg-cyan-500/30 transition underline underline-offset-2 decoration-cyan-400/40"
                >
                  {copiedId === "vercel-cmd" ? <Check className="w-3.5 h-3.5" strokeWidth={1.5} /> : <Copy className="w-3.5 h-3.5" strokeWidth={1.5} />}
                  <span>Copy Deploy Command</span>
                </button>
              </div>

              <div className="space-y-4 font-mono text-xs">
                <pre className="p-4 rounded-xl bg-black/70 border border-white/10 text-cyan-300 overflow-x-auto">
                  {`# Deploy directly using Vercel CLI
npx vercel

# Deploy directly to production
npx vercel --prod`}
                </pre>
                <div className="p-4 rounded-xl bg-cyan-950/30 border border-cyan-500/30 text-cyan-200">
                  <p className="font-bold mb-1 underline underline-offset-2 decoration-cyan-400/40">💡 Tip for Cloud Deployment:</p>
                  <p className="text-white/70">
                    When hosted on Vercel or cloud servers, the Web Serial API still operates seamlessly from any Chrome/Edge browser on the client side to communicate with your local USB Arduino!
                  </p>
                </div>
              </div>
            </div>
          )}

          {/* Tab 3: Cloudflare Tunnel */}
          {activeTab === "tunnel" && (
            <div className="mt-6 p-6 rounded-2xl bg-[#0c0e1a]/90 border border-cyan-500/20 backdrop-blur-xl">
              <div className="flex items-center justify-between mb-4">
                <div>
                  <h3 className="text-lg font-bold font-orbitron text-white underline underline-offset-4 decoration-cyan-400/40">Cloudflare Tunnel (Zero-Port-Forwarding)</h3>
                  <p className="text-xs font-mono text-white/50">Expose your local Arduino host securely to the internet with free HTTPS</p>
                </div>
                <button
                  onClick={() => copyToClipboard(`cloudflared tunnel --url http://localhost:3000`, "cf-cmd")}
                  className="px-3 py-1.5 rounded-lg bg-cyan-500/20 border border-cyan-400/40 text-cyan-300 text-xs font-mono flex items-center gap-1.5 hover:bg-cyan-500/30 transition underline underline-offset-2 decoration-cyan-400/40"
                >
                  {copiedId === "cf-cmd" ? <Check className="w-3.5 h-3.5" strokeWidth={1.5} /> : <Copy className="w-3.5 h-3.5" strokeWidth={1.5} />}
                  <span>Copy Tunnel Command</span>
                </button>
              </div>

              <div className="space-y-4 font-mono text-xs">
                <pre className="p-4 rounded-xl bg-black/70 border border-white/10 text-emerald-300 overflow-x-auto">
                  {`# 1. Install cloudflared CLI
winget install Cloudflare.cloudflared   # Windows
brew install cloudflared               # macOS
sudo apt-get install cloudflared       # Ubuntu/Debian

# 2. Expose your local Next.js instance:
cloudflared tunnel --url http://localhost:3000`}
                </pre>
              </div>
            </div>
          )}

          {/* Tab 4: PM2 Background Runner */}
          {activeTab === "pm2" && (
            <div className="mt-6 p-6 rounded-2xl bg-[#0c0e1a]/90 border border-cyan-500/20 backdrop-blur-xl">
              <div className="flex items-center justify-between mb-4">
                <div>
                  <h3 className="text-lg font-bold font-orbitron text-white underline underline-offset-4 decoration-cyan-400/40">PM2 Process Manager for VPS</h3>
                  <p className="text-xs font-mono text-white/50">Keep JASICA running 24/7 in the background with auto-restart</p>
                </div>
                <button
                  onClick={() => copyToClipboard(`pm2 start npm --name "jasica" -- start`, "pm2-cmd")}
                  className="px-3 py-1.5 rounded-lg bg-cyan-500/20 border border-cyan-400/40 text-cyan-300 text-xs font-mono flex items-center gap-1.5 hover:bg-cyan-500/30 transition underline underline-offset-2 decoration-cyan-400/40"
                >
                  {copiedId === "pm2-cmd" ? <Check className="w-3.5 h-3.5" strokeWidth={1.5} /> : <Copy className="w-3.5 h-3.5" strokeWidth={1.5} />}
                  <span>Copy PM2 Script</span>
                </button>
              </div>

              <div className="space-y-4 font-mono text-xs">
                <pre className="p-4 rounded-xl bg-black/70 border border-white/10 text-cyan-300 overflow-x-auto">
                  {`# 1. Build production bundle
npm run build

# 2. Start with PM2
pm2 start npm --name "jasica" -- start

# 3. Save startup configuration
pm2 save
pm2 startup`}
                </pre>
              </div>
            </div>
          )}

          {/* Tab 5: REST API & Siri/Home Assistant */}
          {activeTab === "api" && (
            <div className="mt-6 p-6 rounded-2xl bg-[#0c0e1a]/90 border border-cyan-500/20 backdrop-blur-xl">
              <div className="mb-6">
                <h3 className="text-lg font-bold font-orbitron text-white underline underline-offset-4 decoration-cyan-400/40">REST API Webhook Playground</h3>
                <p className="text-xs font-mono text-white/50">Send commands from Home Assistant, iOS Siri Shortcuts, or external webhooks</p>
              </div>

              {/* Interactive Tester */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div className="space-y-4">
                  <div>
                    <label className="text-xs font-mono text-white/70 block mb-1 underline underline-offset-2 decoration-white/20">Target Command:</label>
                    <div className="flex gap-2">
                      <select
                        value={testCmd}
                        onChange={(e) => setTestCmd(e.target.value)}
                        className="bg-black/60 border border-white/15 rounded-xl px-3 py-2 text-xs font-mono text-cyan-300 outline-none focus:border-cyan-400"
                      >
                        <option value="a">a - Turn ON PC</option>
                        <option value="A">A - Turn OFF PC</option>
                        <option value="b">b - Turn ON RGB</option>
                        <option value="B">B - Turn OFF RGB</option>
                        <option value="c">c - Turn ON Room Light</option>
                        <option value="C">C - Turn OFF Room Light</option>
                        <option value="d">d - Turn ON Plug</option>
                        <option value="D">D - Turn OFF Plug</option>
                        <option value="on">on - All Devices ON</option>
                        <option value="off">off - All Devices OFF</option>
                        <option value="mood">mood - Stark Tower Mode</option>
                      </select>
                      <button
                        onClick={testApi}
                        disabled={isSending}
                        className="px-4 py-2 rounded-xl bg-gradient-to-r from-cyan-500 to-blue-600 text-black text-xs font-bold shadow-glow hover:opacity-90 disabled:opacity-50 underline underline-offset-4 decoration-black/40"
                      >
                        {isSending ? "Executing..." : "Send Request"}
                      </button>
                    </div>
                  </div>

                  <div>
                    <span className="text-xs font-mono text-white/70 block mb-1 underline underline-offset-2 decoration-white/20">cURL Example:</span>
                    <pre className="p-3 rounded-xl bg-black/60 border border-white/10 text-cyan-300 text-[11px] overflow-x-auto">
                      {`curl -X POST ${typeof window !== 'undefined' ? window.location.origin : 'http://localhost:3000'}/api/command \\
  -H "Content-Type: application/json" \\
  -d '{"command": "${testCmd}", "source": "SIRI_SHORTCUT"}'`}
                    </pre>
                  </div>
                </div>

                <div>
                  <span className="text-xs font-mono text-white/70 block mb-1 underline underline-offset-2 decoration-white/20">Live API Response:</span>
                  <pre className="p-4 rounded-xl bg-black/80 border border-cyan-500/30 text-emerald-300 text-xs font-mono h-40 overflow-y-auto">
                    {apiResponse || "// Click 'Send Request' to test webhook output"}
                  </pre>
                </div>
              </div>
            </div>
          )}

        </div>

      </main>

    </div>
  );
}

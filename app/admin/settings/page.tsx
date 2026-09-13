"use client";

import { Save } from "lucide-react";

export default function Settings() {
  return (
    <div className="p-4 md:p-8 max-w-4xl mx-auto w-full space-y-8 animate-fade-up">
      <div>
        <h1 className="text-2xl md:text-3xl font-bold text-zinc-900 tracking-tight">Settings</h1>
        <p className="text-zinc-500 text-sm mt-1">Manage global application settings and API configurations.</p>
      </div>

      <div className="bg-white rounded-3xl p-6 md:p-8 border border-zinc-100 shadow-sm shadow-zinc-200/50 space-y-8">
        <div>
          <h2 className="text-lg font-bold text-zinc-900 mb-4 border-b border-zinc-100 pb-2">General</h2>
          <div className="space-y-4 max-w-lg">
            <div>
              <label className="block text-xs font-semibold text-zinc-600 uppercase tracking-wider mb-2 ml-1">App Name</label>
              <input 
                type="text" 
                defaultValue="Jasica AI"
                className="w-full bg-[#f5f5f7] border-0 rounded-2xl px-5 py-3 text-zinc-900 outline-none focus:ring-2 focus:ring-black transition-all text-sm font-medium"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-zinc-600 uppercase tracking-wider mb-2 ml-1">Support Email</label>
              <input 
                type="email" 
                defaultValue="support@jasica.com"
                className="w-full bg-[#f5f5f7] border-0 rounded-2xl px-5 py-3 text-zinc-900 outline-none focus:ring-2 focus:ring-black transition-all text-sm font-medium"
              />
            </div>
          </div>
        </div>

        <div>
          <h2 className="text-lg font-bold text-zinc-900 mb-4 border-b border-zinc-100 pb-2">SEO & Metadata</h2>
          <div className="space-y-4 max-w-lg">
            <div>
              <label className="block text-xs font-semibold text-zinc-600 uppercase tracking-wider mb-2 ml-1">Meta Description</label>
              <textarea 
                rows={3}
                defaultValue="Jasica AI - The next generation smart home controller."
                className="w-full bg-[#f5f5f7] border-0 rounded-2xl px-5 py-3 text-zinc-900 outline-none focus:ring-2 focus:ring-black transition-all text-sm font-medium resize-none"
              />
            </div>
          </div>
        </div>

        <div>
          <h2 className="text-lg font-bold text-zinc-900 mb-4 border-b border-zinc-100 pb-2">Security</h2>
          <div className="space-y-4 max-w-lg">
            <button className="text-sm font-semibold text-blue-600 bg-blue-50 px-4 py-2 rounded-xl hover:bg-blue-100 transition-colors">
              Change Admin Password
            </button>
          </div>
        </div>

        <div className="pt-4 flex justify-end">
          <button className="bg-black hover:bg-zinc-800 text-white px-6 py-3 rounded-xl text-sm font-semibold flex items-center gap-2 transition-all active:scale-95 shadow-lg shadow-black/10">
            <Save className="w-4 h-4" />
            Save Changes
          </button>
        </div>
      </div>
    </div>
  );
}

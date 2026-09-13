"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { Loader2 } from "lucide-react";

export default function AdminLogin() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const router = useRouter();

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      const res = await fetch("/api/admin/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password }),
      });

      const data = await res.json();

      if (res.ok && data.success) {
        router.push("/admin");
        router.refresh(); // Refresh to ensure middleware catches the new cookie
      } else {
        setError(data.message || "Invalid credentials");
      }
    } catch (err) {
      setError("An error occurred. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-[#f5f5f7] flex flex-col items-center justify-center p-4 antialiased">
      <div className="w-full max-w-[400px] bg-white rounded-3xl shadow-[0_20px_40px_rgba(0,0,0,0.08)] p-10 border border-zinc-100">
        <div className="text-center mb-10">
          <h1 className="text-3xl font-bold tracking-tight text-zinc-900 mb-2">
            Jasica Admin
          </h1>
          <p className="text-sm text-zinc-500">
            Sign in to manage your ecosystem.
          </p>
        </div>

        <form onSubmit={handleLogin} className="space-y-5">
          {error && (
            <div className="p-3 bg-red-50 text-red-600 text-sm rounded-xl text-center border border-red-100">
              {error}
            </div>
          )}

          <div className="space-y-4">
            <div>
              <label className="block text-xs font-semibold text-zinc-600 uppercase tracking-wider mb-2 ml-1">
                Admin ID
              </label>
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
                className="w-full bg-[#f5f5f7] border-0 rounded-2xl px-5 py-4 text-zinc-900 outline-none focus:ring-2 focus:ring-blue-500 transition-all text-sm font-medium"
                placeholder="name@jasica.com"
              />
            </div>
            <div>
              <label className="block text-xs font-semibold text-zinc-600 uppercase tracking-wider mb-2 ml-1">
                Password
              </label>
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
                className="w-full bg-[#f5f5f7] border-0 rounded-2xl px-5 py-4 text-zinc-900 outline-none focus:ring-2 focus:ring-blue-500 transition-all text-sm font-medium"
                placeholder="••••••••••"
              />
            </div>
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full bg-black text-white font-medium rounded-2xl py-4 mt-4 hover:bg-zinc-800 transition-all active:scale-[0.98] flex items-center justify-center gap-2 shadow-lg shadow-black/20"
          >
            {loading ? <Loader2 className="w-5 h-5 animate-spin" /> : "Sign In"}
          </button>
        </form>
      </div>
      
      <div className="mt-8 text-xs text-zinc-400">
        Secured by JasicaAI Core
      </div>
    </div>
  );
}

"use client";

import { useEffect, useState } from "react";
import { Activity, Users, Map, Globe, MousePointerClick, Loader2 } from "lucide-react";

interface Stats {
  totalViews: number;
  totalClicks: number;
  topPaths: { path: string; count: number }[];
  topClicks: { text: string; count: number }[];
}

export default function Analytics() {
  const [stats, setStats] = useState<Stats | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetch("/api/admin/analytics/data")
      .then(res => res.json())
      .then(data => {
        if (data.success) {
          setStats(data.stats);
        }
        setLoading(false);
      })
      .catch(() => setLoading(false));
  }, []);

  return (
    <div className="p-4 md:p-8 max-w-7xl mx-auto w-full space-y-8 animate-fade-up">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl md:text-3xl font-bold text-zinc-900 tracking-tight">Local Analytics</h1>
          <p className="text-zinc-500 text-sm mt-1">Self-hosted tracking data without external databases.</p>
        </div>
      </div>

      {loading ? (
        <div className="flex items-center justify-center h-64">
          <Loader2 className="w-8 h-8 animate-spin text-zinc-400" />
        </div>
      ) : (
        <>
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
            <div className="bg-white rounded-3xl p-6 md:p-8 border border-zinc-100 shadow-sm shadow-zinc-200/50 flex flex-col justify-center">
              <h3 className="text-zinc-500 text-sm font-medium mb-1">Total Page Views</h3>
              <p className="text-4xl font-bold text-zinc-900 tracking-tight">{stats?.totalViews || 0}</p>
            </div>
            <div className="bg-white rounded-3xl p-6 md:p-8 border border-zinc-100 shadow-sm shadow-zinc-200/50 flex flex-col justify-center">
              <h3 className="text-zinc-500 text-sm font-medium mb-1">Total Tracked Clicks</h3>
              <p className="text-4xl font-bold text-zinc-900 tracking-tight">{stats?.totalClicks || 0}</p>
            </div>
          </div>

          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <div className="bg-white rounded-3xl p-6 md:p-8 border border-zinc-100 shadow-sm shadow-zinc-200/50">
              <h2 className="text-lg font-bold text-zinc-900 mb-6 flex items-center gap-2">
                <Globe className="w-5 h-5 text-zinc-400" />
                Top Visited Pages
              </h2>
              <div className="space-y-4">
                {stats?.topPaths && stats.topPaths.length > 0 ? stats.topPaths.map((item, i) => (
                  <div key={i} className="flex justify-between items-center text-sm font-medium border-b border-zinc-50 pb-3 last:border-0">
                    <span className="text-zinc-700 font-mono text-xs bg-zinc-100 px-2 py-1 rounded-md">{item.path}</span>
                    <span className="text-zinc-500 font-bold">{item.count} views</span>
                  </div>
                )) : (
                  <p className="text-zinc-400 text-sm">No pageview data collected yet.</p>
                )}
              </div>
            </div>

            <div className="bg-white rounded-3xl p-6 md:p-8 border border-zinc-100 shadow-sm shadow-zinc-200/50">
              <h2 className="text-lg font-bold text-zinc-900 mb-6 flex items-center gap-2">
                <MousePointerClick className="w-5 h-5 text-zinc-400" />
                Most Clicked Elements
              </h2>
              <div className="space-y-4">
                {stats?.topClicks && stats.topClicks.length > 0 ? stats.topClicks.map((item, i) => (
                  <div key={i} className="flex justify-between items-center text-sm font-medium border-b border-zinc-50 pb-3 last:border-0">
                    <span className="text-zinc-700 truncate pr-4">{item.text || 'Unknown'}</span>
                    <span className="text-zinc-500 font-bold shrink-0">{item.count} clicks</span>
                  </div>
                )) : (
                  <p className="text-zinc-400 text-sm">No click data collected yet.</p>
                )}
              </div>
            </div>
          </div>
        </>
      )}
    </div>
  );
}

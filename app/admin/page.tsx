"use client";

import { Download, Users, Smartphone, TrendingUp, ArrowUpRight, Activity } from "lucide-react";

export default function AdminDashboard() {
  const stats = [
    { label: "Total APK Downloads", value: "142.5K", trend: "+12.5%", icon: Download, color: "text-blue-500", bg: "bg-blue-50" },
    { label: "Active Users", value: "24.8K", trend: "+5.2%", icon: Users, color: "text-emerald-500", bg: "bg-emerald-50" },
    { label: "Connected Devices", value: "86.2K", trend: "+18.1%", icon: Smartphone, color: "text-purple-500", bg: "bg-purple-50" },
    { label: "Conversion Rate", value: "8.4%", trend: "+1.2%", icon: TrendingUp, color: "text-orange-500", bg: "bg-orange-50" },
  ];

  const recentActivity = [
    { id: 1, action: "New APK Uploaded", details: "v1.2.14.09.2026-beta", time: "2 hours ago" },
    { id: 2, action: "User Milestone", details: "Passed 100k total downloads", time: "5 hours ago" },
    { id: 3, action: "Settings Updated", details: "Changed active download link", time: "1 day ago" },
    { id: 4, action: "New APK Uploaded", details: "v1.2.13.08.2026-alpha", time: "3 days ago" },
  ];

  return (
    <div className="p-4 md:p-8 max-w-7xl mx-auto w-full space-y-8 animate-fade-up">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl md:text-3xl font-bold text-zinc-900 tracking-tight">Overview</h1>
          <p className="text-zinc-500 text-sm mt-1">Welcome back, Bristi. Here's what's happening today.</p>
        </div>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 md:gap-6">
        {stats.map((stat, i) => {
          const Icon = stat.icon;
          return (
            <div key={i} className="bg-white rounded-3xl p-6 border border-zinc-100 shadow-sm shadow-zinc-200/50 hover:shadow-md transition-shadow">
              <div className="flex items-center justify-between mb-4">
                <div className={`w-12 h-12 rounded-2xl flex items-center justify-center ${stat.bg} ${stat.color}`}>
                  <Icon className="w-6 h-6" />
                </div>
                <div className="flex items-center gap-1 text-emerald-600 bg-emerald-50 px-2.5 py-1 rounded-lg text-xs font-semibold">
                  <ArrowUpRight className="w-3 h-3" />
                  {stat.trend}
                </div>
              </div>
              <h3 className="text-zinc-500 text-sm font-medium mb-1">{stat.label}</h3>
              <p className="text-3xl font-bold text-zinc-900 tracking-tight">{stat.value}</p>
            </div>
          );
        })}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Main Chart Area (Mock) */}
        <div className="lg:col-span-2 bg-white rounded-3xl p-6 md:p-8 border border-zinc-100 shadow-sm shadow-zinc-200/50">
          <div className="flex items-center justify-between mb-8">
            <h2 className="text-lg font-bold text-zinc-900">Download Traffic</h2>
            <select className="bg-zinc-50 border-none text-sm font-medium text-zinc-600 rounded-xl px-4 py-2 outline-none">
              <option>Last 7 Days</option>
              <option>Last 30 Days</option>
              <option>This Year</option>
            </select>
          </div>
          <div className="h-64 w-full flex items-end justify-between gap-2 px-2">
            {/* Mock bars */}
            {[40, 60, 45, 80, 55, 90, 70].map((height, i) => (
              <div key={i} className="w-full bg-blue-50 rounded-t-lg relative group">
                <div 
                  className="absolute bottom-0 w-full bg-blue-500 rounded-t-lg transition-all duration-500 ease-out group-hover:bg-blue-600"
                  style={{ height: `${height}%` }}
                />
              </div>
            ))}
          </div>
          <div className="flex justify-between mt-4 text-xs font-medium text-zinc-400 px-2">
            <span>Mon</span>
            <span>Tue</span>
            <span>Wed</span>
            <span>Thu</span>
            <span>Fri</span>
            <span>Sat</span>
            <span>Sun</span>
          </div>
        </div>

        {/* Recent Activity */}
        <div className="bg-white rounded-3xl p-6 md:p-8 border border-zinc-100 shadow-sm shadow-zinc-200/50">
          <h2 className="text-lg font-bold text-zinc-900 mb-6 flex items-center gap-2">
            <Activity className="w-5 h-5 text-zinc-400" />
            Recent Activity
          </h2>
          <div className="space-y-6">
            {recentActivity.map((activity) => (
              <div key={activity.id} className="flex gap-4 relative">
                <div className="w-2 h-2 rounded-full bg-blue-500 mt-2 shrink-0 relative z-10" />
                <div className="absolute left-[3px] top-4 bottom-[-24px] w-[2px] bg-zinc-100 last:hidden" />
                <div>
                  <p className="text-sm font-bold text-zinc-900">{activity.action}</p>
                  <p className="text-xs text-zinc-500 mt-0.5">{activity.details}</p>
                  <p className="text-[10px] text-zinc-400 mt-1 uppercase font-semibold tracking-wider">{activity.time}</p>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}

"use client";

import { useState, useEffect } from "react";
import { Package, Upload, Trash2, CheckCircle, Clock, FileWarning, Loader2 } from "lucide-react";

interface ApkFile {
  name: string;
  size: string;
  date: string;
  path: string;
  active: boolean;
}

export default function ApkManagement() {
  const [apks, setApks] = useState<ApkFile[]>([]);
  const [loading, setLoading] = useState(true);
  const [uploading, setUploading] = useState(false);

  useEffect(() => {
    fetchApks();
  }, []);

  const fetchApks = async () => {
    try {
      const res = await fetch("/api/admin/apks");
      const data = await res.json();
      if (data.success) {
        setApks(data.apks);
      }
    } catch (error) {
      console.error("Failed to fetch APKs");
    } finally {
      setLoading(false);
    }
  };

  const handleFileUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    if (!e.target.files || e.target.files.length === 0) return;
    
    const file = e.target.files[0];
    if (!file.name.endsWith(".apk")) {
      alert("Only .apk files are allowed");
      return;
    }

    setUploading(true);
    const formData = new FormData();
    formData.append("file", file);

    try {
      const res = await fetch("/api/admin/apks", {
        method: "POST",
        body: formData,
      });
      if (res.ok) {
        fetchApks();
      } else {
        alert("Upload failed");
      }
    } catch (error) {
      alert("Upload failed");
    } finally {
      setUploading(false);
      e.target.value = '';
    }
  };

  const handleDelete = async (name: string) => {
    if (!confirm(`Are you sure you want to delete ${name}?`)) return;

    try {
      const res = await fetch(`/api/admin/apks?name=${encodeURIComponent(name)}`, {
        method: "DELETE",
      });
      if (res.ok) {
        fetchApks();
      }
    } catch (error) {
      alert("Delete failed");
    }
  };

  return (
    <div className="p-4 md:p-8 max-w-7xl mx-auto w-full space-y-8 animate-fade-up">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl md:text-3xl font-bold text-zinc-900 tracking-tight">APK Management</h1>
          <p className="text-zinc-500 text-sm mt-1">Upload, manage, and set active release builds.</p>
        </div>
        
        <div>
          <label className="bg-black hover:bg-zinc-800 text-white px-5 py-2.5 rounded-xl text-sm font-semibold flex items-center gap-2 cursor-pointer transition-colors shadow-sm active:scale-95 inline-flex">
            {uploading ? <Loader2 className="w-4 h-4 animate-spin" /> : <Upload className="w-4 h-4" />}
            {uploading ? "Uploading..." : "Upload New APK"}
            <input 
              type="file" 
              accept=".apk" 
              className="hidden" 
              onChange={handleFileUpload}
              disabled={uploading}
            />
          </label>
        </div>
      </div>

      <div className="bg-white rounded-3xl border border-zinc-100 shadow-sm shadow-zinc-200/50 overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="border-b border-zinc-100 bg-zinc-50/50">
                <th className="px-6 py-4 text-xs font-semibold text-zinc-500 uppercase tracking-wider">File Name</th>
                <th className="px-6 py-4 text-xs font-semibold text-zinc-500 uppercase tracking-wider">Size</th>
                <th className="px-6 py-4 text-xs font-semibold text-zinc-500 uppercase tracking-wider">Date Uploaded</th>
                <th className="px-6 py-4 text-xs font-semibold text-zinc-500 uppercase tracking-wider">Status</th>
                <th className="px-6 py-4 text-xs font-semibold text-zinc-500 uppercase tracking-wider text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-zinc-50">
              {loading ? (
                <tr>
                  <td colSpan={5} className="px-6 py-12 text-center text-zinc-400">
                    <Loader2 className="w-6 h-6 animate-spin mx-auto mb-2" />
                    Loading APKs...
                  </td>
                </tr>
              ) : apks.length === 0 ? (
                <tr>
                  <td colSpan={5} className="px-6 py-12 text-center text-zinc-400">
                    <FileWarning className="w-8 h-8 mx-auto mb-2 opacity-50" />
                    <p>No APK files found.</p>
                  </td>
                </tr>
              ) : (
                apks.map((apk, idx) => (
                  <tr key={idx} className="hover:bg-zinc-50/50 transition-colors group">
                    <td className="px-6 py-4">
                      <div className="flex items-center gap-3">
                        <div className={`p-2 rounded-lg ${apk.active ? 'bg-emerald-50 text-emerald-600' : 'bg-zinc-100 text-zinc-500'}`}>
                          <Package className="w-5 h-5" />
                        </div>
                        <div>
                          <p className="font-semibold text-sm text-zinc-900">{apk.name}</p>
                          <a href={apk.path} className="text-[11px] text-blue-500 hover:underline">Download Link</a>
                        </div>
                      </div>
                    </td>
                    <td className="px-6 py-4 text-sm text-zinc-600">{apk.size}</td>
                    <td className="px-6 py-4">
                      <div className="flex items-center gap-1.5 text-sm text-zinc-600">
                        <Clock className="w-3.5 h-3.5 text-zinc-400" />
                        {new Date(apk.date).toLocaleDateString(undefined, { year: 'numeric', month: 'short', day: 'numeric' })}
                      </div>
                    </td>
                    <td className="px-6 py-4">
                      {apk.active ? (
                        <span className="inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-[11px] font-bold bg-emerald-50 text-emerald-600 border border-emerald-100">
                          <CheckCircle className="w-3 h-3" />
                          Active Build
                        </span>
                      ) : (
                        <span className="inline-flex items-center gap-1 px-2.5 py-1 rounded-full text-[11px] font-bold bg-zinc-100 text-zinc-500 border border-zinc-200">
                          Archived
                        </span>
                      )}
                    </td>
                    <td className="px-6 py-4 text-right">
                      <button 
                        onClick={() => handleDelete(apk.name)}
                        className="p-2 text-zinc-400 hover:text-red-600 hover:bg-red-50 rounded-lg transition-colors opacity-0 group-hover:opacity-100"
                        title="Delete APK"
                      >
                        <Trash2 className="w-4 h-4" />
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}

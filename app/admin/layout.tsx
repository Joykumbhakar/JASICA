"use client";

import { useState } from "react";
import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import { 
  LayoutDashboard, 
  Package, 
  Activity, 
  Settings, 
  LogOut,
  Menu,
  X,
  ChevronRight
} from "lucide-react";

export default function AdminLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const pathname = usePathname();
  const router = useRouter();
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
  const [isLoggingOut, setIsLoggingOut] = useState(false);

  // Don't show sidebar on login page
  if (pathname === "/admin/login") {
    return <>{children}</>;
  }

  const handleLogout = async () => {
    setIsLoggingOut(true);
    try {
      await fetch("/api/admin/logout", { method: "POST" });
      router.push("/admin/login");
      router.refresh();
    } catch (error) {
      console.error("Logout failed");
      setIsLoggingOut(false);
    }
  };

  const navItems = [
    { name: "Overview", href: "/admin", icon: LayoutDashboard },
    { name: "APK Management", href: "/admin/apks", icon: Package },
    { name: "Analytics", href: "/admin/analytics", icon: Activity },
    { name: "Settings", href: "/admin/settings", icon: Settings },
  ];

  return (
    <div className="min-h-screen bg-[#f5f5f7] flex font-sans antialiased">
      {/* Mobile Menu Overlay */}
      {isMobileMenuOpen && (
        <div 
          className="fixed inset-0 bg-black/20 backdrop-blur-sm z-40 lg:hidden"
          onClick={() => setIsMobileMenuOpen(false)}
        />
      )}

      {/* Sidebar */}
      <aside 
        className={`fixed inset-y-0 left-0 z-50 w-64 bg-white border-r border-zinc-200/60 transform transition-transform duration-300 ease-in-out lg:translate-x-0 lg:static lg:block ${
          isMobileMenuOpen ? "translate-x-0" : "-translate-x-full"
        }`}
      >
        <div className="h-full flex flex-col">
          {/* Logo Area */}
          <div className="h-16 flex items-center px-6 border-b border-zinc-100">
            <Link href="/" className="font-bold text-xl tracking-tight text-zinc-900 flex items-center gap-2">
              <div className="w-8 h-8 bg-black text-white rounded-lg flex items-center justify-center font-bold text-lg">
                J
              </div>
              Jasica Admin
            </Link>
          </div>

          {/* Navigation */}
          <nav className="flex-1 px-4 py-6 space-y-1 overflow-y-auto">
            {navItems.map((item) => {
              const isActive = pathname === item.href;
              const Icon = item.icon;
              return (
                <Link
                  key={item.name}
                  href={item.href}
                  onClick={() => setIsMobileMenuOpen(false)}
                  className={`flex items-center gap-3 px-3 py-2.5 rounded-xl transition-all duration-200 group ${
                    isActive 
                      ? "bg-black text-white shadow-md shadow-black/10" 
                      : "text-zinc-600 hover:bg-zinc-100 hover:text-zinc-900"
                  }`}
                >
                  <Icon className={`w-5 h-5 ${isActive ? "opacity-100" : "opacity-70 group-hover:opacity-100"}`} />
                  <span className="font-medium text-sm">{item.name}</span>
                  {isActive && <ChevronRight className="w-4 h-4 ml-auto opacity-50" />}
                </Link>
              );
            })}
          </nav>

          {/* User Profile / Logout */}
          <div className="p-4 border-t border-zinc-100">
            <div className="flex items-center gap-3 px-3 py-3 mb-2">
              <div className="w-10 h-10 rounded-full bg-blue-100 flex items-center justify-center text-blue-600 font-bold">
                BK
              </div>
              <div className="flex-1 min-w-0">
                <p className="text-sm font-bold text-zinc-900 truncate">Bristi K.</p>
                <p className="text-xs text-zinc-500 truncate">Admin</p>
              </div>
            </div>
            <button
              onClick={handleLogout}
              disabled={isLoggingOut}
              className="w-full flex items-center gap-2 px-3 py-2.5 text-sm font-medium text-red-600 hover:bg-red-50 rounded-xl transition-colors"
            >
              <LogOut className="w-4 h-4" />
              {isLoggingOut ? "Logging out..." : "Log out"}
            </button>
          </div>
        </div>
      </aside>

      {/* Main Content */}
      <main className="flex-1 flex flex-col min-w-0 h-screen overflow-hidden">
        {/* Mobile Header */}
        <header className="lg:hidden h-16 bg-white border-b border-zinc-200/60 flex items-center justify-between px-4 sticky top-0 z-30">
          <Link href="/admin" className="font-bold text-lg text-zinc-900">
            Jasica Admin
          </Link>
          <button
            onClick={() => setIsMobileMenuOpen(true)}
            className="p-2 -mr-2 text-zinc-600 hover:text-zinc-900 hover:bg-zinc-100 rounded-lg"
          >
            <Menu className="w-6 h-6" />
          </button>
        </header>

        {/* Page Content */}
        <div className="flex-1 overflow-y-auto">
          {children}
        </div>
      </main>
    </div>
  );
}

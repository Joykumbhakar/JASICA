"use client";

import { useEffect } from "react";
import { usePathname, useSearchParams } from "next/navigation";

export default function AnalyticsTracker() {
  const pathname = usePathname();
  const searchParams = useSearchParams();

  // Track page views
  useEffect(() => {
    if (typeof window !== "undefined") {
      let url = window.origin + pathname;
      if (searchParams && searchParams.toString()) {
        url = url + `?${searchParams.toString()}`;
      }

      fetch("/api/admin/analytics/track", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ 
          type: "pageview", 
          path: pathname,
          url: url,
          timestamp: new Date().toISOString()
        })
      }).catch(() => {});
    }
  }, [pathname, searchParams]);

  // Track all clicks globally
  useEffect(() => {
    const handleClick = (e: MouseEvent) => {
      // Traverse up to find the closest button or link
      const target = (e.target as HTMLElement).closest("button, a");
      
      if (target) {
        const text = target.textContent?.trim() || "Icon/Image Button";
        const id = target.id || "No ID";
        const tag = target.tagName.toLowerCase();
        let href = "";
        
        if (target instanceof HTMLAnchorElement) {
          href = target.href;
        }

        fetch("/api/admin/analytics/track", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            type: "click",
            element: tag,
            text: text.substring(0, 50), // Truncate long text
            id: id,
            href: href,
            path: window.location.pathname,
            timestamp: new Date().toISOString()
          })
        }).catch(() => {});
      }
    };

    document.addEventListener("click", handleClick);
    return () => document.removeEventListener("click", handleClick);
  }, []);

  return null;
}

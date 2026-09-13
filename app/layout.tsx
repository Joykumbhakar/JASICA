import type { Metadata } from "next";
import { Inter } from "next/font/google";
import "./globals.css";
import { Suspense } from "react";
import AnalyticsTracker from "./components/AnalyticsTracker";

const inter = Inter({
  subsets: ["latin"],
  variable: "--font-inter",
  weight: ["300", "400", "500", "600", "700", "800"],
});

export const metadata: Metadata = {
  title: {
    default: "Jasica AI | Next-Gen Smart Home Controller",
    template: "%s | Jasica AI"
  },
  description: "Jasica AI is an advanced offline smart home controller built for seamless hardware automation. Features voice control, gesture shortcuts, and Apple-inspired UI.",
  keywords: ["Jasica AI", "Smart Home Controller", "Home Automation", "Offline Smart Home", "Gesture Controls", "Bluetooth Controller", "Android App"],
  authors: [{ name: "Joy Kumbhakar" }],
  creator: "Joy Kumbhakar",
  publisher: "JASICA AI",
  openGraph: {
    type: "website",
    locale: "en_US",
    url: "https://jasicaai.vercel.app/",
    title: "Jasica AI \u2014 Smart Home Controller",
    description: "Control your world with Jasica AI. Seamless smart home automation with persistent Bluetooth, gesture shortcuts, and a beautiful UI.",
    siteName: "Jasica AI",
    images: [
      {
        url: "/jasica.png",
        width: 1200,
        height: 630,
        alt: "Jasica AI Logo",
      }
    ]
  },
  twitter: {
    card: "summary_large_image",
    title: "Jasica AI | Next-Gen Smart Home Controller",
    description: "Experience the next-gen offline smart home controller with gesture shortcuts and voice AI.",
    images: ["/jasica.png"],
  },
  icons: {
    icon: "/jasica.png",
    shortcut: "/jasica.png",
    apple: "/jasica.png",
  },
  themeColor: "#000000",
  viewport: "width=device-width, initial-scale=1, maximum-scale=1, user-scalable=0",
  manifest: "/manifest.json"
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en" className={`scroll-smooth ${inter.variable}`}>
      <body className="bg-[#f5f5f7] text-[#1d1d1f] antialiased">
        <Suspense fallback={null}>
          <AnalyticsTracker />
        </Suspense>
        {children}
      </body>
    </html>
  );
}

import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "JASICA AI - Next-Gen Voice & Hardware Controller",
  description: "Futuristic Voice Assistant, Web Serial Hardware Controller, and Custom Cloud Hosting Platform for Arduino UNO & ESP32 IoT.",
  icons: {
    icon: "/jasica.png",
  },
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en" className="dark">
      <body className="min-h-screen bg-[#06070d] text-white antialiased selection:bg-cyan-500 selection:text-black">
        {children}
      </body>
    </html>
  );
}

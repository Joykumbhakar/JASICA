import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "Jasica AI - Apple Style Experience",
  description: "Next-Gen AI Hardware & Neural Core Release. Experience Jasica AI.",
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
    <html lang="en" className="scroll-smooth">
      <head>
        <link rel="preconnect" href="https://fonts.googleapis.com" />
        <link rel="preconnect" href="https://fonts.gstatic.com" crossOrigin="anonymous" />
        <link
          href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&display=swap"
          rel="stylesheet"
        />
      </head>
      <body className="bg-[#f5f5f7] text-[#1d1d1f] antialiased">
        {children}
      </body>
    </html>
  );
}

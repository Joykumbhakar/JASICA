/** @type {import("tailwindcss").Config} */
module.exports = {
  content: [
    "./app/**/*.{js,ts,jsx,tsx,mdx}",
    "./pages/**/*.{js,ts,jsx,tsx,mdx}",
    "./components/**/*.{js,ts,jsx,tsx,mdx}",
  ],
  theme: {
    extend: {
      colors: {
        cyber: {
          dark: "#06070d",
          card: "rgba(18, 20, 36, 0.7)",
          cyan: "#00f2fe",
          blue: "#4facfe",
          purple: "#7928ca",
          magenta: "#ff0080",
          green: "#00ff87",
          orange: "#ff5e3a",
          red: "#ff3366",
        },
      },
      fontFamily: {
        orbitron: ["Orbitron", "sans-serif"],
        fira: ["Fira Code", "monospace"],
        sans: ["Plus Jakarta Sans", "sans-serif"],
      },
      boxShadow: {
        glow: "0 0 25px rgba(0, 242, 254, 0.45)",
        "glow-lg": "0 0 45px rgba(0, 242, 254, 0.65)",
        "glow-purple": "0 0 35px rgba(121, 40, 202, 0.5)",
        "glow-green": "0 0 25px rgba(0, 255, 135, 0.4)",
      },
      animation: {
        "spin-slow": "spin 20s linear infinite",
        "spin-reverse": "spin 14s linear infinite reverse",
        "pulse-glow": "pulse 3s cubic-bezier(0.4, 0, 0.6, 1) infinite",
        float: "floatGlow 8s ease-in-out infinite alternate",
      },
      keyframes: {
        floatGlow: {
          "0%": { transform: "translateY(0px) scale(1)" },
          "100%": { transform: "translateY(-30px) scale(1.15)" },
        },
      },
    },
  },
  plugins: [],
};

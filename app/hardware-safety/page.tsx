export default function Page() {
  return (
    <div className="prose prose-zinc max-w-none">
      <h1 className="text-3xl sm:text-4xl font-bold tracking-tight mb-6">Hardware Safety Guidelines</h1>
      <p className="text-lg text-zinc-600 leading-relaxed">When connecting Jasica AI to custom Arduino or ESP32 hardware via Bluetooth LE, ensure all relays and high-voltage components are properly isolated. Never leave experimental smart home setups unattended.</p>
      <div className="mt-10 pt-10 border-t border-zinc-100 text-sm text-zinc-400">
        Last updated: September 2026
      </div>
    </div>
  );
}

export default function Layout({ children }: { children: React.ReactNode }) {
  return (
    <div className="min-h-screen bg-[#f5f5f7] text-zinc-900 font-sans py-20 px-6 sm:px-12 md:px-24">
      <div className="max-w-3xl mx-auto bg-white rounded-3xl p-8 sm:p-12 shadow-sm border border-zinc-200">
        <a href="/" className="text-blue-600 font-medium mb-8 inline-block hover:underline">&larr; Back to Home</a>
        {children}
      </div>
    </div>
  );
}

import { NextResponse } from "next/server";

export async function GET() {
  return NextResponse.json({
    status: "healthy",
    system: "JASICA AI Cloud Bridge",
    version: "2.0.0",
    uptime: process.uptime(),
    timestamp: new Date().toISOString(),
    endpoints: {
      command: "/api/command",
      pins: "/api/pins",
      health: "/api/health"
    }
  });
}

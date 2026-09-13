import { NextResponse } from 'next/server';
import fs from 'fs';
import path from 'path';

export async function POST(request: Request) {
  try {
    const data = await request.json();
    
    // Ensure the data directory exists
    const dataDir = path.join(process.cwd(), 'data');
    if (!fs.existsSync(dataDir)) {
      fs.mkdirSync(dataDir, { recursive: true });
    }

    // Append to analytics.jsonl
    const filePath = path.join(dataDir, 'analytics.jsonl');
    
    // Add server-side timestamp and IP if needed (simplified here)
    const event = {
      ...data,
      serverTime: new Date().toISOString()
    };

    fs.appendFileSync(filePath, JSON.stringify(event) + '\n');

    return NextResponse.json({ success: true });
  } catch (error) {
    console.error("Analytics track error", error);
    return NextResponse.json({ success: false }, { status: 500 });
  }
}

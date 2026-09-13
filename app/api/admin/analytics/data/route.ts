import { NextResponse } from 'next/server';
import fs from 'fs';
import path from 'path';

export async function GET() {
  try {
    const filePath = path.join(process.cwd(), 'data', 'analytics.jsonl');
    
    if (!fs.existsSync(filePath)) {
      return NextResponse.json({ 
        success: true, 
        stats: { totalViews: 0, totalClicks: 0, topPaths: [], topClicks: [] }
      });
    }

    const fileContent = fs.readFileSync(filePath, 'utf-8');
    const lines = fileContent.split('\n').filter(line => line.trim() !== '');
    
    let totalViews = 0;
    let totalClicks = 0;
    const pathsCount: Record<string, number> = {};
    const clicksCount: Record<string, number> = {};

    lines.forEach(line => {
      try {
        const event = JSON.parse(line);
        if (event.type === 'pageview') {
          totalViews++;
          const path = event.path || '/';
          pathsCount[path] = (pathsCount[path] || 0) + 1;
        } else if (event.type === 'click') {
          totalClicks++;
          const clickTarget = event.text || event.id || 'Unknown Button';
          clicksCount[clickTarget] = (clicksCount[clickTarget] || 0) + 1;
        }
      } catch (e) {
        // Skip malformed lines
      }
    });

    const topPaths = Object.entries(pathsCount)
      .sort((a, b) => b[1] - a[1])
      .slice(0, 5)
      .map(([path, count]) => ({ path, count }));

    const topClicks = Object.entries(clicksCount)
      .sort((a, b) => b[1] - a[1])
      .slice(0, 5)
      .map(([text, count]) => ({ text, count }));

    return NextResponse.json({ 
      success: true, 
      stats: { totalViews, totalClicks, topPaths, topClicks }
    });

  } catch (error) {
    console.error("Analytics fetch error", error);
    return NextResponse.json({ success: false, message: "Failed to read data" }, { status: 500 });
  }
}

import { NextResponse } from 'next/server';
import fs from 'fs';
import path from 'path';

export async function GET() {
  try {
    const apksDir = path.join(process.cwd(), 'public', 'apks');
    
    if (!fs.existsSync(apksDir)) {
      return NextResponse.json({ success: true, apks: [] });
    }

    const files = fs.readdirSync(apksDir);
    
    const apks = files
      .filter(file => file.endsWith('.apk'))
      .map(file => {
        const filePath = path.join(apksDir, file);
        const stats = fs.statSync(filePath);
        return {
          name: file,
          size: (stats.size / (1024 * 1024)).toFixed(2) + ' MB',
          date: stats.mtime,
          path: `/apks/${file}`,
          active: file.includes('beta') || file.includes('1.2.14') // Mock logic for active APK
        };
      })
      .sort((a, b) => new Date(b.date).getTime() - new Date(a.date).getTime());

    return NextResponse.json({ success: true, apks });
  } catch (error) {
    return NextResponse.json({ success: false, message: 'Failed to read APKs' }, { status: 500 });
  }
}

export async function POST(request: Request) {
  try {
    const formData = await request.formData();
    const file = formData.get('file') as File;
    
    if (!file) {
      return NextResponse.json({ success: false, message: 'No file uploaded' }, { status: 400 });
    }

    const buffer = Buffer.from(await file.arrayBuffer());
    const apksDir = path.join(process.cwd(), 'public', 'apks');
    
    if (!fs.existsSync(apksDir)) {
      fs.mkdirSync(apksDir, { recursive: true });
    }

    const filePath = path.join(apksDir, file.name);
    fs.writeFileSync(filePath, buffer);

    return NextResponse.json({ success: true, message: 'APK uploaded successfully' });
  } catch (error) {
    return NextResponse.json({ success: false, message: 'Upload failed' }, { status: 500 });
  }
}

export async function DELETE(request: Request) {
  try {
    const { searchParams } = new URL(request.url);
    const fileName = searchParams.get('name');
    
    if (!fileName) {
      return NextResponse.json({ success: false, message: 'Filename required' }, { status: 400 });
    }

    const filePath = path.join(process.cwd(), 'public', 'apks', fileName);
    
    if (fs.existsSync(filePath)) {
      fs.unlinkSync(filePath);
      return NextResponse.json({ success: true, message: 'File deleted' });
    }
    
    return NextResponse.json({ success: false, message: 'File not found' }, { status: 404 });
  } catch (error) {
    return NextResponse.json({ success: false, message: 'Delete failed' }, { status: 500 });
  }
}

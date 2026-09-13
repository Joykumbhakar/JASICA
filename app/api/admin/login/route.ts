import { NextResponse } from 'next/server';

export async function POST(request: Request) {
  try {
    const { email, password } = await request.json();

    // Verify hardcoded credentials
    if (email === 'bristikumbhakar@jasica.com' && password === '9635493832') {
      const response = NextResponse.json({ success: true });
      
      // Set HTTP-only cookie
      response.cookies.set({
        name: 'admin_session',
        value: 'jasica-admin-authorized',
        httpOnly: true,
        path: '/',
        secure: process.env.NODE_ENV === 'production',
        maxAge: 60 * 60 * 24 * 7, // 1 week
      });
      
      return response;
    }

    return NextResponse.json(
      { success: false, message: 'Invalid credentials' },
      { status: 401 }
    );
  } catch (error) {
    return NextResponse.json(
      { success: false, message: 'Server error' },
      { status: 500 }
    );
  }
}

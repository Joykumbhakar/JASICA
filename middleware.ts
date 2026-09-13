import { NextResponse } from 'next/server';
import type { NextRequest } from 'next/server';

export function middleware(request: NextRequest) {
  const { pathname } = request.nextUrl;
  
  // Protect all /admin routes except /admin/login
  if (pathname.startsWith('/admin') && !pathname.startsWith('/admin/login')) {
    const session = request.cookies.get('admin_session');
    
    // Simple check: if cookie doesn't exist or isn't our expected value, redirect to login
    if (!session || session.value !== 'jasica-admin-authorized') {
      const loginUrl = new URL('/admin/login', request.url);
      return NextResponse.redirect(loginUrl);
    }
  }

  // If logged in and trying to access /admin/login, redirect to /admin
  if (pathname.startsWith('/admin/login')) {
    const session = request.cookies.get('admin_session');
    if (session && session.value === 'jasica-admin-authorized') {
      const adminUrl = new URL('/admin', request.url);
      return NextResponse.redirect(adminUrl);
    }
  }

  return NextResponse.next();
}

export const config = {
  matcher: ['/admin/:path*'],
};

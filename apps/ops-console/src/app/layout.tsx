import { Inter } from 'next/font/google';
import './globals.css';
import { Providers } from './providers';

const inter = Inter({
  subsets: ['latin'],
  variable: '--font-inter',
  display: 'swap',
});

export const metadata = {
  title: 'Jumarkot — Ops Console',
  description: 'Risk, Fraud, and Compliance Intelligence Platform — Operations Dashboard',
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en" suppressHydrationWarning>
      <body className={`${inter.variable} enterprise-body`}>
        <Providers>{children}</Providers>
      </body>
    </html>
  );
}

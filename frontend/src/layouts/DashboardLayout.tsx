import { Outlet } from 'react-router-dom'
import { Navbar } from '@/components/layout/Navbar'

export function DashboardLayout() {
  return (
    <div className="min-h-screen bg-background">
      <Navbar />
      <main className="container py-8 2xl:max-w-7xl px-4 mx-auto">
        <Outlet />
      </main>
    </div>
  )
}

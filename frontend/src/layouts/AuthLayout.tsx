import { Outlet } from 'react-router-dom'
import { Wallet } from 'lucide-react'
import { ThemeToggle } from '@/components/common/ThemeToggle'

export function AuthLayout() {
  return (
    <div className="relative min-h-screen bg-gradient-to-br from-background via-background to-muted">
      <div className="absolute right-4 top-4">
        <ThemeToggle />
      </div>
      <div className="flex min-h-screen items-center justify-center p-4">
        <div className="w-full max-w-md">
          <div className="mb-8 flex flex-col items-center gap-2">
            <div className="flex h-12 w-12 items-center justify-center rounded-2xl bg-primary text-primary-foreground shadow-md">
              <Wallet className="h-6 w-6" />
            </div>
            <h1 className="text-2xl font-bold tracking-tight">FinPay</h1>
            <p className="text-sm text-muted-foreground">Payment Gateway Integration Template</p>
          </div>
          <Outlet />
        </div>
      </div>
    </div>
  )
}

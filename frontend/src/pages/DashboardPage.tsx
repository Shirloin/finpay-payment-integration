import { BalanceCard } from '@/components/payment/BalanceCard'
import { TopUpForm } from '@/components/payment/TopUpForm'
import { TransactionHistory } from '@/components/payment/TransactionHistory'
import { useAuthStore } from '@/store/auth.store'

export default function DashboardPage() {
  const user = useAuthStore((s) => s.currentUser)

  return (
    <div className="space-y-6 container mx-auto">
      <div>
        <h1 className="text-2xl font-bold tracking-tight">Welcome back, {user?.username}</h1>
        <p className="text-sm text-muted-foreground">
          Manage your balance and top up your wallet.
        </p>
      </div>

      <div className="grid gap-6 lg:grid-cols-2">
        <BalanceCard />
        <TopUpForm />
      </div>

      <TransactionHistory />
    </div>
  )
}

import { Wallet } from 'lucide-react'
import { Card, CardContent } from '@/components/ui/card'
import { Skeleton } from '@/components/ui/skeleton'
import { useBalance } from '@/hooks/useBalance'
import { formatCurrency } from '@/utils/format'

export function BalanceCard() {
  const { data, isLoading } = useBalance()

  return (
    <Card className="overflow-hidden border-none bg-gradient-to-br from-primary to-primary/70 text-primary-foreground shadow-lg">
      <CardContent className="p-6">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2 text-sm opacity-80">
            <Wallet className="h-4 w-4" />
            Current Balance
          </div>
        </div>
        <div className="mt-4">
          {isLoading ? (
            <Skeleton className="h-10 w-48 bg-white/20" />
          ) : (
            <div className="text-4xl font-bold tracking-tight">{formatCurrency(data ?? 0)}</div>
          )}
          <p className="mt-2 text-xs opacity-70">Available for spending or top up</p>
        </div>
      </CardContent>
    </Card>
  )
}

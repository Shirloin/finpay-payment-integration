import { Receipt } from 'lucide-react'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Skeleton } from '@/components/ui/skeleton'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table'
import { EmptyState } from '@/components/common/EmptyState'
import { useTransactions } from '@/hooks/useTransactions'
import { formatCurrency, formatDate } from '@/utils/format'
import { ContinuePaymentButton } from './ContinuePaymentButton'
import { TransactionStatusBadge } from './TransactionStatusBadge'

export function TransactionHistory() {
  const { data, isLoading } = useTransactions()

  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-base">Transaction History</CardTitle>
        <CardDescription>Your top up activity, newest first.</CardDescription>
      </CardHeader>
      <CardContent>
        {isLoading ? (
          <div className="space-y-3">
            {Array.from({ length: 3 }).map((_, i) => (
              <Skeleton key={i} className="h-12 w-full" />
            ))}
          </div>
        ) : !data || data.length === 0 ? (
          <EmptyState
            icon={<Receipt className="h-6 w-6" />}
            title="No transactions yet"
            description="Your top up history will appear here once you make a payment."
          />
        ) : (
          <>
            <div className="hidden md:block">
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Reference</TableHead>
                    <TableHead>Amount</TableHead>
                    <TableHead>Status</TableHead>
                    <TableHead>Date</TableHead>
                    <TableHead className="text-right">Action</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {data.map((tx) => (
                    <TableRow key={tx.id}>
                      <TableCell className="font-mono text-xs">{tx.orderId}</TableCell>
                      <TableCell className="font-medium">{formatCurrency(tx.amount)}</TableCell>
                      <TableCell>
                        <TransactionStatusBadge status={tx.status} />
                      </TableCell>
                      <TableCell className="text-muted-foreground">{formatDate(tx.createdAt)}</TableCell>
                      <TableCell className="text-right">
                        {tx.status === 'PENDING' && (
                          <ContinuePaymentButton orderId={tx.orderId} redirectUrl={tx.redirectUrl} />
                        )}
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </div>

            <div className="space-y-3 md:hidden">
              {data.map((tx) => (
                <div key={tx.id} className="rounded-lg border p-4">
                  <div className="flex items-start justify-between">
                    <div>
                      <p className="font-mono text-xs text-muted-foreground">{tx.orderId}</p>
                      <p className="mt-1 text-lg font-semibold">{formatCurrency(tx.amount)}</p>
                    </div>
                    <TransactionStatusBadge status={tx.status} />
                  </div>
                  <p className="mt-2 text-xs text-muted-foreground">{formatDate(tx.createdAt)}</p>
                  {tx.status === 'PENDING' && (
                    <div className="mt-3">
                      <ContinuePaymentButton orderId={tx.orderId} redirectUrl={tx.redirectUrl} />
                    </div>
                  )}
                </div>
              ))}
            </div>
          </>
        )}
      </CardContent>
    </Card>
  )
}

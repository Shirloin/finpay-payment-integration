import { CheckCircle2, XCircle } from 'lucide-react'
import { Button } from '@/components/ui/button'
import {
  Dialog,
  DialogContent,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog'
import { Separator } from '@/components/ui/separator'
import { formatCurrency } from '@/utils/format'
import type { Transaction } from '@/types'

interface Props {
  open: boolean
  onOpenChange: (open: boolean) => void
  transaction: Transaction | null
  balance: number
  onRetry?: () => void
}

export function PaymentResultModal({ open, onOpenChange, transaction, balance, onRetry }: Props) {
  if (!transaction) return null
  const isSuccess = transaction.status === 'SUCCESS'

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <div className="flex flex-col items-center gap-3 pt-2 text-center">
            {isSuccess ? (
              <div className="flex h-14 w-14 items-center justify-center rounded-full bg-success/10 text-success">
                <CheckCircle2 className="h-8 w-8" />
              </div>
            ) : (
              <div className="flex h-14 w-14 items-center justify-center rounded-full bg-destructive/10 text-destructive">
                <XCircle className="h-8 w-8" />
              </div>
            )}
            <DialogTitle className="text-xl">
              {isSuccess ? 'Payment Successful' : 'Payment Failed'}
            </DialogTitle>
          </div>
        </DialogHeader>

        <div className="rounded-lg bg-muted/50 p-4 text-sm">
          <Row label="Reference Number" value={transaction.referenceNumber} />
          <Row label="Amount" value={formatCurrency(transaction.amount)} />
          {isSuccess ? (
            <>
              <Separator className="my-3" />
              <Row label="Updated Balance" value={formatCurrency(balance)} bold />
            </>
          ) : (
            <>
              <Separator className="my-3" />
              <Row label="Reason" value={transaction.failureReason ?? 'Unknown error'} />
            </>
          )}
        </div>

        <DialogFooter>
          {!isSuccess && onRetry && (
            <Button
              variant="outline"
              onClick={() => {
                onOpenChange(false)
                onRetry()
              }}
            >
              Try Again
            </Button>
          )}
          <Button onClick={() => onOpenChange(false)}>Close</Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  )
}

function Row({ label, value, bold }: { label: string; value: string; bold?: boolean }) {
  return (
    <div className="flex items-center justify-between py-1">
      <span className="text-muted-foreground">{label}</span>
      <span className={bold ? 'font-semibold' : 'font-medium'}>{value}</span>
    </div>
  )
}

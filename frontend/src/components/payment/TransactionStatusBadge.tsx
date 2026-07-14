import { Badge } from '@/components/ui/badge'
import type { PaymentStatus } from '@/types'

export function TransactionStatusBadge({ status }: { status: PaymentStatus }) {
  if (status === 'SUCCESS') return <Badge variant="success">Success</Badge>
  return <Badge variant="destructive">Failed</Badge>
}

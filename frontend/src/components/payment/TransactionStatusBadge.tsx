import { Badge } from '@/components/ui/badge'
import type { OrderStatusValue } from '@/types'

export function TransactionStatusBadge({ status }: { status: OrderStatusValue }) {
  if (status === 'PAID') return <Badge variant="success">Paid</Badge>
  if (status === 'PENDING') return <Badge variant="secondary">Pending</Badge>
  return <Badge variant="destructive">Failed</Badge>
}

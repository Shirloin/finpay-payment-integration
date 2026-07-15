import { ExternalLink } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { openPaymentInNewTab } from '@/utils/payment'

type Props = {
  orderId: string
  redirectUrl: string | null | undefined
  className?: string
}

export function ContinuePaymentButton({ orderId, redirectUrl, className }: Props) {
  if (!redirectUrl) {
    return null
  }

  return (
    <Button
      type="button"
      size="sm"
      variant="outline"
      className={className}
      onClick={() => openPaymentInNewTab(orderId, redirectUrl)}
    >
      <ExternalLink className="h-3.5 w-3.5" />
      Continue Payment
    </Button>
  )
}

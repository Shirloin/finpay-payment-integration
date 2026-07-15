import { CheckCircle2 } from 'lucide-react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Spinner } from '@/components/ui/spinner'
import { ContinuePaymentButton } from '@/components/payment/ContinuePaymentButton'
import { useOrderStatus } from '@/hooks/useOrderStatus'
import { formatCurrency } from '@/utils/format'
import { PENDING_ORDER_KEY } from '@/utils/payment'

export default function PaymentSuccessPage() {
  const navigate = useNavigate()
  const [searchParams] = useSearchParams()
  const orderId = searchParams.get('orderId') ?? sessionStorage.getItem(PENDING_ORDER_KEY)
  const { data, isLoading, isError } = useOrderStatus(orderId)

  if (isLoading) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <Spinner className="h-6 w-6" />
      </div>
    )
  }

  if (isError || !data) {
    return (
      <div className="flex min-h-screen items-center justify-center p-4">
        <Card className="w-full max-w-md">
          <CardHeader>
            <CardTitle>Payment Result Unavailable</CardTitle>
            <CardDescription>We could not load your payment status.</CardDescription>
          </CardHeader>
          <CardContent>
            <Button className="w-full" onClick={() => navigate('/dashboard')}>
              Back to Dashboard
            </Button>
          </CardContent>
        </Card>
      </div>
    )
  }

  const isPaid = data.status === 'PAID'
  const isPending = data.status === 'PENDING'

  if (isPaid) {
    sessionStorage.removeItem(PENDING_ORDER_KEY)
  }

  return (
    <div className="flex min-h-screen items-center justify-center p-4">
      <Card className="w-full max-w-md">
        <CardHeader className="text-center">
          <div className="mx-auto mb-3 flex h-14 w-14 items-center justify-center rounded-full bg-success/10 text-success">
            {isPaid ? <CheckCircle2 className="h-8 w-8" /> : <Spinner className="h-8 w-8" />}
          </div>
          <CardTitle>{isPaid ? 'Payment Successful' : 'Payment Pending'}</CardTitle>
          <CardDescription>
            {isPaid
              ? 'Your wallet balance has been updated.'
              : 'Your payment is not complete yet. Continue on Finpay to finish it.'}
          </CardDescription>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="rounded-lg bg-muted/50 p-4 text-sm">
            <Row label="Order ID" value={data.orderId} />
            <Row label="Amount" value={formatCurrency(data.amount)} />
            {data.paymentMethod && <Row label="Payment Method" value={data.paymentMethod} />}
            {isPaid && <Row label="Updated Balance" value={formatCurrency(data.balance)} bold />}
          </div>
          {isPending && data.redirectUrl ? (
            <ContinuePaymentButton
              orderId={data.orderId}
              redirectUrl={data.redirectUrl}
              className="h-10 w-full"
            />
          ) : null}
          <Button
            className="w-full"
            variant={isPending ? 'outline' : 'default'}
            onClick={() => navigate('/dashboard')}
          >
            Back to Dashboard
          </Button>
        </CardContent>
      </Card>
    </div>
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

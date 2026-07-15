import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { toast } from 'sonner'
import { CreditCard } from 'lucide-react'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Spinner } from '@/components/ui/spinner'
import { useTopUpMutation } from '@/hooks/useTopUp'
import { formatCurrency } from '@/utils/format'
import { continueToPayment } from '@/utils/payment'

const MIN = 10_000
const MAX = 10_000_000

const schema = z.object({
  amount: z
    .number({ error: 'Amount is required' })
    .int('Amount must be a whole number')
    .min(MIN, `Minimum top up is ${formatCurrency(MIN)}`)
    .max(MAX, `Maximum top up is ${formatCurrency(MAX)}`),
})

type FormValues = z.infer<typeof schema>

const PRESETS = [50_000, 100_000, 250_000, 500_000]

export function TopUpForm() {
  const {
    register,
    handleSubmit,
    setValue,
    watch,
    formState: { errors },
  } = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: { amount: 0 },
  })

  const amount = watch('amount')
  const topUp = useTopUpMutation()

  const submit = (values: FormValues) => {
    topUp.mutate(
      { amount: values.amount },
      {
        onSuccess: (data) => {
          continueToPayment(data.orderId, data.redirectUrl)
        },
        onError: (err) => toast.error(err.message),
      }
    )
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2 text-base">
          <CreditCard className="h-4 w-4" />
          Top Up Balance
        </CardTitle>
        <CardDescription>Pay with Finpay Payment Code.</CardDescription>
      </CardHeader>
      <CardContent>
        <form onSubmit={handleSubmit(submit)} className="space-y-4">
          <div className="space-y-2">
            <Label htmlFor="amount">Amount</Label>
            <Input
              id="amount"
              type="number"
              min={MIN}
              max={MAX}
              placeholder="10000"
              {...register('amount', { valueAsNumber: true })}
            />
            {errors.amount && <p className="text-xs text-destructive">{errors.amount.message}</p>}
            <p className="text-xs text-muted-foreground">
              Min {formatCurrency(MIN)} · Max {formatCurrency(MAX)}
            </p>
          </div>

          <div className="grid grid-cols-2 gap-2 sm:grid-cols-4">
            {PRESETS.map((v) => (
              <Button
                key={v}
                type="button"
                variant={amount === v ? 'default' : 'outline'}
                size="sm"
                onClick={() => setValue('amount', v, { shouldValidate: true })}
              >
                {formatCurrency(v)}
              </Button>
            ))}
          </div>

          <Button type="submit" className="w-full" disabled={topUp.isPending}>
            {topUp.isPending && <Spinner />}
            Top Up
          </Button>
        </form>
      </CardContent>
    </Card>
  )
}

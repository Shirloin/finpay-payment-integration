import { useQuery } from '@tanstack/react-query'
import { getTransactionsRequest } from '@/api/topup'

export function useTransactions() {
  return useQuery({
    queryKey: ['transactions'],
    queryFn: getTransactionsRequest,
  })
}

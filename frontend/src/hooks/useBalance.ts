import { useQuery } from '@tanstack/react-query'
import { getBalanceRequest } from '@/api/topup'

export function useBalance() {
  return useQuery({
    queryKey: ['balance'],
    queryFn: getBalanceRequest,
  })
}

import { useQuery } from '@tanstack/react-query'
import { getTransactionsRequest } from '@/api/topup'
import { useAuthStore } from '@/store/auth.store'

export function useTransactions() {
  const userId = useAuthStore((state) => state.currentUser?.id)
  return useQuery({
    queryKey: ['transactions', userId],
    queryFn: () => getTransactionsRequest(userId!),
    enabled: !!userId,
  })
}

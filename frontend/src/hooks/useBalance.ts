import { useQuery } from '@tanstack/react-query'
import { getBalanceRequest } from '@/api/topup'
import { useAuthStore } from '@/store/auth.store'

export function useBalance() {
  const userId = useAuthStore((state) => state.currentUser?.id)
  return useQuery({
    queryKey: ['balance', userId],
    queryFn: () => getBalanceRequest(userId!),
    enabled: !!userId,
  })
}

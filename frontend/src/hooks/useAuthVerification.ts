import { useQuery } from '@tanstack/react-query'
import { verifyTokenRequest } from '@/api/auth'
import { useAuthStore } from '@/store/auth.store'

export function useAuthVerification() {
  const token = useAuthStore((state) => state.token)

  return useQuery({
    queryKey: ['auth', 'verify', token],
    queryFn: verifyTokenRequest,
    enabled: Boolean(token),
    retry: false,
    staleTime: 30_000,
  })
}

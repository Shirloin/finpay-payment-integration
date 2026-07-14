import { useMutation, useQueryClient } from '@tanstack/react-query'
import { topUpRequest } from '@/api/topup'
import { useAuthStore } from '@/store/auth.store'
import type { TopUpRequest, TopUpResponse } from '@/types'

export function useTopUpMutation() {
  const qc = useQueryClient()
  const userId = useAuthStore((s) => s.currentUser?.id)

  return useMutation<TopUpResponse, Error, TopUpRequest>({
    mutationFn: (payload) => {
      if (!userId) throw new Error('Not authenticated')
      return topUpRequest(userId, payload)
    },
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['balance', userId] })
      qc.invalidateQueries({ queryKey: ['transactions', userId] })
    },
  })
}

import { useMutation, useQueryClient } from '@tanstack/react-query'
import { topUpRequest } from '@/api/topup'
import type { TopUpRequest, TopUpResponse } from '@/types'

export function useTopUpMutation() {
  const qc = useQueryClient()

  return useMutation<TopUpResponse, Error, TopUpRequest>({
    mutationFn: topUpRequest,
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['balance'] })
      qc.invalidateQueries({ queryKey: ['transactions'] })
    },
  })
}

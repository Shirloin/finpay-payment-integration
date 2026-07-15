import { useQuery } from '@tanstack/react-query'
import { getOrderStatusRequest } from '@/api/topup'

export function useOrderStatus(orderId: string | null) {
  return useQuery({
    queryKey: ['order-status', orderId],
    queryFn: () => getOrderStatusRequest(orderId!),
    enabled: Boolean(orderId),
    refetchInterval: (query) => {
      const status = query.state.data?.status
      return status === 'PENDING' ? 3000 : false
    },
  })
}

import axios from 'axios'
import { api } from '@/api/axios'
import type { ApiResponse, OrderStatus, TopUpRequest, TopUpResponse, Transaction } from '@/types'

function toApiError(error: unknown, fallbackMessage: string): Error {
  if (!axios.isAxiosError<ApiResponse<unknown>>(error)) {
    return error instanceof Error ? error : new Error(fallbackMessage)
  }

  const responseMessage = error.response?.data?.message?.trim()
  if (responseMessage) return new Error(responseMessage)
  if (!error.response) {
    return new Error('Unable to reach the server. Check your connection and try again.')
  }

  return new Error(fallbackMessage)
}

export async function getBalanceRequest(): Promise<number> {
  try {
    const response = await api.get<ApiResponse<number>>('/balance')
    return response.data.data
  } catch (error) {
    throw toApiError(error, 'Unable to load balance')
  }
}

export async function getTransactionsRequest(): Promise<Transaction[]> {
  try {
    const response = await api.get<ApiResponse<Transaction[]>>('/transactions')
    return response.data.data
  } catch (error) {
    throw toApiError(error, 'Unable to load transactions')
  }
}

export async function topUpRequest(payload: TopUpRequest): Promise<TopUpResponse> {
  try {
    const response = await api.post<ApiResponse<TopUpResponse>>('/topup', payload)
    return response.data.data
  } catch (error) {
    throw toApiError(error, 'Unable to initiate top up')
  }
}

export async function getOrderStatusRequest(orderId: string): Promise<OrderStatus> {
  try {
    const response = await api.get<ApiResponse<OrderStatus>>(`/orders/${orderId}`)
    return response.data.data
  } catch (error) {
    throw toApiError(error, 'Unable to load payment status')
  }
}

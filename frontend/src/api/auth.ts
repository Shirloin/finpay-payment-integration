import axios from 'axios'
import { api } from '@/api/axios'
import type { ApiResponse, AuthCredentials, AuthResponse, User } from '@/types'

function getBackendErrorMessage(error: unknown): string | null {
  if (typeof error === 'string' && error.trim()) return error

  if (error && typeof error === 'object' && 'message' in error) {
    const message = error.message
    if (typeof message === 'string' && message.trim()) return message
  }

  return null
}

function toAuthError(error: unknown, fallbackMessage: string): Error {
  if (!axios.isAxiosError<ApiResponse<unknown>>(error)) {
    return error instanceof Error ? error : new Error(fallbackMessage)
  }

  const responseBody = error.response?.data
  const responseMessage =
    responseBody?.message?.trim() || getBackendErrorMessage(responseBody?.error)

  if (responseMessage) return new Error(responseMessage)
  if (error.code === 'ECONNABORTED' || error.code === 'ETIMEDOUT') {
    return new Error('The request timed out. Please try again.')
  }
  if (!error.response) {
    return new Error('Unable to reach the server. Check your connection and try again.')
  }

  return new Error(fallbackMessage)
}

export async function registerRequest(payload: AuthCredentials): Promise<User> {
  try {
    const response = await api.post<ApiResponse<User>>('/auth/register', payload)
    return response.data.data
  } catch (error) {
    throw toAuthError(error, 'Unable to create account')
  }
}

export async function loginRequest(payload: AuthCredentials): Promise<AuthResponse> {
  try {
    const response = await api.post<ApiResponse<AuthResponse>>('/auth/login', payload)
    return response.data.data
  } catch (error) {
    throw toAuthError(error, 'Unable to sign in')
  }
}

export async function verifyTokenRequest(): Promise<User> {
  try {
    const response = await api.get<ApiResponse<User>>('/auth/verify')
    return response.data.data
  } catch (error) {
    throw toAuthError(error, 'Your session is no longer valid')
  }
}

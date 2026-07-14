import axios from 'axios'
import { api } from '@/api/axios'
import { delay } from '@/utils/payment'
import { useAuthStore } from '@/store/auth.store'
import type { ApiResponse, AuthCredentials, User } from '@/types'

export async function registerRequest(payload: AuthCredentials): Promise<User> {
  try {
    const response = await api.post<ApiResponse<User>>('/auth/register', payload)
    return response.data.data
  } catch (error) {
    if (axios.isAxiosError<ApiResponse<never>>(error)) {
      throw new Error(error.response?.data?.message ?? 'Unable to create account')
    }

    throw error
  }
}

// Login remains simulated until the backend login endpoint is implemented.
export async function loginRequest(payload: AuthCredentials): Promise<User> {
  await delay(800)
  const found = useAuthStore.getState().findUser(payload.username)
  if (!found || found.password !== payload.password) {
    throw new Error('Invalid username or password')
  }
  const { password: _pw, ...safe } = found
  return safe
}

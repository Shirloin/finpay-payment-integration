import { useMutation } from '@tanstack/react-query'
import { loginRequest, registerRequest } from '@/api/auth'
import { useAuthStore } from '@/store/auth.store'
import type { AuthCredentials, AuthResponse } from '@/types'

export function useLoginMutation() {
  const setSession = useAuthStore((s) => s.setSession)
  return useMutation<AuthResponse, Error, AuthCredentials>({
    mutationFn: loginRequest,
    onSuccess: setSession,
  })
}

export function useRegisterMutation() {
  const setSession = useAuthStore((s) => s.setSession)
  return useMutation<AuthResponse, Error, AuthCredentials>({
    mutationFn: async (credentials) => {
      await registerRequest(credentials)
      return loginRequest(credentials)
    },
    onSuccess: setSession,
  })
}

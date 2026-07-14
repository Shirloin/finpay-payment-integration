import { useMutation } from '@tanstack/react-query'
import { loginRequest, registerRequest } from '@/api/auth'
import { useAuthStore } from '@/store/auth.store'
import type { AuthCredentials, User } from '@/types'

export function useLoginMutation() {
  const setCurrentUser = useAuthStore((s) => s.setCurrentUser)
  return useMutation<User, Error, AuthCredentials>({
    mutationFn: loginRequest,
    onSuccess: (user) => setCurrentUser(user),
  })
}

export function useRegisterMutation() {
  const setCurrentUser = useAuthStore((s) => s.setCurrentUser)
  return useMutation<User, Error, AuthCredentials>({
    mutationFn: registerRequest,
    onSuccess: (user) => setCurrentUser(user),
  })
}

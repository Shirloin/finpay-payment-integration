import { create } from 'zustand'
import { persist } from 'zustand/middleware'
import { AUTH_STORAGE_KEY } from '@/constants/auth.constants'
import type { AuthResponse, User } from '@/types'

type AuthState = {
  token: string | null
  currentUser: User | null
  setSession: (session: AuthResponse) => void
  clearSession: () => void
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      token: null,
      currentUser: null,
      setSession: ({ accessToken, user }) => set({ token: accessToken, currentUser: user }),
      clearSession: () => set({ token: null, currentUser: null }),
    }),
    { name: AUTH_STORAGE_KEY }
  )
)

export function selectIsAuthenticated(state: AuthState): boolean {
  return Boolean(state.token)
}

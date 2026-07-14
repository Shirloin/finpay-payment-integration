import { create } from 'zustand'
import { persist } from 'zustand/middleware'
import type { StoredUser, User } from '@/types'

interface AuthState {
  users: StoredUser[]
  currentUser: User | null
  registerUser: (user: StoredUser) => void
  setCurrentUser: (user: User | null) => void
  findUser: (username: string) => StoredUser | undefined
  logout: () => void
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      users: [],
      currentUser: null,
      registerUser: (user) => set((state) => ({ users: [...state.users, user] })),
      setCurrentUser: (user) => set({ currentUser: user }),
      findUser: (username) => get().users.find((u) => u.username.toLowerCase() === username.toLowerCase()),
      logout: () => set({ currentUser: null }),
    }),
    { name: 'finpay-auth' }
  )
)

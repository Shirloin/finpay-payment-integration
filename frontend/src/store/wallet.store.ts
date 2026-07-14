import { create } from 'zustand'
import { persist } from 'zustand/middleware'

interface WalletState {
  balances: Record<string, number>
  getBalance: (userId: string) => number
  setBalance: (userId: string, balance: number) => void
  increaseBalance: (userId: string, amount: number) => number
}

export const useWalletStore = create<WalletState>()(
  persist(
    (set, get) => ({
      balances: {},
      getBalance: (userId) => get().balances[userId] ?? 0,
      setBalance: (userId, balance) =>
        set((state) => ({ balances: { ...state.balances, [userId]: balance } })),
      increaseBalance: (userId, amount) => {
        const next = (get().balances[userId] ?? 0) + amount
        set((state) => ({ balances: { ...state.balances, [userId]: next } }))
        return next
      },
    }),
    { name: 'finpay-wallet' }
  )
)

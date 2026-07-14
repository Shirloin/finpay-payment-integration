import { create } from 'zustand'
import { persist } from 'zustand/middleware'
import type { Transaction } from '@/types'

interface TransactionState {
  transactions: Transaction[]
  sequence: number
  addTransaction: (tx: Transaction) => void
  nextSequence: () => number
  listByUser: (userId: string) => Transaction[]
}

export const useTransactionStore = create<TransactionState>()(
  persist(
    (set, get) => ({
      transactions: [],
      sequence: 0,
      addTransaction: (tx) => set((state) => ({ transactions: [tx, ...state.transactions] })),
      nextSequence: () => {
        const next = get().sequence + 1
        set({ sequence: next })
        return next
      },
      listByUser: (userId) =>
        get()
          .transactions.filter((t) => t.userId === userId)
          .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()),
    }),
    { name: 'finpay-transactions' }
  )
)

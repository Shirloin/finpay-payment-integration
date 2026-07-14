import { delay, generateReferenceNumber, randomPaymentResult } from '@/utils/payment'
import { useTransactionStore } from '@/store/transaction.store'
import { useWalletStore } from '@/store/wallet.store'
import type { TopUpRequest, TopUpResponse, Transaction } from '@/types'

// GET /balance
export async function getBalanceRequest(userId: string): Promise<number> {
  await delay(300)
  return useWalletStore.getState().getBalance(userId)
}

// GET /transactions
export async function getTransactionsRequest(userId: string): Promise<Transaction[]> {
  await delay(300)
  return useTransactionStore.getState().listByUser(userId)
}

// POST /topup
export async function topUpRequest(userId: string, payload: TopUpRequest): Promise<TopUpResponse> {
  await delay(1500)

  const txStore = useTransactionStore.getState()
  const walletStore = useWalletStore.getState()
  const status = randomPaymentResult()
  const seq = txStore.nextSequence()

  const tx: Transaction = {
    id: crypto.randomUUID(),
    userId,
    amount: payload.amount,
    status,
    createdAt: new Date().toISOString(),
    paymentMethod: 'Sandbox Payment',
    referenceNumber: generateReferenceNumber(seq),
    failureReason: status === 'FAILED' ? 'Simulated payment failure' : undefined,
  }

  txStore.addTransaction(tx)

  const balance =
    status === 'SUCCESS'
      ? walletStore.increaseBalance(userId, payload.amount)
      : walletStore.getBalance(userId)

  return { transaction: tx, balance }
}

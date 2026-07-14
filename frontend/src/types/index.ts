export type PaymentStatus = 'SUCCESS' | 'FAILED'

export interface User {
  id: string
  username: string
  createdAt: string
}

export interface StoredUser extends User {
  password: string
}

export interface Wallet {
  userId: string
  balance: number
}

export interface Transaction {
  id: string
  userId: string
  amount: number
  status: PaymentStatus
  createdAt: string
  paymentMethod: string
  referenceNumber: string
  failureReason?: string
}

export interface TopUpRequest {
  amount: number
}

export interface TopUpResponse {
  transaction: Transaction
  balance: number
}

export interface AuthCredentials {
  username: string
  password: string
}

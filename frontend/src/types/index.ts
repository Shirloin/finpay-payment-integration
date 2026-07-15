export type OrderStatusValue = 'PENDING' | 'PAID' | 'FAIL'

export interface ApiResponse<T> {
  data: T
  message: string
  error: unknown | null
}

export interface User {
  id: string
  username: string
  createdAt: string
}

export interface AuthResponse {
  accessToken: string
  tokenType: 'Bearer'
  expiresAt: string
  user: User
}

export interface Transaction {
  id: string
  orderId: string
  amount: number
  status: OrderStatusValue
  paymentMethod: string | null
  paymentCode: string | null
  redirectUrl: string | null
  createdAt: string
}

export interface TopUpRequest {
  amount: number
}

export interface TopUpResponse {
  orderId: string
  amount: number
  paymentCode: string | null
  redirectUrl: string
  expiryLink: string | null
  status: OrderStatusValue
  createdAt: string
}

export interface OrderStatus {
  orderId: string
  amount: number
  status: OrderStatusValue
  paymentMethod: string | null
  paymentCode: string | null
  redirectUrl: string | null
  balance: number
}

export interface AuthCredentials {
  username: string
  password: string
}

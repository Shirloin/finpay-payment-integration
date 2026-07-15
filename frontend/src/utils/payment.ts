export const PENDING_ORDER_KEY = 'finpay-pending-order-id'

export function continueToPayment(orderId: string, redirectUrl: string) {
  sessionStorage.setItem(PENDING_ORDER_KEY, orderId)
  window.location.href = redirectUrl
}

export function openPaymentInNewTab(orderId: string, redirectUrl: string) {
  sessionStorage.setItem(PENDING_ORDER_KEY, orderId)
  window.open(redirectUrl, '_blank', 'noopener,noreferrer')
}

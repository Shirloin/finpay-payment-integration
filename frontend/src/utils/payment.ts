export function generateReferenceNumber(sequence: number): string {
  const now = new Date()
  const yyyy = now.getFullYear()
  const mm = String(now.getMonth() + 1).padStart(2, '0')
  const dd = String(now.getDate()).padStart(2, '0')
  const seq = String(sequence).padStart(4, '0')
  return `TRX-${yyyy}${mm}${dd}-${seq}`
}

export function randomPaymentResult(): 'SUCCESS' | 'FAILED' {
  return Math.random() < 0.8 ? 'SUCCESS' : 'FAILED'
}

export function delay(ms: number): Promise<void> {
  return new Promise((resolve) => setTimeout(resolve, ms))
}

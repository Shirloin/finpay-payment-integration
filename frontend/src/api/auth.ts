import { delay } from '@/utils/payment'
import { useAuthStore } from '@/store/auth.store'
import type { AuthCredentials, User } from '@/types'

// POST /register
export async function registerRequest(payload: AuthCredentials): Promise<User> {
  await delay(800)
  const store = useAuthStore.getState()
  if (store.findUser(payload.username)) {
    throw new Error('Username already exists')
  }
  const user = {
    id: crypto.randomUUID(),
    username: payload.username,
    password: payload.password,
    createdAt: new Date().toISOString(),
  }
  store.registerUser(user)
  const { password: _pw, ...safe } = user
  return safe
}

// POST /login
export async function loginRequest(payload: AuthCredentials): Promise<User> {
  await delay(800)
  const found = useAuthStore.getState().findUser(payload.username)
  if (!found || found.password !== payload.password) {
    throw new Error('Invalid username or password')
  }
  const { password: _pw, ...safe } = found
  return safe
}

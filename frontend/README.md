# FinPay — Payment Gateway Integration Template

A production-ready React template that simulates a Top Up flow against a mock
payment gateway. Built with React 19, Vite, TypeScript, TanStack Query,
Zustand, Tailwind CSS, shadcn/ui-style components, React Hook Form, and Zod.

There is **no backend yet** — all state lives in the browser (Zustand +
`localStorage`), and API calls are simulated with `Promise + setTimeout`
through an Axios-shaped API layer so the app can graduate to a real backend
with minimal change.

## Getting started

```bash
npm install
npm run dev
```

Then open http://localhost:5173.

Other scripts:

- `npm run build` — production build (`tsc -b && vite build`)
- `npm run preview` — preview built assets
- `npm run lint` — run oxlint

## Feature tour

1. Register a username / password on `/register`
2. Log in on `/login`
3. Land on `/dashboard`:
   - **Balance card** — displays the current wallet balance
   - **Top Up form** — Zod-validated (Rp 10.000 – Rp 10.000.000). Simulates
     a payment (~1.5s). 80% success, 20% failure.
   - **Transaction history** — table on desktop, cards on mobile. Success /
     Failed badges. Newest first.
   - **Result modal** — success shows updated balance; failure offers a retry
4. Logout from the navbar (with confirmation).
5. Toggle dark mode from the navbar.

State is persisted via `zustand/middleware/persist`, so refreshing the page
keeps you logged in.

## Architecture

```
src/
  api/              Axios instance + endpoint-shaped functions (mocked today)
    axios.ts        pre-configured axios instance
    auth.ts         POST /register, POST /login
    topup.ts        POST /topup, GET /balance, GET /transactions
  components/
    common/         reusable app-wide bits (EmptyState, ThemeToggle)
    forms/          LoginForm, RegisterForm
    layout/         Navbar
    payment/        BalanceCard, TopUpForm, TransactionHistory, modal, badge
    ui/             shadcn-style primitives (Button, Card, Dialog, ...)
  hooks/            TanStack Query hooks (useBalance, useTransactions, useTopUp, useAuth)
  layouts/          AuthLayout, DashboardLayout
  lib/              cn() helper
  pages/            LoginPage, RegisterPage, DashboardPage
  routes/           AppRouter, ProtectedRoute
  store/            Zustand stores (auth, wallet, transaction, theme)
  types/            User, Wallet, Transaction, PaymentStatus, ...
  utils/            formatCurrency, formatDate, generateReferenceNumber,
                    randomPaymentResult, delay
```

Separation of concerns:

- **UI components** never touch storage or randomness directly.
- **Hooks (`useBalance`, `useTopUpMutation`, …)** wrap TanStack Query and are
  the single interface the UI consumes.
- **API layer** (`src/api/*`) has the same shape as a real REST client: each
  function corresponds to an endpoint (`POST /topup`, `GET /balance`, …) and
  returns a promise. Today those promises resolve from Zustand; tomorrow they
  will resolve from the network.
- **Zustand stores** are the mock persistence layer. Once the backend is
  live, `walletStore` and `transactionStore` become caches / read-through
  local state (or are removed entirely).

## Swapping the mock for a real backend

The whole point of this template is that the swap is local to `src/api/`.

Example — replacing `topUpRequest`:

```ts
// src/api/topup.ts
import { api } from './axios'

export async function topUpRequest(_userId: string, payload: TopUpRequest) {
  const { data } = await api.post<TopUpResponse>('/topup', payload)
  return data
}
```

The hook (`useTopUpMutation`), the form (`TopUpForm`), the modal, the badges,
and the transaction list all stay untouched — they consume `TopUpResponse`
regardless of where it came from.

Auth changes similarly: replace `loginRequest` / `registerRequest`, wire the
returned token into the Axios interceptor in `src/api/axios.ts`, and drop
persistence of `users` / passwords from `auth.store.ts` (keep only
`currentUser`).

## Tech stack

| Concern           | Choice                       |
|-------------------|------------------------------|
| Bundler           | Vite 8                       |
| Framework         | React 19                     |
| Language          | TypeScript (strict)          |
| Routing           | React Router v7              |
| HTTP              | Axios                        |
| Server state      | TanStack Query v5            |
| Client state      | Zustand + persist            |
| Forms             | React Hook Form              |
| Validation        | Zod                          |
| Styling           | Tailwind CSS v3              |
| UI primitives     | Radix UI + shadcn-style code |
| Icons             | Lucide React                 |
| Toasts            | Sonner                       |

## Notes on the simulation

- Registered users, current session, wallet balance, and transactions all
  persist via `localStorage`. Clear the `finpay-*` keys to reset.
- Passwords are stored in plaintext client-side because this is a UI-only
  simulation. **Do not ship this to production as-is.**
- The 80/20 success/failure split lives in `utils/payment.ts` —
  `randomPaymentResult()`.

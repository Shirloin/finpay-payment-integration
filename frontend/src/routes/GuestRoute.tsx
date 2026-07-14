import { Navigate, Outlet, useLocation } from 'react-router-dom'
import type { Location } from 'react-router-dom'
import { Spinner } from '@/components/ui/spinner'
import { useAuthVerification } from '@/hooks/useAuthVerification'
import { selectIsAuthenticated, useAuthStore } from '@/store/auth.store'

interface GuestRouteState {
  from?: Location
}

export function GuestRoute() {
  const isAuthenticated = useAuthStore(selectIsAuthenticated)
  const location = useLocation()
  const verification = useAuthVerification()

  if (!isAuthenticated || verification.isError) {
    return <Outlet />
  }

  if (verification.isPending) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <Spinner className="h-6 w-6" />
      </div>
    )
  }

  const previousLocation = (location.state as GuestRouteState | null)?.from
  const destination = previousLocation
    ? `${previousLocation.pathname}${previousLocation.search}${previousLocation.hash}`
    : '/dashboard'

  return <Navigate to={destination} replace />
}

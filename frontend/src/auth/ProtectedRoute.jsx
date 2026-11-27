import { Navigate, Outlet, useLocation } from 'react-router-dom'
import { useAuth } from './useAuth.js'

export default function ProtectedRoute({ allowedRoles }) {
  const { user, loading } = useAuth()
  const location = useLocation()

  if (loading) {
    return <p role="status">Validando sesión...</p>
  }

  if (!user) {
    return <Navigate to="/login" state={{ from: location }} replace />
  }

  if (allowedRoles && !allowedRoles.includes(user.role)) {
    return <p role="alert">No tienes permiso para acceder a esta sección.</p>
  }

  return <Outlet />
}

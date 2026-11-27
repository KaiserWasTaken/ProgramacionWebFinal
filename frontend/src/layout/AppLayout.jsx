import { useState } from 'react'
import { NavLink, Outlet, useNavigate } from 'react-router-dom'
import { useAuth } from '../auth/useAuth.js'
import UserAvatar from '../auth/UserAvatar.jsx'

export default function AppLayout() {
  const { authError, logout, user } = useAuth()
  const navigate = useNavigate()
  const [menuOpen, setMenuOpen] = useState(false)

  async function handleLogout() {
    let notice
    try {
      await logout()
    } catch {
      notice = 'La sesión se cerró en este navegador, pero el servidor no confirmó su revocación.'
    }
    navigate('/login', { replace: true, state: notice ? { notice } : null })
  }

  const displayName = user?.fullName || user?.email?.split('@')[0] || 'Usuario'

  return (
    <div className="app-frame">
      <header className="app-header">
        <button
          aria-expanded={menuOpen}
          aria-label={menuOpen ? 'Cerrar menú' : 'Abrir menú'}
          className="menu-toggle"
          onClick={() => setMenuOpen((open) => !open)}
          type="button"
        >
          <span aria-hidden="true">☰</span>
        </button>
        <div className="app-user">
          <div className="app-user-copy">
            <span className="app-user-name">{displayName}</span>
            <span className="app-user-email">{user?.email}</span>
          </div>
          <UserAvatar user={user} />
        </div>
      </header>
      <div className="app-body">
        <aside className={`app-sidebar${menuOpen ? ' app-sidebar--open' : ''}`}>
          <nav aria-label="Navegación principal" className="sidebar-nav">
            <NavLink end onClick={() => setMenuOpen(false)} to="/">
              Catálogo
            </NavLink>
            <NavLink onClick={() => setMenuOpen(false)} to="/tmdb">
              Buscar en TMDB
            </NavLink>
            {['USER', 'ADMIN', 'MODERATOR'].includes(user?.role) && (
              <NavLink onClick={() => setMenuOpen(false)} to="/profile">
                Mi perfil
              </NavLink>
            )}
            {user?.role === 'USER' && (
              <>
                <NavLink onClick={() => setMenuOpen(false)} to="/watchlist">
                  Mi lista
                </NavLink>
                <NavLink onClick={() => setMenuOpen(false)} to="/my-reviews">
                  Mis reseñas
                </NavLink>
              </>
            )}
            {['ADMIN', 'MODERATOR'].includes(user?.role) && (
              <NavLink onClick={() => setMenuOpen(false)} to="/moderation">
                Moderación
              </NavLink>
            )}
            {user?.role === 'ADMIN' && (
              <>
                <NavLink onClick={() => setMenuOpen(false)} to="/admin/titles">
                  Títulos
                </NavLink>
                <NavLink onClick={() => setMenuOpen(false)} to="/admin/genres">
                  Géneros
                </NavLink>
                <NavLink onClick={() => setMenuOpen(false)} to="/admin/people">
                  Personas
                </NavLink>
                <NavLink onClick={() => setMenuOpen(false)} to="/admin/users">
                  Usuarios
                </NavLink>
                <NavLink onClick={() => setMenuOpen(false)} to="/admin/reviews">
                  Reseñas
                </NavLink>
              </>
            )}
          </nav>
          <button className="logout-button" onClick={handleLogout} type="button">
            Cerrar sesión
          </button>
        </aside>
        <main className="app-content">
          {authError && <p className="app-notice" role="alert">{authError}</p>}
          <Outlet />
        </main>
      </div>
    </div>
  )
}

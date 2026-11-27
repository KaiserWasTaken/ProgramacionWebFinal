import { useCallback, useEffect, useMemo, useState } from 'react'
import apiClient, { AUTH_EXPIRED_EVENT } from '../api/client.js'
import { AuthContext } from './AuthContext.js'
import { clearAccessToken, getAccessToken, saveAccessToken } from './tokenStorage.js'

export function AuthProvider({ children }) {
  const [token, setToken] = useState(getAccessToken)
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(() => Boolean(getAccessToken()))
  const [authError, setAuthError] = useState(null)

  const clearSession = useCallback(() => {
    clearAccessToken()
    setToken(null)
    setUser(null)
  }, [])

  const saveSession = useCallback((authResponse, rememberMe = false) => {
    if (!authResponse?.accessToken || !authResponse.email || !authResponse.role) {
      throw new Error('El servidor devolvió una respuesta de autenticación inválida.')
    }

    saveAccessToken(authResponse.accessToken, rememberMe)
    setToken(authResponse.accessToken)
    setUser({ email: authResponse.email, role: authResponse.role })
    setAuthError(null)
    return authResponse
  }, [])

  const login = useCallback(async (credentials, rememberMe = false) => {
    const { data } = await apiClient.post('/api/auth/login', credentials)
    return saveSession(data, rememberMe)
  }, [saveSession])

  const register = useCallback(async (details) => {
    const { data } = await apiClient.post('/api/auth/register', details)
    return saveSession(data)
  }, [saveSession])

  const googleLogin = useCallback(async (credential, rememberMe = false) => {
    const { data } = await apiClient.post('/api/auth/google', { credential })
    return saveSession(data, rememberMe)
  }, [saveSession])

  const logout = useCallback(async () => {
    try {
      if (token) {
        await apiClient.post('/api/auth/logout')
      }
    } finally {
      clearSession()
    }
  }, [clearSession, token])

  useEffect(() => {
    const handleExpiredSession = () => {
      clearSession()
      setAuthError('Tu sesión expiró. Inicia sesión nuevamente.')
    }
    window.addEventListener(AUTH_EXPIRED_EVENT, handleExpiredSession)
    return () => window.removeEventListener(AUTH_EXPIRED_EVENT, handleExpiredSession)
  }, [clearSession])

  useEffect(() => {
    let active = true

    if (!token) {
      return () => {
        active = false
      }
    }

    apiClient.get('/api/auth/me')
      .then(async ({ data }) => {
        if (!data.email || !data.role) {
          throw new Error('El servidor devolvió una identidad inválida.')
        }

        let currentUser = { ...data }
        if (['USER', 'ADMIN', 'MODERATOR'].includes(data.role)) {
          try {
            const { data: profile } = await apiClient.get('/api/users/me')
            currentUser = { ...currentUser, ...profile }
            if (active) setAuthError(null)
          } catch (error) {
            if (error.response?.status === 401) throw error
            if (active) {
              setAuthError('No se pudo cargar tu perfil. Algunas opciones pueden no estar disponibles.')
            }
          }
        } else if (active) {
          setAuthError(null)
        }

        if (active) setUser(currentUser)
      })
      .catch((error) => {
        if (!active) return
        if (error.response?.status === 401) {
          clearSession()
        } else {
          setAuthError('No se pudo validar la sesión. Verifica tu conexión e inténtalo de nuevo.')
        }
      })
      .finally(() => {
        if (active) setLoading(false)
      })

    return () => {
      active = false
    }
  }, [clearSession, token])

  const value = useMemo(() => ({
    token,
    user,
    loading,
    authError,
    isAuthenticated: Boolean(token && user),
    login,
    googleLogin,
    register,
    logout,
  }), [authError, googleLogin, loading, login, logout, register, token, user])

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

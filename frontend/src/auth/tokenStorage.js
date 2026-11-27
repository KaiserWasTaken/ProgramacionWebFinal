export const AUTH_TOKEN_KEY = 'bodriopopcorn.accessToken'

export function getAccessToken() {
  if (typeof window === 'undefined') return null
  return window.localStorage.getItem(AUTH_TOKEN_KEY) || window.sessionStorage.getItem(AUTH_TOKEN_KEY)
}

export function saveAccessToken(token, rememberMe = false) {
  if (typeof window === 'undefined') {
    throw new Error('No se puede guardar una sesión fuera del navegador.')
  }

  clearAccessToken()
  const storage = rememberMe ? window.localStorage : window.sessionStorage
  storage.setItem(AUTH_TOKEN_KEY, token)
}

export function clearAccessToken() {
  if (typeof window === 'undefined') return
  window.localStorage.removeItem(AUTH_TOKEN_KEY)
  window.sessionStorage.removeItem(AUTH_TOKEN_KEY)
}

import axios from 'axios'
import { AUTH_TOKEN_KEY, getAccessToken } from '../auth/tokenStorage.js'

export const AUTH_EXPIRED_EVENT = 'bodriopopcorn:auth-expired'
export { AUTH_TOKEN_KEY }

const isPublicAuthRequest = (url = '') =>
  [
    '/api/auth/login',
    '/api/auth/google',
    '/api/auth/register',
    '/api/auth/forgot-password',
    '/api/auth/reset-password',
  ].includes(url.split('?')[0])

const apiClient = axios.create({
  baseURL: import.meta.env?.VITE_API_BASE_URL ?? '',
})

apiClient.interceptors.request.use((config) => {
  if (!isPublicAuthRequest(config.url) && typeof window !== 'undefined') {
    const token = getAccessToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
  }

  return config
})

apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (
      error.response?.status === 401 &&
      !isPublicAuthRequest(error.config?.url) &&
      typeof window !== 'undefined'
    ) {
      window.dispatchEvent(new Event(AUTH_EXPIRED_EVENT))
    }

    return Promise.reject(error)
  },
)

export default apiClient

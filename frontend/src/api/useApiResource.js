import { useEffect, useState } from 'react'
import apiClient from './client.js'

function getErrorMessage(error) {
  return error.response?.data?.message ||
    (error.response
      ? 'No se pudo cargar el recurso.'
      : 'No se pudo conectar con el servidor.')
}

export default function useApiResource(endpoint, enabled = true) {
  const [result, setResult] = useState(null)
  const requestKey = `${endpoint}:${enabled}`

  useEffect(() => {
    if (!enabled || !endpoint) return undefined

    let active = true
    apiClient.get(endpoint)
      .then(({ data }) => {
        if (active) setResult({ key: requestKey, data, error: '' })
      })
      .catch((error) => {
        if (active) setResult({ key: requestKey, data: null, error: getErrorMessage(error) })
      })

    return () => {
      active = false
    }
  }, [enabled, endpoint, requestKey])

  const currentResult = result?.key === requestKey ? result : null
  return {
    data: currentResult?.data ?? null,
    error: currentResult?.error || '',
    loading: enabled && Boolean(endpoint) && !currentResult,
  }
}

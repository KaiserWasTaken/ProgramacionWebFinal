import { useEffect, useMemo, useState } from 'react'
import apiClient from './client.js'
import { buildPageParams } from './pagination.js'

const EMPTY_PAGE = { content: [], number: 0, size: 0, totalElements: 0, totalPages: 0 }

function getErrorMessage(error) {
  return error.response?.data?.message ||
    (error.response
      ? 'No se pudieron cargar los datos.'
      : 'No se pudo conectar con el servidor.')
}

export default function usePagedData(
  endpoint,
  { enabled = true, filters = {}, page = 0, size = 20, sort } = {},
) {
  const filtersKey = JSON.stringify(filters)
  const params = useMemo(
    () => buildPageParams(page, size, JSON.parse(filtersKey), sort),
    [filtersKey, page, size, sort],
  )
  const [result, setResult] = useState(null)
  const [reloadId, setReloadId] = useState(0)
  const requestKey = JSON.stringify({ endpoint, enabled, params, reloadId })

  useEffect(() => {
    if (!enabled || !endpoint) return undefined

    let active = true
    apiClient.get(endpoint, { params })
      .then(({ data }) => {
        if (active) setResult({ key: requestKey, data, error: null })
      })
      .catch((error) => {
        if (active) setResult({ key: requestKey, data: EMPTY_PAGE, error: getErrorMessage(error) })
      })

    return () => {
      active = false
    }
  }, [enabled, endpoint, params, requestKey])

  const currentResult = result?.key === requestKey ? result : null
  return {
    data: currentResult?.data || EMPTY_PAGE,
    error: currentResult?.error || '',
    loading: enabled && Boolean(endpoint) && !currentResult,
    reload: () => setReloadId((current) => current + 1),
  }
}

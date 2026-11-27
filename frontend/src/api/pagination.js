export function buildPageParams(page, size, filters = {}, sort) {
  const params = { page, size }
  if (sort) params.sort = sort

  for (const [key, value] of Object.entries(filters)) {
    if (value === null || value === undefined) continue
    const normalized = typeof value === 'string' ? value.trim() : value
    if (normalized !== '') params[key] = normalized
  }

  return params
}

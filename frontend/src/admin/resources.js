function normalizeOptional(value) {
  return value?.trim() || null
}

function buildTitlePayload(values, record) {
  return {
    type: values.type,
    name: values.name.trim(),
    synopsis: values.synopsis.trim(),
    releaseYear: Number(values.releaseYear),
    durationMinutes: values.type === 'MOVIE' ? Number(values.durationMinutes) : null,
    seasons: values.type === 'SERIES' ? Number(values.seasons) : null,
    posterUrl: normalizeOptional(values.posterUrl),
    genreIds: values.genreIds.map(Number),
    credits: record?.credits.map(({ person, characterName }) => ({
      personId: person.id,
      characterName,
    })) || [],
  }
}

export function buildResourcePayload(resource, values, record) {
  if (resource === 'titles') return buildTitlePayload(values, record)

  if (resource === 'genres') return { name: values.name.trim() }

  if (resource === 'people') {
    return { name: values.name.trim(), roleType: values.roleType }
  }

  if (resource === 'users') {
    return {
      email: values.email.trim(),
      fullName: values.fullName.trim(),
      phone: normalizeOptional(values.phone),
      avatarUrl: normalizeOptional(values.avatarUrl),
      role: values.role,
      enabled: values.enabled,
    }
  }

  throw new Error(`Recurso administrativo desconocido: ${resource}`)
}

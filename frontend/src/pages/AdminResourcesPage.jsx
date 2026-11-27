import { useState } from 'react'
import { useParams } from 'react-router-dom'
import apiClient from '../api/client.js'
import useDebouncedValue from '../api/useDebouncedValue.js'
import usePagedData from '../api/usePagedData.js'
import { buildResourcePayload } from '../admin/resources.js'
import ConfirmModal from '../components/ConfirmModal.jsx'
import Pagination from '../components/Pagination.jsx'

const RESOURCES = {
  titles: { endpoint: '/api/titles', title: 'Administrar títulos', sort: 'id,desc', canCreate: true },
  genres: { endpoint: '/api/genres', title: 'Administrar géneros', sort: 'name,asc', canCreate: true },
  people: { endpoint: '/api/people', title: 'Administrar personas', sort: 'name,asc', canCreate: true },
  users: { endpoint: '/api/users', title: 'Administrar usuarios', sort: 'email,asc', canCreate: false },
}

function getInitialValues(resource, record) {
  if (resource === 'titles') {
    return record ? {
      type: record.type,
      name: record.name,
      synopsis: record.synopsis,
      releaseYear: String(record.releaseYear),
      durationMinutes: record.durationMinutes ? String(record.durationMinutes) : '',
      seasons: record.seasons ? String(record.seasons) : '',
      posterUrl: record.posterUrl || '',
      genreIds: record.genres.map((genre) => String(genre.id)),
    } : {
      type: 'MOVIE',
      name: '',
      synopsis: '',
      releaseYear: String(new Date().getFullYear()),
      durationMinutes: '90',
      seasons: '',
      posterUrl: '',
      genreIds: [],
    }
  }

  if (resource === 'genres') return { name: record?.name || '' }
  if (resource === 'people') {
    return { name: record?.name || '', roleType: record?.roleType || 'ACTOR' }
  }
  return {
    email: record?.email || '',
    fullName: record?.fullName || '',
    phone: record?.phone || '',
    avatarUrl: record?.avatarUrl || '',
    role: record?.role || 'USER',
    enabled: record?.enabled ?? true,
  }
}

function ResourceForm({ onChange, onSubmit, resource, saving, values }) {
  return (
    <form className="resource-form" onSubmit={onSubmit}>
      {resource === 'titles' && (
        <>
          <label>
            Tipo
            <select onChange={(event) => onChange('type', event.target.value)} value={values.type}>
              <option value="MOVIE">Película</option>
              <option value="SERIES">Serie</option>
            </select>
          </label>
          <label>
            Nombre
            <input maxLength="180" onChange={(event) => onChange('name', event.target.value)} required value={values.name} />
          </label>
          <label>
            Sinopsis
            <textarea maxLength="5000" onChange={(event) => onChange('synopsis', event.target.value)} required rows={3} value={values.synopsis} />
          </label>
          <label>
            Año de estreno
            <input max="2100" min="1888" onChange={(event) => onChange('releaseYear', event.target.value)} required type="number" value={values.releaseYear} />
          </label>
          {values.type === 'MOVIE' ? (
            <label>
              Duración (minutos)
              <input min="1" onChange={(event) => onChange('durationMinutes', event.target.value)} required type="number" value={values.durationMinutes} />
            </label>
          ) : (
            <label>
              Temporadas
              <input min="1" onChange={(event) => onChange('seasons', event.target.value)} required type="number" value={values.seasons} />
            </label>
          )}
          <label>
            URL del póster
            <input maxLength="500" onChange={(event) => onChange('posterUrl', event.target.value)} type="url" value={values.posterUrl} />
          </label>
          <label>
            Géneros (Ctrl/Cmd para seleccionar varios)
            <GenreOptions onChange={onChange} selectedIds={values.genreIds} />
          </label>
        </>
      )}
      {resource === 'genres' && (
        <label>
          Nombre
          <input maxLength="60" onChange={(event) => onChange('name', event.target.value)} required value={values.name} />
        </label>
      )}
      {resource === 'people' && (
        <>
          <label>
            Nombre
            <input maxLength="120" onChange={(event) => onChange('name', event.target.value)} required value={values.name} />
          </label>
          <label>
            Rol
            <select onChange={(event) => onChange('roleType', event.target.value)} value={values.roleType}>
              <option value="ACTOR">Actor / actriz</option>
              <option value="DIRECTOR">Director</option>
            </select>
          </label>
        </>
      )}
      {resource === 'users' && (
        <>
          <label>
            Correo
            <input onChange={(event) => onChange('email', event.target.value)} required type="email" value={values.email} />
          </label>
          <label>
            Nombre completo
            <input maxLength="120" onChange={(event) => onChange('fullName', event.target.value)} required value={values.fullName} />
          </label>
          <label>
            Teléfono
            <input maxLength="30" onChange={(event) => onChange('phone', event.target.value)} value={values.phone} />
          </label>
          <label>
            URL del avatar
            <input maxLength="500" onChange={(event) => onChange('avatarUrl', event.target.value)} type="url" value={values.avatarUrl} />
          </label>
          <label>
            Rol
            <select onChange={(event) => onChange('role', event.target.value)} value={values.role}>
              <option value="USER">Usuario</option>
              <option value="MODERATOR">Moderador</option>
              <option value="ADMIN">Administrador</option>
            </select>
          </label>
          <label className="checkbox-field">
            <input checked={values.enabled} onChange={(event) => onChange('enabled', event.target.checked)} type="checkbox" />
            Cuenta habilitada
          </label>
        </>
      )}
      <div className="modal-actions">
        <button className="secondary-button" disabled={saving} onClick={() => onChange('cancel')} type="button">
          Cancelar
        </button>
        <button className="primary-button" disabled={saving} type="submit">
          {saving ? 'Guardando...' : 'Guardar'}
        </button>
      </div>
    </form>
  )
}

function GenreOptions({ onChange, selectedIds }) {
  const genres = usePagedData('/api/genres', { page: 0, size: 100, sort: 'name,asc' })
  return (
    <>
      {genres.loading && <span role="status">Cargando géneros...</span>}
      {genres.error && <span className="inline-error" role="alert">{genres.error}</span>}
      <select
        disabled={genres.loading || Boolean(genres.error)}
        multiple
        onChange={(event) => onChange('genreIds', Array.from(event.target.selectedOptions, (option) => option.value))}
        required
        size={Math.min(5, Math.max(2, genres.data.content.length))}
        value={selectedIds}
      >
        {genres.data.content.map((genre) => (
          <option key={genre.id} value={genre.id}>{genre.name}</option>
        ))}
      </select>
    </>
  )
}

function getCellValue(resource, record, field) {
  if (field === 'type') return record.type === 'MOVIE' ? 'Película' : 'Serie'
  if (field === 'genres') return record.genres.map((genre) => genre.name).join(', ')
  if (field === 'roleType') return record.roleType === 'ACTOR' ? 'Actor / actriz' : 'Director'
  if (field === 'role') return record.role
  if (field === 'enabled') return record.enabled ? 'Habilitada' : 'Deshabilitada'
  if (field === 'avgRating') return record.avgRating ? `${Number(record.avgRating).toFixed(1)} / 5` : 'Sin calificación'
  if (field === 'fullName') return record.fullName || '—'
  if (field === 'releaseYear') return record.releaseYear
  return record[field] || '—'
}

export default function AdminResourcesPage() {
  const { resource } = useParams()
  const config = RESOURCES[resource]
  const [page, setPage] = useState(0)
  const [search, setSearch] = useState('')
  const debouncedSearch = useDebouncedValue(search)
  const [formState, setFormState] = useState(null)
  const [deleting, setDeleting] = useState(null)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')
  const [notice, setNotice] = useState('')
  const data = usePagedData(config?.endpoint || '', {
    filters: debouncedSearch ? { search: debouncedSearch } : {},
    page,
    size: 10,
    sort: config?.sort,
  })

  if (!config) return <p className="inline-error" role="alert">No existe ese módulo administrativo.</p>

  const columns = resource === 'titles' ? ['name', 'type', 'releaseYear', 'genres', 'avgRating']
    : resource === 'genres' ? ['name']
      : resource === 'people' ? ['name', 'roleType']
        : ['email', 'fullName', 'role', 'enabled']

  function startEditing(record = null) {
    setError('')
    setFormState({ record, values: getInitialValues(resource, record) })
  }

  function updateField(field, value) {
    if (field === 'cancel') {
      setFormState(null)
      return
    }
    setFormState((current) => ({ ...current, values: { ...current.values, [field]: value } }))
  }

  async function saveRecord(event) {
    event.preventDefault()
    setSaving(true)
    setError('')
    try {
      const payload = buildResourcePayload(resource, formState.values, formState.record)
      if (formState.record) {
        await apiClient.put(`${config.endpoint}/${formState.record.id}`, payload)
      } else {
        await apiClient.post(config.endpoint, payload)
      }
      setFormState(null)
      setNotice('Los cambios se guardaron correctamente.')
      data.reload()
    } catch (requestError) {
      setError(requestError.response?.data?.message || 'No se pudo guardar el registro.')
    } finally {
      setSaving(false)
    }
  }

  async function deleteRecord() {
    setSaving(true)
    setError('')
    try {
      await apiClient.delete(`${config.endpoint}/${deleting.id}`)
      setDeleting(null)
      setNotice('El registro se eliminó correctamente.')
      data.reload()
    } catch (requestError) {
      setError(requestError.response?.data?.message || 'No se pudo eliminar el registro.')
    } finally {
      setSaving(false)
    }
  }

  return (
    <section className="domain-page" aria-labelledby="admin-resource-title">
      <div className="page-heading">
        <div>
          <h1 id="admin-resource-title">{config.title}</h1>
          <p>Busca, consulta y administra los registros.</p>
        </div>
        {config.canCreate && (
          <button className="primary-button" onClick={() => startEditing()} type="button">
            Crear
          </button>
        )}
      </div>
      <label className="admin-search">
        <span className="visually-hidden">Buscar registros</span>
        <input onChange={(event) => { setPage(0); setSearch(event.target.value) }} placeholder="Buscar..." type="search" value={search} />
      </label>
      {notice && <p className="success-message" role="status">{notice}</p>}
      {error && <p className="inline-error" role="alert">{error}</p>}
      {data.error && <p className="inline-error" role="alert">{data.error}</p>}
      {data.loading ? (
        <p className="loading-message" role="status">Cargando registros...</p>
      ) : (
        <>
          <div className="table-scroll">
            <table className="catalog-table">
              <thead>
                <tr>
                  {columns.map((field) => <th key={field} scope="col">{field === 'avgRating' ? 'Calificación' : field === 'releaseYear' ? 'Año' : field === 'roleType' ? 'Tipo' : field === 'fullName' ? 'Nombre' : field === 'enabled' ? 'Estado' : field === 'genres' ? 'Géneros' : field === 'role' ? 'Rol' : field === 'type' ? 'Formato' : field === 'email' ? 'Correo' : 'Nombre'}</th>)}
                  <th scope="col">Acciones</th>
                </tr>
              </thead>
              <tbody>
                {data.data.content.map((record) => (
                  <tr key={record.id}>
                    {columns.map((field) => <td key={field}>{getCellValue(resource, record, field)}</td>)}
                    <td className="table-actions">
                      <button className="text-button" onClick={() => startEditing(record)} type="button">Editar</button>
                      <button className="text-button text-button--danger" onClick={() => setDeleting(record)} type="button">Eliminar</button>
                    </td>
                  </tr>
                ))}
                {!data.data.content.length && <tr><td className="empty-table" colSpan={columns.length + 1}>No hay registros.</td></tr>}
              </tbody>
            </table>
          </div>
          <Pagination onPageChange={setPage} page={data.data.number} totalPages={data.data.totalPages} />
        </>
      )}
      {formState && (
        <div className="modal-backdrop">
          <section aria-labelledby="resource-form-title" aria-modal="true" className="confirm-modal resource-modal" role="dialog">
            <h2 id="resource-form-title">{formState.record ? 'Editar registro' : 'Crear registro'}</h2>
            <ResourceForm
              onChange={updateField}
              onSubmit={saveRecord}
              resource={resource}
              saving={saving}
              values={formState.values}
            />
          </section>
        </div>
      )}
      {deleting && (
        <ConfirmModal
          cancelLabel="Cancelar"
          confirmLabel="Eliminar"
          description={`¿Eliminar ${deleting.name || deleting.email}? Esta acción no se puede deshacer.`}
          loading={saving}
          onCancel={() => setDeleting(null)}
          onConfirm={deleteRecord}
          title="Eliminar registro"
        />
      )}
    </section>
  )
}

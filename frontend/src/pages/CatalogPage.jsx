import { useState } from 'react'
import { Link } from 'react-router-dom'
import useDebouncedValue from '../api/useDebouncedValue.js'
import Pagination from '../components/Pagination.jsx'
import usePagedData from '../api/usePagedData.js'

const EMPTY_FILTERS = { search: '', genreId: '', type: '', year: '', minRating: '' }

function getDirectors(title) {
  return title.credits
    .map((credit) => credit.person)
    .filter((person) => person.roleType === 'DIRECTOR')
    .map((person) => person.name)
    .join(', ') || '—'
}

function getRatingLabel(rating) {
  const value = Number(rating || 0)
  return value ? `${value.toFixed(1)} de 5` : 'Sin calificación'
}

export default function CatalogPage() {
  const [filters, setFilters] = useState(EMPTY_FILTERS)
  const [page, setPage] = useState(0)
  const search = useDebouncedValue(filters.search)
  const titles = usePagedData('/api/titles', {
    filters: { ...filters, search },
    page,
    size: 10,
    sort: 'id,desc',
  })
  const genres = usePagedData('/api/genres', { page: 0, size: 100, sort: 'name,asc' })

  function updateFilter(field, value) {
    setPage(0)
    setFilters((current) => ({ ...current, [field]: value }))
  }

  return (
    <section className="domain-page catalog-page" aria-labelledby="catalog-title">
      <div className="page-heading">
        <div>
          <h1 id="catalog-title">Catálogo</h1>
          <p>Encuentra películas y series para tu próxima función.</p>
        </div>
      </div>
      <div className="catalog-filters" aria-label="Filtros del catálogo">
        <label>
          <span className="visually-hidden">Buscar títulos</span>
          <input
            onChange={(event) => updateFilter('search', event.target.value)}
            placeholder="Busca una película o serie..."
            type="search"
            value={filters.search}
          />
        </label>
        <label>
          <span className="visually-hidden">Género</span>
          <select
            onChange={(event) => updateFilter('genreId', event.target.value)}
            value={filters.genreId}
          >
            <option value="">Todos los géneros</option>
            {genres.data.content.map((genre) => (
              <option key={genre.id} value={genre.id}>{genre.name}</option>
            ))}
          </select>
        </label>
        <label>
          <span className="visually-hidden">Tipo</span>
          <select onChange={(event) => updateFilter('type', event.target.value)} value={filters.type}>
            <option value="">Películas y series</option>
            <option value="MOVIE">Películas</option>
            <option value="SERIES">Series</option>
          </select>
        </label>
        <label>
          <span className="visually-hidden">Año de estreno</span>
          <input
            max="2100"
            min="1888"
            onChange={(event) => updateFilter('year', event.target.value)}
            placeholder="Año"
            type="number"
            value={filters.year}
          />
        </label>
        <label>
          <span className="visually-hidden">Calificación mínima</span>
          <select
            onChange={(event) => updateFilter('minRating', event.target.value)}
            value={filters.minRating}
          >
            <option value="">Cualquier calificación</option>
            <option value="1">1 o más</option>
            <option value="2">2 o más</option>
            <option value="3">3 o más</option>
            <option value="4">4 o más</option>
            <option value="5">5</option>
          </select>
        </label>
      </div>
      {genres.error && <p className="inline-error" role="alert">{genres.error}</p>}
      {titles.error && <p className="inline-error" role="alert">{titles.error}</p>}
      {titles.loading ? (
        <p className="loading-message" role="status">Cargando catálogo...</p>
      ) : (
        <>
          <div className="table-scroll">
            <table className="catalog-table">
              <thead>
                <tr>
                  <th scope="col">Nombre</th>
                  <th scope="col">Año</th>
                  <th scope="col">Director</th>
                  <th scope="col">Género</th>
                  <th scope="col">Calificación</th>
                </tr>
              </thead>
              <tbody>
                {titles.data.content.map((title) => (
                  <tr key={title.id}>
                    <td>
                      <Link className="title-link" to={`/titles/${title.id}`}>{title.name}</Link>
                      <span className="title-type">
                        {title.type === 'MOVIE' ? 'Película' : 'Serie'}
                      </span>
                    </td>
                    <td>{title.releaseYear || '—'}</td>
                    <td>{getDirectors(title)}</td>
                    <td>{title.genres.map((genre) => genre.name).join(' / ') || '—'}</td>
                    <td>
                      <span aria-label={getRatingLabel(title.avgRating)} className="rating-value">
                        {title.avgRating ? `🍿 ${getRatingLabel(title.avgRating)}` : '—'}
                      </span>
                    </td>
                  </tr>
                ))}
                {!titles.data.content.length && (
                  <tr>
                    <td className="empty-table" colSpan="5">No se encontraron títulos.</td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
          <Pagination
            onPageChange={setPage}
            page={titles.data.number}
            totalPages={titles.data.totalPages}
          />
        </>
      )}
    </section>
  )
}

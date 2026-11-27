import { useState } from 'react'
import { Link, useLocation, useSearchParams } from 'react-router-dom'
import useApiResource from '../api/useApiResource.js'
import Pagination from '../components/Pagination.jsx'
import TmdbAttribution from '../components/TmdbAttribution.jsx'

function SearchForm({ query, onSearch }) {
  const [input, setInput] = useState(query)

  function search(event) {
    event.preventDefault()
    onSearch(input.trim())
  }

  return (
    <form className="tmdb-search" onSubmit={search}>
      <label htmlFor="tmdb-query">Película o serie</label>
      <input id="tmdb-query" maxLength={200} onChange={(event) => setInput(event.target.value)} type="search" value={input} />
      <button className="secondary-button" disabled={!input.trim()} type="submit">Buscar</button>
    </form>
  )
}

export default function TmdbSearchPage() {
  const [searchParams, setSearchParams] = useSearchParams()
  const location = useLocation()
  const query = (searchParams.get('query') || '').trim().slice(0, 200)
  const requestedPage = searchParams.get('page')
  const pageNumber = /^[1-9]\d*$/.test(requestedPage || '') ? Number(requestedPage) : 1
  const page = Number.isSafeInteger(pageNumber) && pageNumber <= 500 ? pageNumber - 1 : 0
  const endpoint = query ? `/api/tmdb/search?query=${encodeURIComponent(query)}&page=${page + 1}` : ''
  const results = useApiResource(endpoint, Boolean(query))

  function updateSearch(nextQuery, nextPage = 0) {
    setSearchParams(nextQuery ? { query: nextQuery, page: String(nextPage + 1) } : {})
  }

  const titles = (results.data?.results || []).filter(
    (item) => item.media_type === 'movie' || item.media_type === 'tv',
  )

  return (
    <section className="domain-page" aria-labelledby="tmdb-search-title">
      <div className="page-heading">
        <div>
          <h1 id="tmdb-search-title">Buscar en TMDB</h1>
          <p>Consulta películas y series en vivo sin modificar el catálogo existente.</p>
        </div>
      </div>
      <SearchForm key={query} onSearch={updateSearch} query={query} />
      {results.error && <p className="inline-error" role="alert">{results.error}</p>}
      {results.loading && <p className="loading-message" role="status">Buscando en TMDB...</p>}
      {results.data && !results.loading && (
        <>
          <div className="table-scroll">
            <table className="catalog-table">
              <thead><tr><th scope="col">Nombre</th><th scope="col">Tipo</th><th scope="col">Estreno</th></tr></thead>
              <tbody>
                {titles.map((title) => (
                  <tr key={`${title.media_type}-${title.id}`}>
                    <td><Link className="title-link" to={{ pathname: `/tmdb/${title.media_type}/${title.id}`, search: location.search }}>{title.title || title.name}</Link></td>
                    <td>{title.media_type === 'movie' ? 'Película' : 'Serie'}</td>
                    <td>{title.release_date || title.first_air_date || 'No disponible'}</td>
                  </tr>
                ))}
                {!titles.length && <tr><td className="empty-table" colSpan="3">No hay películas ni series en esta página.</td></tr>}
              </tbody>
            </table>
          </div>
          <Pagination onPageChange={(nextPage) => updateSearch(query, nextPage)} page={page} totalPages={Math.min(results.data.total_pages || 0, 500)} />
        </>
      )}
      <TmdbAttribution />
    </section>
  )
}

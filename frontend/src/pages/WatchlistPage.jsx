import { useState } from 'react'
import { Link } from 'react-router-dom'
import apiClient from '../api/client.js'
import usePagedData from '../api/usePagedData.js'
import ConfirmModal from '../components/ConfirmModal.jsx'
import Pagination from '../components/Pagination.jsx'

export default function WatchlistPage() {
  const [page, setPage] = useState(0)
  const [selectedTitle, setSelectedTitle] = useState(null)
  const [removing, setRemoving] = useState(false)
  const [error, setError] = useState('')
  const [notice, setNotice] = useState('')
  const watchlist = usePagedData('/api/watchlist', {
    page,
    size: 10,
    sort: 'addedAt,desc',
  })

  async function removeTitle() {
    setRemoving(true)
    setError('')
    try {
      await apiClient.delete(`/api/watchlist/${selectedTitle.id}`)
      setSelectedTitle(null)
      setNotice('El título se quitó de tu lista.')
      watchlist.reload()
    } catch (requestError) {
      setError(requestError.response?.data?.message || 'No se pudo quitar el título de tu lista.')
    } finally {
      setRemoving(false)
    }
  }

  return (
    <section className="domain-page" aria-labelledby="watchlist-title">
      <div className="page-heading">
        <div>
          <h1 id="watchlist-title">Mi lista</h1>
          <p>Los títulos que guardaste para ver después.</p>
        </div>
      </div>
      {notice && <p className="success-message" role="status">{notice}</p>}
      {error && <p className="inline-error" role="alert">{error}</p>}
      {watchlist.error && <p className="inline-error" role="alert">{watchlist.error}</p>}
      {watchlist.loading ? (
        <p className="loading-message" role="status">Cargando tu lista...</p>
      ) : (
        <>
          <div className="watchlist-grid">
            {watchlist.data.content.map(({ title, addedAt }) => (
              <article className="watchlist-card" key={title.id}>
                <Link to={`/titles/${title.id}`}>
                  {title.posterUrl ? (
                    <img alt={`Póster de ${title.name}`} src={title.posterUrl} />
                  ) : (
                    <span aria-hidden="true" className="poster-fallback">🍿</span>
                  )}
                  <span className="watchlist-card-copy">
                    <strong>{title.name}</strong>
                    <small>{title.type === 'MOVIE' ? 'Película' : 'Serie'}</small>
                  </span>
                </Link>
                <button
                  className="text-button"
                  onClick={() => setSelectedTitle(title)}
                  type="button"
                >
                  Quitar de mi lista
                </button>
                <time dateTime={addedAt}>
                  Agregado {new Date(addedAt).toLocaleDateString('es-MX')}
                </time>
              </article>
            ))}
          </div>
          {!watchlist.data.content.length && (
            <p className="empty-message">Tu lista está vacía. Agrega títulos desde su detalle.</p>
          )}
          <Pagination
            onPageChange={setPage}
            page={watchlist.data.number}
            totalPages={watchlist.data.totalPages}
          />
        </>
      )}
      {selectedTitle && (
        <ConfirmModal
          cancelLabel="Conservar"
          confirmLabel="Quitar"
          description={`¿Quieres quitar “${selectedTitle.name}” de tu lista?`}
          loading={removing}
          onCancel={() => setSelectedTitle(null)}
          onConfirm={removeTitle}
          title="Quitar de mi lista"
        />
      )}
    </section>
  )
}

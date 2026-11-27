import { useState } from 'react'
import { Link, useLocation, useParams } from 'react-router-dom'
import apiClient from '../api/client.js'
import useApiResource from '../api/useApiResource.js'
import usePagedData from '../api/usePagedData.js'
import { useAuth } from '../auth/useAuth.js'
import Pagination from '../components/Pagination.jsx'
import ReviewList from '../components/ReviewList.jsx'
import TmdbAttribution from '../components/TmdbAttribution.jsx'

export default function TmdbDetailPage() {
  const { type, id } = useParams()
  const location = useLocation()
  const { user } = useAuth()
  const endpoint = `/api/tmdb/${type}/${id}`
  const result = useApiResource(endpoint)
  const [imported, setImported] = useState(null)
  const localId = imported?.endpoint === endpoint ? imported.id : result.data?.localTitleId
  const [reviewPage, setReviewPage] = useState(0)
  const reviews = usePagedData(`/api/titles/${localId}/reviews`, {
    enabled: Boolean(localId), page: reviewPage, size: 5, sort: 'createdAt,desc',
  })
  const watchlist = usePagedData('/api/watchlist', {
    enabled: user?.role === 'USER' && Boolean(localId), page: 0, size: 100,
  })
  const [rating, setRating] = useState('')
  const [comment, setComment] = useState('')
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState('')
  const [notice, setNotice] = useState('')

  if (result.loading) return <p className="loading-message" role="status">Cargando título...</p>
  if (result.error) return <p className="inline-error" role="alert">{result.error}</p>
  if (!result.data?.title) return <p className="empty-message">No se encontró el título.</p>

  const title = result.data.title
  const name = title.title || title.name || 'Título sin nombre'
  const poster = /^\/[A-Za-z0-9._-]+$/.test(title.poster_path || '') ? `https://image.tmdb.org/t/p/w500${title.poster_path}` : ''
  const inWatchlist = watchlist.data.content.some((item) => item.title.id === localId)
  const directors = (title.credits?.crew || []).filter((person) => person.job === 'Director')
  const cast = (title.credits?.cast || []).slice(0, 10)

  async function addToWatchlist() {
    setBusy(true)
    setError('')
    setNotice('')
    try {
      const { data } = await apiClient.post(`${endpoint}/watchlist`)
      setImported({ endpoint, id: data.title.id })
      setNotice('El título se agregó a tu lista.')
      watchlist.reload()
    } catch (requestError) {
      setError(requestError.response?.data?.message || 'No se pudo agregar el título a tu lista.')
    } finally {
      setBusy(false)
    }
  }

  async function submitReview(event) {
    event.preventDefault()
    if (!rating || !comment.trim()) {
      setError('Selecciona una calificación y escribe tu reseña.')
      return
    }
    setBusy(true)
    setError('')
    setNotice('')
    try {
      const { data } = await apiClient.post(`${endpoint}/reviews`, { rating: Number(rating), comment: comment.trim() })
      setImported({ endpoint, id: data.titleId })
      setRating('')
      setComment('')
      setNotice('Tu reseña fue enviada y está pendiente de moderación.')
    } catch (requestError) {
      setError(requestError.response?.data?.message || 'No se pudo enviar la reseña.')
    } finally {
      setBusy(false)
    }
  }

  return (
    <section className="domain-page" aria-labelledby="tmdb-title-name">
      <Link className="back-link" to={{ pathname: '/tmdb', search: location.search }}>← Volver a la búsqueda</Link>
      <article className="title-detail">
        <div className="title-poster">
          {poster ? <img alt={`Póster de ${name}`} src={poster} /> : <span aria-hidden="true">🍿</span>}
        </div>
        <div className="title-detail-copy">
          <p className="eyebrow">{type === 'movie' ? 'Película' : 'Serie'} · {title.release_date || title.first_air_date || 'Fecha no disponible'}</p>
          <h1 id="tmdb-title-name">{name}</h1>
          <p>{title.overview || 'Sin sinopsis disponible.'}</p>
          <dl className="title-facts">
            <div><dt>Géneros</dt><dd>{(title.genres || []).map((genre) => genre.name).join(' / ') || 'No disponible'}</dd></div>
            {title.runtime > 0 && <div><dt>Duración</dt><dd>{title.runtime} min</dd></div>}
            {title.number_of_seasons > 0 && <div><dt>Temporadas</dt><dd>{title.number_of_seasons}</dd></div>}
          </dl>
          <p><strong>Dirección:</strong> {directors.map((person) => person.name).join(', ') || 'No disponible'}</p>
          <p><strong>Elenco:</strong> {cast.map((person) => person.character ? `${person.name} (${person.character})` : person.name).join(', ') || 'No disponible'}</p>
          {localId && <p><Link to={`/titles/${localId}`}>Ver ficha y calificación local</Link></p>}
          {user?.role === 'USER' && (
            <>
              {watchlist.error && <p className="inline-error" role="alert">{watchlist.error}</p>}
              <button className="secondary-button" disabled={busy || watchlist.loading || inWatchlist} onClick={addToWatchlist} type="button">
                {inWatchlist ? 'Ya está en tu lista' : 'Agregar a mi lista'}
              </button>
            </>
          )}
        </div>
      </article>
      {notice && <p className="success-message" role="status">{notice}</p>}
      {error && <p className="inline-error" role="alert">{error}</p>}
      <section className="reviews-section" aria-labelledby="tmdb-reviews-title">
        <h2 id="tmdb-reviews-title">Reseñas de BodrioPopcorn</h2>
        {reviews.error && <p className="inline-error" role="alert">{reviews.error}</p>}
        {reviews.loading ? <p className="loading-message" role="status">Cargando reseñas...</p> : (
          <><ReviewList reviews={reviews.data.content} /><Pagination onPageChange={setReviewPage} page={reviews.data.number} totalPages={reviews.data.totalPages} /></>
        )}
      </section>
      {user?.role === 'USER' && (
        <form className="review-form" onSubmit={submitReview}>
          <h2>Escribe tu reseña</h2>
          <label>Calificación
            <select onChange={(event) => setRating(event.target.value)} value={rating}>
              <option value="">Elige una calificación</option>
              {[1, 2, 3, 4, 5].map((value) => <option key={value} value={value}>{value} / 5</option>)}
            </select>
          </label>
          <label>Comentario
            <textarea maxLength={2000} onChange={(event) => setComment(event.target.value)} required rows={4} value={comment} />
          </label>
          <button className="primary-button" disabled={busy} type="submit">{busy ? 'Enviando...' : 'Enviar reseña'}</button>
        </form>
      )}
      <TmdbAttribution />
    </section>
  )
}

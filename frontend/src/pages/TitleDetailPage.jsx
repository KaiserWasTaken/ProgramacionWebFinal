import { useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import apiClient from '../api/client.js'
import useApiResource from '../api/useApiResource.js'
import usePagedData from '../api/usePagedData.js'
import Pagination from '../components/Pagination.jsx'
import ReviewList from '../components/ReviewList.jsx'
import { useAuth } from '../auth/useAuth.js'

export default function TitleDetailPage() {
  const { id } = useParams()
  const { user } = useAuth()
  const titleResult = useApiResource(`/api/titles/${id}`)
  const [reviewPage, setReviewPage] = useState(0)
  const reviews = usePagedData(`/api/titles/${id}/reviews`, {
    page: reviewPage,
    size: 5,
    sort: 'createdAt,desc',
  })
  const watchlist = usePagedData('/api/watchlist', {
    enabled: user?.role === 'USER',
    page: 0,
    size: 100,
  })
  const [rating, setRating] = useState('')
  const [comment, setComment] = useState('')
  const [submitting, setSubmitting] = useState(false)
  const [actionError, setActionError] = useState('')
  const [notice, setNotice] = useState('')

  if (titleResult.loading) return <p className="loading-message" role="status">Cargando título...</p>
  if (titleResult.error) return <p className="inline-error" role="alert">{titleResult.error}</p>

  const title = titleResult.data
  if (!title) return <p className="empty-message">No se encontró el título.</p>

  const inWatchlist = watchlist.data.content.some((item) => item.title.id === title.id)

  async function addToWatchlist() {
    setActionError('')
    setNotice('')
    try {
      await apiClient.post('/api/watchlist', { titleId: title.id })
      setNotice('El título se agregó a tu lista.')
      watchlist.reload()
    } catch (error) {
      setActionError(error.response?.data?.message || 'No se pudo agregar el título a tu lista.')
    }
  }

  async function submitReview(event) {
    event.preventDefault()
    setActionError('')
    setNotice('')

    if (!rating || Number(rating) < 1 || Number(rating) > 5 || !comment.trim()) {
      setActionError('Selecciona una calificación de 1 a 5 y escribe tu reseña.')
      return
    }

    setSubmitting(true)
    try {
      await apiClient.post('/api/reviews', {
        titleId: title.id,
        rating: Number(rating),
        comment: comment.trim(),
      })
      setRating('')
      setComment('')
      setNotice('Tu reseña fue enviada y está pendiente de moderación.')
    } catch (error) {
      setActionError(error.response?.data?.message || 'No se pudo enviar la reseña.')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <section className="domain-page" aria-labelledby="title-name">
      <Link className="back-link" to="/">← Volver al catálogo</Link>
      <article className="title-detail">
        <div className="title-poster">
          {title.posterUrl ? (
            <img alt={`Póster de ${title.name}`} src={title.posterUrl} />
          ) : (
            <span aria-hidden="true">🍿</span>
          )}
        </div>
        <div className="title-detail-copy">
          <p className="eyebrow">{title.type === 'MOVIE' ? 'Película' : 'Serie'} · {title.releaseYear}</p>
          <h1 id="title-name">{title.name}</h1>
          {title.tmdbId && (
            <p><Link to={`/tmdb/${title.tmdbMediaType}/${title.tmdbId}`}>Ver datos actualizados en TMDB</Link></p>
          )}
          <p>{title.synopsis}</p>
          <dl className="title-facts">
            <div><dt>Géneros</dt><dd>{title.genres.map((genre) => genre.name).join(' / ')}</dd></div>
            <div>
              <dt>Calificación</dt>
              <dd>{title.avgRating ? `${Number(title.avgRating).toFixed(1)} / 5` : 'Sin calificación'}</dd>
            </div>
            {title.durationMinutes && <div><dt>Duración</dt><dd>{title.durationMinutes} min</dd></div>}
            {title.seasons && <div><dt>Temporadas</dt><dd>{title.seasons}</dd></div>}
          </dl>
          <p>
            <strong>Dirección:</strong>{' '}
            {title.credits
              .filter((credit) => credit.person.roleType === 'DIRECTOR')
              .map((credit) => credit.person.name)
              .join(', ') || 'No disponible'}
          </p>
          <p>
            <strong>Elenco:</strong>{' '}
            {title.credits
              .filter((credit) => credit.person.roleType === 'ACTOR')
              .map((credit) => credit.characterName
                ? `${credit.person.name} (${credit.characterName})`
                : credit.person.name)
              .join(', ') || 'No disponible'}
          </p>
          {user?.role === 'USER' && (
            <>
              {watchlist.error && <p className="inline-error" role="alert">{watchlist.error}</p>}
              <button
                className="secondary-button"
                disabled={watchlist.loading || inWatchlist}
                onClick={addToWatchlist}
                type="button"
              >
                {inWatchlist ? 'Ya está en tu lista' : 'Agregar a mi lista'}
              </button>
            </>
          )}
        </div>
      </article>

      {notice && <p className="success-message" role="status">{notice}</p>}
      {actionError && <p className="inline-error" role="alert">{actionError}</p>}

      <section className="reviews-section" aria-labelledby="reviews-title">
        <h2 id="reviews-title">Reseñas</h2>
        {reviews.error && <p className="inline-error" role="alert">{reviews.error}</p>}
        {reviews.loading ? (
          <p className="loading-message" role="status">Cargando reseñas...</p>
        ) : (
          <>
            <ReviewList reviews={reviews.data.content} />
            <Pagination
              onPageChange={setReviewPage}
              page={reviews.data.number}
              totalPages={reviews.data.totalPages}
            />
          </>
        )}
      </section>

      {user?.role === 'USER' && (
        <form className="review-form" onSubmit={submitReview}>
          <h2>Escribe tu reseña</h2>
          <label>
            Calificación
            <select onChange={(event) => setRating(event.target.value)} value={rating}>
              <option value="">Elige una calificación</option>
              {[1, 2, 3, 4, 5].map((value) => (
                <option key={value} value={value}>{value} / 5</option>
              ))}
            </select>
          </label>
          <label>
            Comentario
            <textarea
              maxLength={2000}
              onChange={(event) => setComment(event.target.value)}
              required
              rows={4}
              value={comment}
            />
          </label>
          <button className="primary-button" disabled={submitting} type="submit">
            {submitting ? 'Enviando...' : 'Enviar reseña'}
          </button>
        </form>
      )}
    </section>
  )
}

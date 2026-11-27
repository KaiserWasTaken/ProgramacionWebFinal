import { useState } from 'react'
import apiClient from '../api/client.js'
import usePagedData from '../api/usePagedData.js'
import Pagination from '../components/Pagination.jsx'

export default function ModerationPage() {
  const [page, setPage] = useState(0)
  const [processingId, setProcessingId] = useState(null)
  const [error, setError] = useState('')
  const [notice, setNotice] = useState('')
  const reviews = usePagedData('/api/moderator/reviews/pending', {
    page,
    size: 10,
    sort: 'createdAt,asc',
  })

  async function moderate(review, status) {
    setProcessingId(review.id)
    setError('')
    setNotice('')
    try {
      await apiClient.patch(`/api/reviews/${review.id}/status`, { status })
      setNotice(`La reseña de ${review.titleName} fue ${status === 'APPROVED' ? 'aprobada' : 'rechazada'}.`)
      reviews.reload()
    } catch (requestError) {
      setError(requestError.response?.data?.message || 'No se pudo actualizar el estado de la reseña.')
    } finally {
      setProcessingId(null)
    }
  }

  return (
    <section className="domain-page" aria-labelledby="moderation-title">
      <div className="page-heading">
        <div>
          <h1 id="moderation-title">Moderación de reseñas</h1>
          <p>Revisa las reseñas pendientes antes de publicarlas.</p>
        </div>
      </div>
      {notice && <p className="success-message" role="status">{notice}</p>}
      {error && <p className="inline-error" role="alert">{error}</p>}
      {reviews.error && <p className="inline-error" role="alert">{reviews.error}</p>}
      {reviews.loading ? (
        <p className="loading-message" role="status">Cargando reseñas pendientes...</p>
      ) : (
        <>
          <div className="review-list">
            {reviews.data.content.map((review) => (
              <article className="review-card" key={review.id}>
                <div className="review-card-heading">
                  <strong>{review.titleName}</strong>
                  <span>{review.userEmail}</span>
                </div>
                <p>{review.comment}</p>
                <span aria-label={`${review.rating} de 5`}>{'🍿'.repeat(review.rating)}</span>
                <div className="review-actions">
                  <button
                    className="primary-button"
                    disabled={processingId === review.id}
                    onClick={() => moderate(review, 'APPROVED')}
                    type="button"
                  >
                    Aprobar
                  </button>
                  <button
                    className="danger-button"
                    disabled={processingId === review.id}
                    onClick={() => moderate(review, 'REJECTED')}
                    type="button"
                  >
                    Rechazar
                  </button>
                </div>
              </article>
            ))}
          </div>
          {!reviews.data.content.length && <p className="empty-message">No hay reseñas pendientes.</p>}
          <Pagination
            onPageChange={setPage}
            page={reviews.data.number}
            totalPages={reviews.data.totalPages}
          />
        </>
      )}
    </section>
  )
}

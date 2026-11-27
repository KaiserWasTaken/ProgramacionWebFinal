import { useState } from 'react'
import apiClient from '../api/client.js'
import usePagedData from '../api/usePagedData.js'
import ConfirmModal from '../components/ConfirmModal.jsx'
import Pagination from '../components/Pagination.jsx'

export default function MyReviewsPage() {
  const [page, setPage] = useState(0)
  const [editing, setEditing] = useState(null)
  const [deleting, setDeleting] = useState(null)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')
  const [notice, setNotice] = useState('')
  const reviews = usePagedData('/api/reviews/mine', { page, size: 10, sort: 'createdAt,desc' })

  async function saveReview(event) {
    event.preventDefault()
    setSaving(true)
    setError('')
    setNotice('')
    try {
      await apiClient.put(`/api/reviews/${editing.id}`, {
        rating: Number(editing.rating),
        comment: editing.comment.trim(),
      })
      setEditing(null)
      setNotice('Tu reseña fue actualizada.')
      reviews.reload()
    } catch (requestError) {
      setError(requestError.response?.data?.message || 'No se pudo actualizar la reseña.')
    } finally {
      setSaving(false)
    }
  }

  async function deleteReview() {
    setSaving(true)
    setError('')
    try {
      await apiClient.delete(`/api/reviews/${deleting.id}`)
      setDeleting(null)
      setNotice('Tu reseña fue eliminada.')
      reviews.reload()
    } catch (requestError) {
      setError(requestError.response?.data?.message || 'No se pudo eliminar la reseña.')
    } finally {
      setSaving(false)
    }
  }

  return (
    <section className="domain-page" aria-labelledby="my-reviews-title">
      <div className="page-heading">
        <div>
          <h1 id="my-reviews-title">Mis reseñas</h1>
          <p>Consulta y administra tus reseñas.</p>
        </div>
      </div>
      {notice && <p className="success-message" role="status">{notice}</p>}
      {error && <p className="inline-error" role="alert">{error}</p>}
      {reviews.error && <p className="inline-error" role="alert">{reviews.error}</p>}
      {reviews.loading ? (
        <p className="loading-message" role="status">Cargando tus reseñas...</p>
      ) : (
        <>
          <div className="review-list">
            {reviews.data.content.map((review) => (
              <article className="review-card" key={review.id}>
                <div className="review-card-heading">
                  <strong>{review.titleName}</strong>
                  <span className={`status-badge status-badge--${review.status.toLowerCase()}`}>
                    {review.status === 'PENDING' ? 'Pendiente'
                      : review.status === 'APPROVED' ? 'Aprobada' : 'Rechazada'}
                  </span>
                </div>
                <p>{review.comment}</p>
                <span aria-label={`${review.rating} de 5`}>{'🍿'.repeat(review.rating)}</span>
                <div className="review-actions">
                  <button
                    className="text-button"
                    onClick={() => setEditing({
                      id: review.id,
                      rating: String(review.rating),
                      comment: review.comment,
                    })}
                    type="button"
                  >
                    Editar
                  </button>
                  <button className="text-button text-button--danger" onClick={() => setDeleting(review)} type="button">
                    Eliminar
                  </button>
                </div>
              </article>
            ))}
          </div>
          {!reviews.data.content.length && <p className="empty-message">Aún no has escrito reseñas.</p>}
          <Pagination
            onPageChange={setPage}
            page={reviews.data.number}
            totalPages={reviews.data.totalPages}
          />
        </>
      )}
      {editing && (
        <div className="modal-backdrop">
          <form
            aria-labelledby="edit-review-title"
            aria-modal="true"
            className="confirm-modal edit-review-modal"
            onSubmit={saveReview}
            role="dialog"
          >
            <h2 id="edit-review-title">Editar reseña</h2>
            <label>
              Calificación
              <select
                onChange={(event) => setEditing((current) => ({ ...current, rating: event.target.value }))}
                required
                value={editing.rating}
              >
                {[1, 2, 3, 4, 5].map((rating) => (
                  <option key={rating} value={rating}>{rating} / 5</option>
                ))}
              </select>
            </label>
            <label>
              Comentario
              <textarea
                maxLength={2000}
                onChange={(event) => setEditing((current) => ({ ...current, comment: event.target.value }))}
                required
                rows={4}
                value={editing.comment}
              />
            </label>
            <div className="modal-actions">
              <button className="secondary-button" disabled={saving} onClick={() => setEditing(null)} type="button">
                Cancelar
              </button>
              <button className="primary-button" disabled={saving} type="submit">
                {saving ? 'Guardando...' : 'Guardar'}
              </button>
            </div>
          </form>
        </div>
      )}
      {deleting && (
        <ConfirmModal
          cancelLabel="Conservar"
          confirmLabel="Eliminar"
          description={`¿Eliminar tu reseña de “${deleting.titleName}”?`}
          loading={saving}
          onCancel={() => setDeleting(null)}
          onConfirm={deleteReview}
          title="Eliminar reseña"
        />
      )}
    </section>
  )
}

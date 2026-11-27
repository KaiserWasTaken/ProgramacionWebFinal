import { useState } from 'react'
import apiClient from '../api/client.js'
import useDebouncedValue from '../api/useDebouncedValue.js'
import usePagedData from '../api/usePagedData.js'
import ConfirmModal from '../components/ConfirmModal.jsx'
import Pagination from '../components/Pagination.jsx'

export default function AdminReviewsPage() {
  const [page, setPage] = useState(0)
  const [search, setSearch] = useState('')
  const [status, setStatus] = useState('')
  const [editing, setEditing] = useState(null)
  const [deleting, setDeleting] = useState(null)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')
  const [notice, setNotice] = useState('')
  const debouncedSearch = useDebouncedValue(search)
  const reviews = usePagedData('/api/reviews', {
    filters: { search: debouncedSearch, status },
    page,
    size: 10,
    sort: 'createdAt,desc',
  })

  async function saveReview(event) {
    event.preventDefault()
    setSaving(true)
    setError('')
    try {
      await apiClient.put(`/api/reviews/${editing.id}`, {
        rating: Number(editing.rating),
        comment: editing.comment.trim(),
      })
      setEditing(null)
      setNotice('La reseña se actualizó correctamente.')
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
      setNotice('La reseña se eliminó correctamente.')
      reviews.reload()
    } catch (requestError) {
      setError(requestError.response?.data?.message || 'No se pudo eliminar la reseña.')
    } finally {
      setSaving(false)
    }
  }

  async function updateStatus(review, nextStatus) {
    setSaving(true)
    setError('')
    try {
      await apiClient.patch(`/api/reviews/${review.id}/status`, { status: nextStatus })
      setNotice(`La reseña se marcó como ${nextStatus === 'APPROVED' ? 'aprobada' : 'rechazada'}.`)
      reviews.reload()
    } catch (requestError) {
      setError(requestError.response?.data?.message || 'No se pudo cambiar el estado.')
    } finally {
      setSaving(false)
    }
  }

  return (
    <section className="domain-page" aria-labelledby="admin-reviews-title">
      <div className="page-heading">
        <div>
          <h1 id="admin-reviews-title">Administrar reseñas</h1>
          <p>Consulta, edita y modera las reseñas.</p>
        </div>
      </div>
      <div className="catalog-filters">
        <label>
          <span className="visually-hidden">Buscar reseñas</span>
          <input onChange={(event) => { setPage(0); setSearch(event.target.value) }} placeholder="Buscar por título o correo..." type="search" value={search} />
        </label>
        <label>
          <span className="visually-hidden">Estado de reseña</span>
          <select onChange={(event) => { setPage(0); setStatus(event.target.value) }} value={status}>
            <option value="">Todos los estados</option>
            <option value="PENDING">Pendientes</option>
            <option value="APPROVED">Aprobadas</option>
            <option value="REJECTED">Rechazadas</option>
          </select>
        </label>
      </div>
      {notice && <p className="success-message" role="status">{notice}</p>}
      {error && <p className="inline-error" role="alert">{error}</p>}
      {reviews.error && <p className="inline-error" role="alert">{reviews.error}</p>}
      {reviews.loading ? (
        <p className="loading-message" role="status">Cargando reseñas...</p>
      ) : (
        <>
          <div className="table-scroll">
            <table className="catalog-table">
              <thead>
                <tr>
                  <th scope="col">Título</th>
                  <th scope="col">Usuario</th>
                  <th scope="col">Calificación</th>
                  <th scope="col">Comentario</th>
                  <th scope="col">Estado</th>
                  <th scope="col">Acciones</th>
                </tr>
              </thead>
              <tbody>
                {reviews.data.content.map((review) => (
                  <tr key={review.id}>
                    <td>{review.titleName}</td>
                    <td>{review.userEmail}</td>
                    <td>{review.rating} / 5</td>
                    <td className="review-comment-cell">{review.comment}</td>
                    <td>{review.status}</td>
                    <td className="table-actions">
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
                      {review.status !== 'APPROVED' && (
                        <button className="text-button" disabled={saving} onClick={() => updateStatus(review, 'APPROVED')} type="button">
                          Aprobar
                        </button>
                      )}
                      {review.status !== 'REJECTED' && (
                        <button className="text-button" disabled={saving} onClick={() => updateStatus(review, 'REJECTED')} type="button">
                          Rechazar
                        </button>
                      )}
                      <button className="text-button text-button--danger" onClick={() => setDeleting(review)} type="button">
                        Eliminar
                      </button>
                    </td>
                  </tr>
                ))}
                {!reviews.data.content.length && (
                  <tr><td className="empty-table" colSpan="6">No hay reseñas para mostrar.</td></tr>
                )}
              </tbody>
            </table>
          </div>
          <Pagination onPageChange={setPage} page={reviews.data.number} totalPages={reviews.data.totalPages} />
        </>
      )}
      {editing && (
        <div className="modal-backdrop">
          <form aria-labelledby="edit-admin-review-title" aria-modal="true" className="confirm-modal edit-review-modal" onSubmit={saveReview} role="dialog">
            <h2 id="edit-admin-review-title">Editar reseña</h2>
            <label>
              Calificación
              <select onChange={(event) => setEditing((current) => ({ ...current, rating: event.target.value }))} value={editing.rating}>
                {[1, 2, 3, 4, 5].map((rating) => <option key={rating} value={rating}>{rating} / 5</option>)}
              </select>
            </label>
            <label>
              Comentario
              <textarea maxLength={2000} onChange={(event) => setEditing((current) => ({ ...current, comment: event.target.value }))} required rows={4} value={editing.comment} />
            </label>
            <div className="modal-actions">
              <button className="secondary-button" disabled={saving} onClick={() => setEditing(null)} type="button">Cancelar</button>
              <button className="primary-button" disabled={saving} type="submit">{saving ? 'Guardando...' : 'Guardar'}</button>
            </div>
          </form>
        </div>
      )}
      {deleting && (
        <ConfirmModal
          cancelLabel="Conservar"
          confirmLabel="Eliminar"
          description={`¿Eliminar la reseña de “${deleting.titleName}” escrita por ${deleting.userEmail}?`}
          loading={saving}
          onCancel={() => setDeleting(null)}
          onConfirm={deleteReview}
          title="Eliminar reseña"
        />
      )}
    </section>
  )
}

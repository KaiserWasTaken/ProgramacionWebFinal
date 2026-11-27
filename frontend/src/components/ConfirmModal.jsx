export default function ConfirmModal({
  cancelLabel = 'Cancelar',
  confirmLabel = 'Confirmar',
  description,
  loading = false,
  onCancel,
  onConfirm,
  title,
}) {
  return (
    <div className="modal-backdrop">
      <section aria-labelledby="confirm-title" aria-modal="true" className="confirm-modal" role="dialog">
        <h2 id="confirm-title">{title}</h2>
        <p>{description}</p>
        <div className="modal-actions">
          <button className="secondary-button" disabled={loading} onClick={onCancel} type="button">
            {cancelLabel}
          </button>
          <button className="danger-button" disabled={loading} onClick={onConfirm} type="button">
            {loading ? 'Procesando...' : confirmLabel}
          </button>
        </div>
      </section>
    </div>
  )
}

export default function Pagination({ onPageChange, page, totalPages }) {
  if (totalPages <= 1) return null

  return (
    <nav aria-label="Paginación" className="pagination">
      <button disabled={page === 0} onClick={() => onPageChange(page - 1)} type="button">
        Anterior
      </button>
      <span>Página {page + 1} de {totalPages}</span>
      <button
        disabled={page + 1 >= totalPages}
        onClick={() => onPageChange(page + 1)}
        type="button"
      >
        Siguiente
      </button>
    </nav>
  )
}

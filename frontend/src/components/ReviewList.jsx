export default function ReviewList({ reviews }) {
  if (!reviews.length) {
    return <p className="empty-message">Todavía no hay reseñas aprobadas para este título.</p>
  }

  return (
    <div className="review-list">
      {reviews.map((review) => (
        <article className="review-card" key={review.id}>
          <div className="review-card-heading">
            <strong>{review.userEmail}</strong>
            <span aria-label={`${review.rating} de 5`}>{'🍿'.repeat(review.rating)}</span>
          </div>
          <p>{review.comment}</p>
          <time dateTime={review.createdAt}>
            {new Date(review.createdAt).toLocaleDateString('es-MX')}
          </time>
        </article>
      ))}
    </div>
  )
}

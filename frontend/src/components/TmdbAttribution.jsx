export default function TmdbAttribution() {
  return (
    <aside className="tmdb-attribution" aria-label="Créditos de TMDB">
      <a href="https://www.themoviedb.org" rel="noreferrer" target="_blank">
        <img
          alt="TMDB"
          src="https://www.themoviedb.org/assets/v4/logos/v2/blue_short-8e7b30f73a4020692ccca9c88bafe5dcb6f8a62a4c6bc55cd9ba82bb2cd95f6c.svg"
        />
      </a>
      <p>This product uses the TMDB API but is not endorsed or certified by TMDB.</p>
    </aside>
  )
}

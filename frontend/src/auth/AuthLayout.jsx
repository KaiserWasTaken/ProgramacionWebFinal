export default function AuthLayout({ children, reverse = false }) {
  return (
    <main className={`auth-layout${reverse ? ' auth-layout--reverse' : ''}`}>
      <section className="auth-brand" aria-label="Bodrio PopCorn">
        <h1>Bodrio PopCorn</h1>
        <div className="brand-mark">
          <svg viewBox="0 0 128 128" role="img" aria-label="Palomitas">
            <g fill="currentColor">
              <circle cx="64" cy="40" r="22" />
              <circle cx="43" cy="60" r="22" />
              <circle cx="84" cy="61" r="23" />
              <circle cx="56" cy="82" r="22" />
              <circle cx="75" cy="84" r="21" />
            </g>
            <path
              d="M68 28c-13 3-18 15-12 25m-27 8c-5 9 0 20 10 23m49-15c-10 1-15 9-12 18"
              fill="none"
              stroke="#103582"
              strokeLinecap="round"
              strokeWidth="7"
            />
          </svg>
        </div>
        <p>Hasta el mayor bodrio sabe mejor con palomitas</p>
      </section>
      <section className="auth-content">{children}</section>
    </main>
  )
}

import { useEffect, useRef, useState } from 'react'

const clientId = import.meta.env?.VITE_GOOGLE_CLIENT_ID
const scriptUrl = 'https://accounts.google.com/gsi/client'

export default function GoogleButton({ onCredential, text = 'signin_with' }) {
  const container = useRef(null)
  const callback = useRef(onCredential)
  const [error, setError] = useState('')

  useEffect(() => {
    callback.current = onCredential
  }, [onCredential])

  useEffect(() => {
    if (!clientId) return undefined
    let active = true
    let script = document.querySelector(`script[src="${scriptUrl}"]`)
    if (!script) {
      script = document.createElement('script')
      script.src = scriptUrl
      script.async = true
      document.head.appendChild(script)
    }
    function render() {
      if (!active || !container.current || !window.google?.accounts?.id) return
      window.google.accounts.id.initialize({
        client_id: clientId,
        callback: (response) => callback.current(response.credential),
      })
      window.google.accounts.id.renderButton(container.current, { type: 'standard', size: 'large', text })
    }
    function failed() {
      script.remove()
      if (active) setError('No se pudo cargar Google. Comprueba tu conexión.')
    }
    script.addEventListener('load', render)
    script.addEventListener('error', failed)
    render()
    return () => {
      active = false
      script.removeEventListener('load', render)
      script.removeEventListener('error', failed)
    }
  }, [text])

  if (!clientId) return null
  return (
    <div>
      <div ref={container} />
      {error && <p className="inline-error" role="alert">{error}</p>}
    </div>
  )
}

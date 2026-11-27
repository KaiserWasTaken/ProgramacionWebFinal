import { useState } from 'react'
import { Link } from 'react-router-dom'
import apiClient from '../api/client.js'
import AuthField from '../auth/AuthField.jsx'
import AuthLayout from '../auth/AuthLayout.jsx'
import { validateForgotPassword } from '../auth/validation.js'

export default function ForgotPasswordPage() {
  const [email, setEmail] = useState('')
  const [errors, setErrors] = useState({})
  const [touched, setTouched] = useState(false)
  const [attempted, setAttempted] = useState(false)
  const [submitting, setSubmitting] = useState(false)
  const [submitted, setSubmitted] = useState(false)
  const [formError, setFormError] = useState('')

  function handleChange(event) {
    setEmail(event.target.value)
    setErrors(validateForgotPassword({ email: event.target.value }))
    setFormError('')
    setSubmitted(false)
  }

  async function handleSubmit(event) {
    event.preventDefault()
    const nextErrors = validateForgotPassword({ email })
    setErrors(nextErrors)
    setAttempted(true)
    if (Object.keys(nextErrors).length) return

    setSubmitting(true)
    setFormError('')
    try {
      await apiClient.post('/api/auth/forgot-password', { email: email.trim() })
      setSubmitted(true)
    } catch (error) {
      setFormError(
        error.response?.data?.message ||
        (error.response
          ? 'No se pudo solicitar la recuperación. Inténtalo de nuevo.'
          : 'No se pudo conectar con el servidor.'),
      )
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <AuthLayout>
      <section className="auth-card" aria-labelledby="forgot-password-title">
        <h2 id="forgot-password-title">RECUPERAR CONTRASEÑA</h2>
        <p className="auth-subtitle">
          Te enviaremos un enlace si el correo pertenece a una cuenta.
        </p>
        {submitted && (
          <p className="form-success" role="status">
            Si la cuenta existe, enviaremos instrucciones para restablecer la contraseña.
          </p>
        )}
        {formError && <p className="form-error" role="alert">{formError}</p>}
        <form noValidate onSubmit={handleSubmit}>
          <AuthField
            autoComplete="email"
            error={attempted || touched ? errors.email : ''}
            id="forgot-password-email"
            label="Correo electrónico"
            name="email"
            onBlur={() => setTouched(true)}
            onChange={handleChange}
            placeholder="Correo electrónico"
            type="email"
            value={email}
          />
          <button className="primary-button" disabled={submitting} type="submit">
            {submitting ? 'Enviando...' : 'Enviar enlace'}
          </button>
        </form>
        <p className="auth-switch">
          <Link to="/login">Volver a iniciar sesión</Link>
        </p>
      </section>
    </AuthLayout>
  )
}

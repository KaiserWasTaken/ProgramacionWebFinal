import { useState } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import apiClient from '../api/client.js'
import AuthField from '../auth/AuthField.jsx'
import AuthLayout from '../auth/AuthLayout.jsx'
import { validatePasswordReset } from '../auth/validation.js'

export default function ResetPasswordPage() {
  const [searchParams] = useSearchParams()
  const token = searchParams.get('token') || ''
  const [values, setValues] = useState({ password: '', confirmPassword: '' })
  const [errors, setErrors] = useState({})
  const [touched, setTouched] = useState({})
  const [attempted, setAttempted] = useState(false)
  const [submitting, setSubmitting] = useState(false)
  const [submitted, setSubmitted] = useState(false)
  const [formError, setFormError] = useState('')

  function handleChange(event) {
    const nextValues = { ...values, [event.target.name]: event.target.value }
    setValues(nextValues)
    setErrors(validatePasswordReset({ ...nextValues, token }))
    setFormError('')
  }

  function handleBlur(event) {
    const field = event.target.name
    setTouched((current) => ({ ...current, [field]: true }))
    setErrors(validatePasswordReset({ ...values, token }))
  }

  async function handleSubmit(event) {
    event.preventDefault()
    const nextErrors = validatePasswordReset({ ...values, token })
    setErrors(nextErrors)
    setAttempted(true)
    if (Object.keys(nextErrors).length) return

    setSubmitting(true)
    setFormError('')
    try {
      await apiClient.post('/api/auth/reset-password', { token, password: values.password })
      setSubmitted(true)
    } catch (error) {
      setFormError(
        error.response?.data?.message ||
        (error.response
          ? 'No se pudo restablecer la contraseña. Solicita un enlace nuevo.'
          : 'No se pudo conectar con el servidor.'),
      )
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <AuthLayout>
      <section className="auth-card" aria-labelledby="reset-password-title">
        <h2 id="reset-password-title">CAMBIAR CONTRASEÑA</h2>
        <p className="auth-subtitle">Elige una contraseña segura para tu cuenta.</p>
        {submitted ? (
          <>
            <p className="form-success" role="status">Tu contraseña se actualizó correctamente.</p>
            <p className="auth-switch"><Link to="/login">Iniciar sesión</Link></p>
          </>
        ) : (
          <>
            {formError && <p className="form-error" role="alert">{formError}</p>}
            {!token && (
              <p className="form-error" role="alert">
                El enlace no es válido o está incompleto. Solicita uno nuevo.
              </p>
            )}
            <form noValidate onSubmit={handleSubmit}>
              <AuthField
                autoComplete="new-password"
                error={attempted || touched.password ? errors.password : ''}
                id="reset-password-new"
                label="Nueva contraseña"
                name="password"
                onBlur={handleBlur}
                onChange={handleChange}
                placeholder="Nueva contraseña"
                type="password"
                value={values.password}
              />
              <AuthField
                autoComplete="new-password"
                error={attempted || touched.confirmPassword ? errors.confirmPassword : ''}
                id="reset-password-confirm"
                label="Confirmar contraseña"
                name="confirmPassword"
                onBlur={handleBlur}
                onChange={handleChange}
                placeholder="Confirmar contraseña"
                type="password"
                value={values.confirmPassword}
              />
              <button className="primary-button" disabled={submitting || !token} type="submit">
                {submitting ? 'Guardando...' : 'Actualizar contraseña'}
              </button>
            </form>
            <p className="auth-switch">
              ¿Enlace vencido? <Link to="/forgot-password">Solicita otro</Link>
            </p>
          </>
        )}
      </section>
    </AuthLayout>
  )
}

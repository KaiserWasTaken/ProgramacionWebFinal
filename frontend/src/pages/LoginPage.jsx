import { useState } from 'react'
import { Link, Navigate, useLocation, useNavigate } from 'react-router-dom'
import AuthField from '../auth/AuthField.jsx'
import AuthLayout from '../auth/AuthLayout.jsx'
import GoogleButton from '../auth/GoogleButton.jsx'
import { useAuth } from '../auth/useAuth.js'
import { validateLogin } from '../auth/validation.js'

const initialValues = { email: '', password: '' }

export default function LoginPage() {
  const { authError, isAuthenticated, loading, login, googleLogin } = useAuth()
  const location = useLocation()
  const navigate = useNavigate()
  const [values, setValues] = useState(initialValues)
  const [errors, setErrors] = useState({})
  const [touched, setTouched] = useState({})
  const [attempted, setAttempted] = useState(false)
  const [rememberMe, setRememberMe] = useState(false)
  const [formError, setFormError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  if (loading) {
    return <main className="auth-loading" role="status">Validando sesión...</main>
  }
  if (isAuthenticated) return <Navigate to="/" replace />

  function handleChange(event) {
    const nextValues = { ...values, [event.target.name]: event.target.value }
    setValues(nextValues)
    setErrors(validateLogin(nextValues))
    setFormError('')
  }

  function handleBlur(event) {
    const field = event.target.name
    setTouched((current) => ({ ...current, [field]: true }))
    setErrors(validateLogin(values))
  }

  async function handleSubmit(event) {
    event.preventDefault()
    const nextErrors = validateLogin(values)
    setErrors(nextErrors)
    setAttempted(true)
    if (Object.keys(nextErrors).length) return

    setSubmitting(true)
    setFormError('')
    try {
      await login({
        email: values.email.trim(),
        password: values.password,
      }, rememberMe)
      const returnPath = location.state?.from?.pathname
      navigate(returnPath?.startsWith('/') && !returnPath.startsWith('//') ? returnPath : '/', {
        replace: true,
      })
    } catch (error) {
      const response = error.response?.data
      if (response?.fieldErrors) {
        setErrors((current) => ({ ...current, ...response.fieldErrors }))
      }
      setFormError(
        response?.message ||
        (error.response
          ? 'No se pudo iniciar sesión. Revisa tus datos e inténtalo de nuevo.'
          : 'No se pudo conectar con el servidor.'),
      )
    } finally {
      setSubmitting(false)
    }
  }

  const errorFor = (field) => (attempted || touched[field] ? errors[field] : '')

  async function handleGoogle(credential) {
    setFormError('')
    setSubmitting(true)
    try {
      await googleLogin(credential, rememberMe)
      const returnPath = location.state?.from?.pathname
      navigate(returnPath?.startsWith('/') && !returnPath.startsWith('//') ? returnPath : '/', { replace: true })
    } catch (error) {
      setFormError(error.response?.data?.message || 'No se pudo iniciar sesión con Google.')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <AuthLayout>
      <section className="auth-card" aria-labelledby="login-title">
        <h2 id="login-title">INICIAR SESIÓN</h2>
        <p className="auth-subtitle">Ingresa tus datos para acceder a la plataforma</p>
        {(location.state?.notice || authError || formError) && (
          <p className="form-error" role="alert">
            {formError || location.state?.notice || authError}
          </p>
        )}
        <form noValidate onSubmit={handleSubmit}>
          <AuthField
            autoComplete="email"
            error={errorFor('email')}
            id="login-email"
            label="Correo electrónico"
            name="email"
            onBlur={handleBlur}
            onChange={handleChange}
            placeholder="Correo electrónico"
            type="email"
            value={values.email}
          />
          <AuthField
            autoComplete="current-password"
            error={errorFor('password')}
            id="login-password"
            label="Contraseña"
            name="password"
            onBlur={handleBlur}
            onChange={handleChange}
            placeholder="Contraseña"
            type="password"
            value={values.password}
          />
          <div className="login-options">
            <label className="remember-option">
              <input
                checked={rememberMe}
                onChange={(event) => setRememberMe(event.target.checked)}
                type="checkbox"
              />
              <span>Recordarme</span>
            </label>
            <Link className="auth-text-link" to="/forgot-password">
              ¿Olvidaste tu contraseña?
            </Link>
          </div>
          <button className="primary-button" disabled={submitting} type="submit">
            {submitting ? 'Ingresando...' : 'Entrar'}
          </button>
        </form>
        <GoogleButton onCredential={handleGoogle} />
        <p className="auth-switch">
          ¿No tienes una cuenta? <Link to="/register">Regístrate aquí.</Link>
        </p>
      </section>
    </AuthLayout>
  )
}

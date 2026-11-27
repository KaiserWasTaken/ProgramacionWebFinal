import { useState } from 'react'
import { Link, Navigate, useNavigate } from 'react-router-dom'
import AuthField from '../auth/AuthField.jsx'
import AuthLayout from '../auth/AuthLayout.jsx'
import GoogleButton from '../auth/GoogleButton.jsx'
import { useAuth } from '../auth/useAuth.js'
import { validateRegistration } from '../auth/validation.js'

const initialValues = {
  fullName: '',
  email: '',
  password: '',
  confirmPassword: '',
}

export default function RegisterPage() {
  const { authError, isAuthenticated, loading, register, googleLogin } = useAuth()
  const navigate = useNavigate()
  const [values, setValues] = useState(initialValues)
  const [errors, setErrors] = useState({})
  const [touched, setTouched] = useState({})
  const [attempted, setAttempted] = useState(false)
  const [formError, setFormError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  if (loading) {
    return <main className="auth-loading" role="status">Validando sesión...</main>
  }
  if (isAuthenticated) return <Navigate to="/" replace />

  function handleChange(event) {
    const nextValues = { ...values, [event.target.name]: event.target.value }
    setValues(nextValues)
    setErrors(validateRegistration(nextValues))
    setFormError('')
  }

  function handleBlur(event) {
    const field = event.target.name
    setTouched((current) => ({ ...current, [field]: true }))
    setErrors(validateRegistration(values))
  }

  async function handleSubmit(event) {
    event.preventDefault()
    const nextErrors = validateRegistration(values)
    setErrors(nextErrors)
    setAttempted(true)
    if (Object.keys(nextErrors).length) return

    setSubmitting(true)
    setFormError('')
    try {
      await register({
        email: values.email.trim(),
        fullName: values.fullName.trim(),
        password: values.password,
      })
      navigate('/', { replace: true })
    } catch (error) {
      const response = error.response?.data
      if (response?.fieldErrors) {
        setErrors((current) => ({ ...current, ...response.fieldErrors }))
      }
      setFormError(
        response?.message ||
        (error.response
          ? 'No se pudo crear la cuenta. Revisa tus datos e inténtalo de nuevo.'
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
      await googleLogin(credential)
      navigate('/', { replace: true })
    } catch (error) {
      setFormError(error.response?.data?.message || 'No se pudo ingresar con Google.')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <AuthLayout reverse>
      <section className="auth-card auth-card--register" aria-labelledby="register-title">
        <h2 id="register-title">REGISTRAR CUENTA</h2>
        <p className="auth-subtitle">Ingresa tus datos para acceder a la plataforma</p>
        {(authError || formError) && (
          <p className="form-error" role="alert">{formError || authError}</p>
        )}
        <form noValidate onSubmit={handleSubmit}>
          <AuthField
            autoComplete="name"
            error={errorFor('fullName')}
            id="register-name"
            label="Nombre completo"
            name="fullName"
            onBlur={handleBlur}
            onChange={handleChange}
            placeholder="Nombre completo"
            value={values.fullName}
          />
          <AuthField
            autoComplete="email"
            error={errorFor('email')}
            id="register-email"
            label="Correo electrónico"
            name="email"
            onBlur={handleBlur}
            onChange={handleChange}
            placeholder="Correo electrónico"
            type="email"
            value={values.email}
          />
          <AuthField
            autoComplete="new-password"
            error={errorFor('password')}
            id="register-password"
            label="Contraseña"
            name="password"
            onBlur={handleBlur}
            onChange={handleChange}
            placeholder="Contraseña"
            type="password"
            value={values.password}
          />
          <AuthField
            autoComplete="new-password"
            error={errorFor('confirmPassword')}
            id="register-confirm-password"
            label="Confirmar contraseña"
            name="confirmPassword"
            onBlur={handleBlur}
            onChange={handleChange}
            placeholder="Confirmar contraseña"
            type="password"
            value={values.confirmPassword}
          />
          <button className="primary-button" disabled={submitting} type="submit">
            {submitting ? 'Creando cuenta...' : 'Crear cuenta'}
          </button>
        </form>
        <GoogleButton onCredential={handleGoogle} text="signup_with" />
        <p className="auth-switch">
          ¿Ya tienes una cuenta? <Link to="/login">Inicia aquí.</Link>
        </p>
      </section>
    </AuthLayout>
  )
}

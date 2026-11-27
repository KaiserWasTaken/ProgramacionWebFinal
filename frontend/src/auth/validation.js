const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
const REGISTRATION_PASSWORD_PATTERN = /^(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9]).{8,72}$/

export function validateProfilePhone(phone) {
  const normalizedPhone = phone.trim()
  if (normalizedPhone && !/^\+[1-9][0-9]{1,14}$/.test(normalizedPhone)) {
    return 'Ingresa un teléfono internacional con + y código de país, por ejemplo +525512345678.'
  }
  return ''
}

export function validateLogin({ email = '', password = '' } = {}) {
  const errors = {}
  const normalizedEmail = email.trim()

  if (!normalizedEmail) {
    errors.email = 'El correo electrónico es obligatorio.'
  } else if (normalizedEmail.length > 254 || !EMAIL_PATTERN.test(normalizedEmail)) {
    errors.email = 'Ingresa un correo electrónico válido.'
  }

  if (!password) {
    errors.password = 'La contraseña es obligatoria.'
  }

  return errors
}

export function validateRegistration(values = {}) {
  const { email = '', fullName = '', password = '', confirmPassword = '' } = values
  const errors = validateLogin({ email, password })
  const normalizedName = fullName.trim()

  if (!normalizedName) {
    errors.fullName = 'El nombre es obligatorio.'
  } else if (normalizedName.length > 120) {
    errors.fullName = 'El nombre no puede superar 120 caracteres.'
  }

  if (password && !REGISTRATION_PASSWORD_PATTERN.test(password)) {
    errors.password = 'Usa al menos 8 caracteres, una mayúscula, un número y un carácter especial (máximo 72).'
  }

  if (!confirmPassword) {
    errors.confirmPassword = 'Confirma tu contraseña.'
  } else if (password !== confirmPassword) {
    errors.confirmPassword = 'Las contraseñas no coinciden.'
  }

  return errors
}

export function validateForgotPassword({ email = '' } = {}) {
  const errors = {}
  const normalizedEmail = email.trim()

  if (!normalizedEmail) {
    errors.email = 'El correo electrónico es obligatorio.'
  } else if (normalizedEmail.length > 254 || !EMAIL_PATTERN.test(normalizedEmail)) {
    errors.email = 'Ingresa un correo electrónico válido.'
  }

  return errors
}

export function validatePasswordReset({
  token = '',
  password = '',
  confirmPassword = '',
} = {}) {
  const errors = {}

  if (!token.trim() || token.length > 100) {
    errors.token = 'El enlace de recuperación no es válido.'
  }
  if (!password) {
    errors.password = 'La contraseña es obligatoria.'
  } else if (!REGISTRATION_PASSWORD_PATTERN.test(password)) {
    errors.password = 'Usa al menos 8 caracteres, una mayúscula, un número y un carácter especial (máximo 72).'
  }
  if (!confirmPassword) {
    errors.confirmPassword = 'Confirma tu contraseña.'
  } else if (password !== confirmPassword) {
    errors.confirmPassword = 'Las contraseñas no coinciden.'
  }

  return errors
}
